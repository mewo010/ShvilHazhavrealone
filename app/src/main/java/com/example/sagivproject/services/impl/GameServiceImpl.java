package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.IGameService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

/**
 * An implementation of the {@link IGameService} interface for managing the memory game.
 * <p>
 * This service handles all aspects of the game lifecycle, including matchmaking (finding or creating rooms),
 * managing game state in real-time, handling player moves, updating scores, and determining the winner.
 * It also manages listeners for game state changes and handles player disconnections.
 * </p>
 */
public class GameServiceImpl extends BaseDatabaseService<GameRoom> implements IGameService {
    private static final String ROOMS_PATH = "rooms";
    private static final String USERS_PATH = "users";
    private final DatabaseReference roomsReference;
    private final DatabaseReference usersReference;
    private ValueEventListener activeGameListener;

    /**
     * Constructs a new GameServiceImpl.
     *
     * @param firebaseDatabase The FirebaseDatabase instance.
     */
    @Inject
    public GameServiceImpl(FirebaseDatabase firebaseDatabase) {
        super(ROOMS_PATH, GameRoom.class);
        this.roomsReference = firebaseDatabase.getReference(ROOMS_PATH);
        this.usersReference = firebaseDatabase.getReference(USERS_PATH);
    }

    /**
     * Finds an available waiting game room or creates a new one for the user.
     *
     * @param user     The user looking for a game.
     * @param callback The callback to be invoked with the resulting game room.
     */
    @Override
    public void findOrCreateRoom(User user, DatabaseCallback<GameRoom> callback) {
        if (user == null) {
            callback.onFailed(new IllegalArgumentException("User cannot be null"));
            return;
        }

        roomsReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                for (MutableData roomData : currentData.getChildren()) {
                    GameRoom room = roomData.getValue(GameRoom.class);

                    if (room != null && "waiting".equals(room.getStatus()) && room.getPlayer2() == null) {
                        room.setPlayer2(user);
                        room.setStatus("playing");
                        roomData.setValue(room);
                        return Transaction.success(currentData);
                    }
                }

                String newRoomId = roomsReference.push().getKey();
                GameRoom newRoom = new GameRoom(newRoomId, user);
                currentData.child(Objects.requireNonNull(newRoomId)).setValue(newRoom);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (!committed || error != null) {
                    callback.onFailed(error != null ? error.toException() : new Exception("Failed to find or create a room."));
                    return;
                }

                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    GameRoom room = roomSnapshot.getValue(GameRoom.class);
                    if (room != null && (user.equals(room.getPlayer1()) || user.equals(room.getPlayer2()))) {
                        callback.onCompleted(room);
                        return;
                    }
                }
                callback.onFailed(new Exception("Could not find the room after transaction."));
            }
        });
    }

    /**
     * Retrieves a list of all game rooms with real-time updates.
     *
     * @param callback The callback to be invoked with the list of game rooms.
     */
    @Override
    public void getAllRoomsRealtime(@NonNull DatabaseCallback<List<GameRoom>> callback) {
        roomsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<GameRoom> roomList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    GameRoom room = child.getValue(GameRoom.class);
                    if (room != null) {
                        roomList.add(room);
                    }
                }
                callback.onCompleted(roomList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        });
    }

    /**
     * Listens for status changes in a specific game room.
     *
     * @param roomId   The ID of the room to listen to.
     * @param callback The callback to handle room status changes.
     * @return The {@link ValueEventListener} used for listening.
     */
    @Override
    public ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull IRoomStatusCallback callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onRoomDeleted();
                    return;
                }

                GameRoom room = snapshot.getValue(GameRoom.class);
                if (room == null) return;

                if ("playing".equals(room.getStatus())) {
                    callback.onRoomStarted(room);
                } else if ("finished".equals(room.getStatus())) {
                    callback.onRoomFinished(room);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        };

        roomsReference.child(roomId).addValueEventListener(listener);
        return listener;
    }

    /**
     * Removes a previously attached room status listener.
     *
     * @param roomId   The ID of the room.
     * @param listener The listener to remove.
     */
    @Override
    public void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener) {
        roomsReference.child(roomId).removeEventListener(listener);
    }

    /**
     * Cancels and deletes a waiting game room.
     *
     * @param roomId   The ID of the room to cancel.
     * @param callback The callback to be invoked on completion.
     */
    @Override
    public void cancelRoom(@NonNull String roomId, @Nullable DatabaseCallback<Void> callback) {
        roomsReference.child(roomId).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                GameRoom room = currentData.getValue(GameRoom.class);
                if (room != null && "waiting".equals(room.getStatus()) && room.getPlayer2() == null) {
                    currentData.setValue(null); // Delete the room
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (callback != null) {
                    if (error != null) {
                        callback.onFailed(error.toException());
                    } else {
                        callback.onCompleted(null);
                    }
                }
            }
        });
    }

    /**
     * Initializes the game board with cards and sets the first turn.
     *
     * @param roomId       The ID of the room.
     * @param cards        The list of cards for the game board.
     * @param firstTurnUid The UID of the player who takes the first turn.
     * @param callback     The callback to be invoked on completion.
     */
    @Override
    public void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseCallback<Void> callback) {
        Map<String, Object> roomUpdates = new HashMap<>();
        roomUpdates.put("cards", cards);
        roomUpdates.put("currentTurnUid", firstTurnUid);
        roomUpdates.put("status", "playing");

        roomsReference.child(roomId).updateChildren(roomUpdates, (error, ref) -> {
            if (callback != null) {
                if (error != null) {
                    callback.onFailed(error.toException());
                } else {
                    callback.onCompleted(null);
                }
            }
        });
    }

    /**
     * Listens for real-time updates to the entire game room state.
     *
     * @param roomId   The ID of the room to listen to.
     * @param callback The callback to be invoked with the updated game room.
     */
    @Override
    public void listenToGame(String roomId, DatabaseCallback<GameRoom> callback) {
        stopListeningToGame(roomId);
        activeGameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onCompleted(null); // Room was deleted
                    return;
                }
                GameRoom room = snapshot.getValue(GameRoom.class);
                callback.onCompleted(room);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        };
        roomsReference.child(roomId).addValueEventListener(activeGameListener);
    }

    /**
     * Stops listening for game state updates for a specific room.
     *
     * @param roomId The ID of the room to stop listening to.
     */
    @Override
    public void stopListeningToGame(String roomId) {
        if (activeGameListener != null) {
            roomsReference.child(roomId).removeEventListener(activeGameListener);
            activeGameListener = null;
        }
    }

    /**
     * Updates a specific field within a game room.
     *
     * @param roomId The ID of the room.
     * @param field  The name of the field to update.
     * @param value  The new value for the field.
     */
    @Override
    public void updateRoomField(String roomId, String field, Object value) {
        roomsReference.child(roomId).child(field).setValue(value);
    }

    /**
     * Updates the status (revealed and matched) of a specific card on the board.
     *
     * @param roomId   The ID of the room.
     * @param index    The index of the card in the list.
     * @param revealed The new revealed status.
     * @param matched  The new matched status.
     */
    @Override
    public void updateCardStatus(String roomId, int index, boolean revealed, boolean matched) {
        DatabaseReference cardRef = roomsReference.child(roomId).child("cards").child(String.valueOf(index));
        cardRef.child("isRevealed").setValue(revealed);
        cardRef.child("isMatched").setValue(matched);
    }

    /**
     * Sets a flag to indicate that a match check is in progress, preventing other moves.
     *
     * @param roomId       The ID of the room.
     * @param isProcessing The processing status.
     */
    @Override
    public void setProcessing(String roomId, boolean isProcessing) {
        updateRoomField(roomId, "processingMatch", isProcessing);
    }

    /**
     * Increments the win count for a user.
     *
     * @param uid The UID of the user who won.
     */
    @Override
    public void addUserWin(String uid) {
        if (uid == null || uid.isEmpty() || uid.equals("draw")) return;

        usersReference.child(uid).child("countWins").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Integer currentWins = currentData.getValue(Integer.class);
                currentData.setValue(currentWins == null ? 1 : currentWins + 1);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Transaction complete. No action needed.
            }
        });
    }

    /**
     * Sets up an onDisconnect handler to automatically end the game if the current player disconnects.
     *
     * @param roomId      The ID of the room.
     * @param opponentUid The UID of the opponent, who will be declared the winner.
     */
    @Override
    public void setupForfeitOnDisconnect(String roomId, String opponentUid) {
        DatabaseReference roomRef = roomsReference.child(roomId);
        roomRef.child("status").onDisconnect().setValue("finished");
        roomRef.child("winnerUid").onDisconnect().setValue(opponentUid);
    }

    /**
     * Removes the onDisconnect handler for the game room.
     *
     * @param roomId The ID of the room.
     */
    @Override
    public void removeForfeitOnDisconnect(String roomId) {
        DatabaseReference roomRef = roomsReference.child(roomId);
        roomRef.child("status").onDisconnect().cancel();
        roomRef.child("winnerUid").onDisconnect().cancel();
    }
}
