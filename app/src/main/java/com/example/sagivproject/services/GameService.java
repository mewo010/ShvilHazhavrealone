package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IGameService;
import com.example.sagivproject.services.interfaces.RoomStatusCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class GameService implements IGameService {

    private static final String ROOMS_PATH = "rooms";
    private final DatabaseReference databaseReference;
    private ValueEventListener activeGameListener;

    @Inject
    public GameService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(ROOMS_PATH);
    }

    @Override
    public void findOrCreateRoom(User user, DatabaseCallback<GameRoom> callback) {
        String newRoomId = databaseReference.push().getKey();
        final String[] matchedRoomId = new String[1];

        databaseReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                for (MutableData roomData : currentData.getChildren()) {
                    GameRoom room = roomData.getValue(GameRoom.class);

                    if (room != null && "waiting".equals(room.getStatus()) && room.getPlayer2() == null) {
                        room.setPlayer2(user);
                        room.setStatus("playing");
                        roomData.setValue(room);
                        matchedRoomId[0] = room.getRoomId();
                        return Transaction.success(currentData);
                    }
                }

                GameRoom newRoom = new GameRoom(newRoomId, user);
                currentData.child(Objects.requireNonNull(newRoomId)).setValue(newRoom);
                matchedRoomId[0] = newRoomId;
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot snapshot) {
                if (!committed || error != null) {
                    callback.onFailed(error != null ? error.toException() : new Exception("Match failed"));
                    return;
                }

                if (matchedRoomId[0] != null) {
                    GameRoom room = snapshot.child(matchedRoomId[0]).getValue(GameRoom.class);
                    callback.onCompleted(room);
                }
            }
        });
    }

    @Override
    public void getAllRoomsRealtime(@NonNull DatabaseCallback<List<GameRoom>> callback) {
        databaseReference.addValueEventListener(new ValueEventListener() {
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        };

        databaseReference.child(roomId).addValueEventListener(listener);
        return listener;
    }

    @Override
    public void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener) {
        databaseReference.child(roomId).removeEventListener(listener);
    }

    @Override
    public void cancelRoom(@NonNull String roomId, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(roomId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseCallback<Void> callback) {
        databaseReference.child(roomId).child("cards").setValue(cards);
        databaseReference.child(roomId).child("currentTurnUid").setValue(firstTurnUid);
        databaseReference.child(roomId).child("status").setValue("playing",
                (error, ref) -> {
                    if (callback == null) return;
                    if (error != null) callback.onFailed(error.toException());
                    else callback.onCompleted(null);
                });
    }

    @Override
    public void listenToGame(String roomId, DatabaseCallback<GameRoom> callback) {
        stopListeningToGame(roomId);

        activeGameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameRoom room = snapshot.getValue(GameRoom.class);
                callback.onCompleted(room);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        };

        databaseReference.child(roomId).addValueEventListener(activeGameListener);
    }

    @Override
    public void stopListeningToGame(String roomId) {
        if (activeGameListener != null) {
            databaseReference.child(roomId).removeEventListener(activeGameListener);
            activeGameListener = null;
        }
    }

    @Override
    public void updateRoomField(String roomId, String field, Object value) {
        databaseReference.child(roomId).child(field).setValue(value);
    }

    @Override
    public void updateCardStatus(String roomId, int index, boolean revealed, boolean matched) {
        databaseReference.child(roomId).child("cards").child(String.valueOf(index)).child("isRevealed").setValue(revealed);
        databaseReference.child(roomId).child("cards").child(String.valueOf(index)).child("isMatched").setValue(matched);
    }

    @Override
    public void setProcessing(String roomId, boolean isProcessing) {
        updateRoomField(roomId, "processingMatch", isProcessing);
    }

    @Override
    public void addUserWin(String uid) {
        // This logic should be in UserService
    }

    @Override
    public void setupForfeitOnDisconnect(String roomId, String opponentUid) {
        databaseReference.child(roomId).child("status").onDisconnect().setValue("finished");
        databaseReference.child(roomId).child("winnerUid").onDisconnect().setValue(opponentUid);
    }

    @Override
    public void removeForfeitOnDisconnect(String roomId) {
        databaseReference.child(roomId).child("status").onDisconnect().cancel();
        databaseReference.child(roomId).child("winnerUid").onDisconnect().cancel();
    }
}
