package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.UnaryOperator;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseService implements IDatabaseService {
    private static final String USERS_PATH = "users",
            FORUM_PATH = "forum",
            ROOMS_PATH = "rooms",
            IMAGES_PATH = "images";
    private final DatabaseReference databaseReference;
    private ValueEventListener activeGameListener;

    @Inject
    public DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable IDatabaseService.DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    private void deleteData(@NotNull final String path, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }

    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final IDatabaseService.DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final IDatabaseService.DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            });

            callback.onCompleted(tList);
        });
    }

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }

    private <T> void runTransaction(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull UnaryOperator<T> function, @NotNull final IDatabaseService.DatabaseCallback<T> callback) {
        readData(path).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                T currentValue = currentData.getValue(clazz);
                if (currentValue == null) {
                    currentValue = function.apply(null);
                } else {
                    currentValue = function.apply(currentValue);
                }
                currentData.setValue(currentValue);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    callback.onFailed(error.toException());
                    return;
                }
                T result = currentData != null ? currentData.getValue(clazz) : null;
                callback.onCompleted(result);
            }
        });

    }

    @Override
    public String generateUserId() {
        return generateNewId(USERS_PATH);
    }

    @Override
    public void createNewUser(@NotNull final User user, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + user.getUid(), user, callback);
    }

    @Override
    public void getUser(@NotNull final String uid, @NotNull final IDatabaseService.DatabaseCallback<User> callback) {
        getData(USERS_PATH + "/" + uid, User.class, callback);
    }

    @Override
    public void getUserList(@NotNull final IDatabaseService.DatabaseCallback<List<User>> callback) {
        getDataList(USERS_PATH, User.class, callback);
    }

    @Override
    public void deleteUser(@NotNull final String uid, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid, callback);
    }

    @Override
    public void getUserByEmailAndPassword(@NotNull final String email, @NotNull final String password, @NotNull final IDatabaseService.DatabaseCallback<User> callback) {
        Query query = readData(USERS_PATH).orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && Objects.equals(user.getPassword(), password)) {
                            callback.onCompleted(user);
                            return;
                        }
                    }
                }
                callback.onCompleted(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        });
    }

    @Override
    public void checkIfEmailExists(@NotNull final String email, @NotNull final IDatabaseService.DatabaseCallback<Boolean> callback) {
        Query query = readData(USERS_PATH).orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCompleted(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        });
    }

    @Override
    public void updateUser(@NotNull final User user, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        runTransaction(USERS_PATH + "/" + user.getUid(), User.class, currentUser -> user, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(User object) {
                if (callback != null) {
                    callback.onCompleted(null);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }

    @Override
    public void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable IDatabaseService.DatabaseCallback<Void> callback) {
        readData(USERS_PATH + "/" + uid + "/role").setValue(role, (error, ref) -> {
            if (error != null) {
                if (callback != null) callback.onFailed(error.toException());
            } else {
                if (callback != null) callback.onCompleted(null);
            }
        });
    }

    @Override
    public void createNewMedication(@NotNull final String uid, @NotNull final Medication medication, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + uid + "/medications/" + medication.getId(), medication, callback);
    }

    @Override
    public void getUserMedicationList(@NotNull final String uid, @NotNull final IDatabaseService.DatabaseCallback<List<Medication>> callback) {
        getDataList(USERS_PATH + "/" + uid + "/medications", Medication.class, callback);
    }

    @Override
    public String generateMedicationId(@NotNull final String uid) {
        return generateNewId(USERS_PATH + "/" + uid + "/medications");
    }

    @Override
    public void deleteMedication(@NotNull final String uid, @NotNull final String medicationId, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        deleteData(USERS_PATH + "/" + uid + "/medications/" + medicationId, callback);
    }

    @Override
    public void updateMedication(String uid, Medication medication, @Nullable IDatabaseService.DatabaseCallback<Void> callback) {
        writeData(USERS_PATH + "/" + uid + "/medications/" + medication.getId(), medication, callback);
    }

    @Override
    public String generateForumMessageId() {
        return generateNewId(FORUM_PATH);
    }

    @Override
    public void sendForumMessage(ForumMessage message, IDatabaseService.DatabaseCallback<Void> callback) {
        writeData(FORUM_PATH + "/" + message.getMessageId(), message, callback);
    }

    @Override
    public void getForumMessagesRealtime(IDatabaseService.DatabaseCallback<List<ForumMessage>> callback) {
        readData(FORUM_PATH)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ForumMessage> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ForumMessage msg = child.getValue(ForumMessage.class);
                            list.add(msg);
                        }
                        callback.onCompleted(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailed(error.toException());
                    }
                });
    }

    @Override
    public void deleteForumMessage(@NotNull final String messageId, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        deleteData(FORUM_PATH + "/" + messageId, callback);
    }

    @Override
    public void findOrCreateRoom(User user, IDatabaseService.DatabaseCallback<GameRoom> callback) {
        String newRoomId = generateNewId(ROOMS_PATH);
        final String[] matchedRoomId = new String[1];

        readData(ROOMS_PATH).runTransaction(new Transaction.Handler() {
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
                currentData.child(newRoomId).setValue(newRoom);
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
    public void getAllRoomsRealtime(@NotNull final IDatabaseService.DatabaseCallback<List<GameRoom>> callback) {
        readData(ROOMS_PATH).addValueEventListener(new ValueEventListener() {
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
    public ValueEventListener listenToRoomStatus(
            @NotNull String roomId,
            @NotNull IDatabaseService.RoomStatusCallback callback) {
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

        readData(ROOMS_PATH + "/" + roomId).addValueEventListener(listener);
        return listener;
    }

    @Override
    public void removeRoomListener(@NotNull String roomId,
                                   @NotNull ValueEventListener listener) {
        readData(ROOMS_PATH + "/" + roomId).removeEventListener(listener);
    }

    @Override
    public void cancelRoom(@NotNull String roomId,
                           @Nullable IDatabaseService.DatabaseCallback<Void> callback) {
        deleteData(ROOMS_PATH + "/" + roomId, callback);
    }

    @Override
    public void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, IDatabaseService.DatabaseCallback<Void> callback) {
        readData(ROOMS_PATH + "/" + roomId + "/cards").setValue(cards);
        readData(ROOMS_PATH + "/" + roomId + "/currentTurnUid").setValue(firstTurnUid);
        readData(ROOMS_PATH + "/" + roomId + "/status").setValue("playing",
                (error, ref) -> {
                    if (callback == null) return;
                    if (error != null) callback.onFailed(error.toException());
                    else callback.onCompleted(null);
                });
    }

    @Override
    public void listenToGame(String roomId, IDatabaseService.DatabaseCallback<GameRoom> callback) {
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

        readData(ROOMS_PATH + "/" + roomId).addValueEventListener(activeGameListener);
    }

    @Override
    public void stopListeningToGame(String roomId) {
        if (activeGameListener != null) {
            readData(ROOMS_PATH + "/" + roomId).removeEventListener(activeGameListener);
            activeGameListener = null;
        }
    }

    @Override
    public void updateRoomField(String roomId, String field, Object value) {
        readData(ROOMS_PATH + "/" + roomId + "/" + field).setValue(value);
    }

    @Override
    public void updateCardStatus(String roomId, int index, boolean revealed, boolean matched) {
        readData(ROOMS_PATH + "/" + roomId + "/cards/" + index + "/isRevealed").setValue(revealed);
        readData(ROOMS_PATH + "/" + roomId + "/cards/" + index + "/isMatched").setValue(matched);
    }

    @Override
    public void setProcessing(String roomId, boolean isProcessing) {
        updateRoomField(roomId, "processingMatch", isProcessing);
    }

    @Override
    public void addUserWin(String uid) {
        if (uid == null || uid.isEmpty() || uid.equals("draw")) return;

        runTransaction(USERS_PATH + "/" + uid + "/countWins", Integer.class,
                currentWins -> (currentWins == null) ? 1 : currentWins + 1,
                new IDatabaseService.DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Integer object) {
                    }

                    @Override
                    public void onFailed(Exception e) {
                    }
                });
    }

    @Override
    public void setupForfeitOnDisconnect(String roomId, String opponentUid) {
        readData(ROOMS_PATH + "/" + roomId + "/status").onDisconnect().setValue("finished");
        readData(ROOMS_PATH + "/" + roomId + "/winnerUid").onDisconnect().setValue(opponentUid);
    }

    @Override
    public void removeForfeitOnDisconnect(String roomId) {
        readData(ROOMS_PATH + "/" + roomId + "/status").onDisconnect().cancel();
        readData(ROOMS_PATH + "/" + roomId + "/winnerUid").onDisconnect().cancel();
    }

    @Override
    public void getAllImages(IDatabaseService.DatabaseCallback<List<ImageData>> callback) {
        getDataList(IMAGES_PATH, ImageData.class, callback);
    }

    @Override
    public void createImage(@NonNull ImageData image, @Nullable IDatabaseService.DatabaseCallback<Void> callback) {
        writeData(IMAGES_PATH + "/" + image.getId(), image, callback);
    }

    @Override
    public void updateAllImages(List<ImageData> list, IDatabaseService.DatabaseCallback<Void> callback) {
        readData(IMAGES_PATH).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < list.size(); i++) {
                    ImageData img = list.get(i);
                    writeData(IMAGES_PATH + "/" + img.getId(), img, null);
                }
                if (callback != null) callback.onCompleted(null);
            } else if (callback != null) {
                callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void addCorrectAnswer(String uid) {
        runTransaction(USERS_PATH + "/" + uid + "/mathProblemsStats/correctAnswers", Integer.class,
                current -> (current == null) ? 1 : current + 1, new IDatabaseService.DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Integer object) {
                    }

                    @Override
                    public void onFailed(Exception e) {
                    }
                });
    }

    @Override
    public void addWrongAnswer(String uid) {
        runTransaction(USERS_PATH + "/" + uid + "/mathProblemsStats/wrongAnswers", Integer.class,
                current -> (current == null) ? 1 : current + 1, new IDatabaseService.DatabaseCallback<>() {
                    @Override
                    public void onCompleted(Integer object) {
                    }

                    @Override
                    public void onFailed(Exception e) {
                    }
                });
    }

    @Override
    public void resetMathStats(@NonNull String uid) {
        readData(USERS_PATH + "/" + uid + "/mathProblemsStats/correctAnswers").setValue(0);
        readData(USERS_PATH + "/" + uid + "/mathProblemsStats/wrongAnswers").setValue(0);
    }
}