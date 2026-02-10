package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.IGameService;
import com.example.sagivproject.services.RoomStatusCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class GameServiceImpl extends BaseDatabaseService<GameRoom> implements IGameService {
    private static final String ROOMS_PATH = "rooms";
    private static final String USERS_PATH = "users";
    private final DatabaseReference roomsReference;
    private final DatabaseReference usersReference;
    private ValueEventListener activeGameListener;

    @Inject
    public GameServiceImpl(FirebaseDatabase firebaseDatabase) {
        super(ROOMS_PATH, GameRoom.class);
        this.roomsReference = firebaseDatabase.getReference(ROOMS_PATH);
        this.usersReference = firebaseDatabase.getReference(USERS_PATH);
    }

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

                // Find the room that the user is in
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

    @Override
    public ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull RoomStatusCallback callback) {
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

    @Override
    public void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener) {
        roomsReference.child(roomId).removeEventListener(listener);
    }

    @Override
    public void cancelRoom(@NonNull String roomId, @Nullable DatabaseCallback<Void> callback) {
        deleteData(roomId, callback);
    }

    @Override
    public void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseCallback<Void> callback) {
        roomsReference.child(roomId).child("cards").setValue(cards);
        roomsReference.child(roomId).child("currentTurnUid").setValue(firstTurnUid);
        roomsReference.child(roomId).child("status").setValue("playing", (error, ref) -> {
            if (callback != null) {
                if (error != null) {
                    callback.onFailed(error.toException());
                } else {
                    callback.onCompleted(null);
                }
            }
        });
    }

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

    @Override
    public void stopListeningToGame(String roomId) {
        if (activeGameListener != null) {
            roomsReference.child(roomId).removeEventListener(activeGameListener);
            activeGameListener = null;
        }
    }

    @Override
    public void updateRoomField(String roomId, String field, Object value) {
        roomsReference.child(roomId).child(field).setValue(value);
    }

    @Override
    public void updateCardStatus(String roomId, int index, boolean revealed, boolean matched) {
        DatabaseReference cardRef = roomsReference.child(roomId).child("cards").child(String.valueOf(index));
        cardRef.child("isRevealed").setValue(revealed);
        cardRef.child("isMatched").setValue(matched);
    }

    @Override
    public void setProcessing(String roomId, boolean isProcessing) {
        updateRoomField(roomId, "processingMatch", isProcessing);
    }

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

    @Override
    public void setupForfeitOnDisconnect(String roomId, String opponentUid) {
        DatabaseReference roomRef = roomsReference.child(roomId);
        roomRef.child("status").onDisconnect().setValue("finished");
        roomRef.child("winnerUid").onDisconnect().setValue(opponentUid);
    }

    @Override
    public void removeForfeitOnDisconnect(String roomId) {
        DatabaseReference roomRef = roomsReference.child(roomId);
        roomRef.child("status").onDisconnect().cancel();
        roomRef.child("winnerUid").onDisconnect().cancel();
    }
}