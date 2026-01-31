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
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public interface IDatabaseService {
    String generateUserId();

    void createNewUser(@NonNull User user, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void getUser(@NonNull String uid, @NonNull DatabaseService.DatabaseCallback<User> callback);

    void getUserList(@NonNull DatabaseService.DatabaseCallback<List<User>> callback);

    void deleteUser(@NonNull String uid, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseService.DatabaseCallback<User> callback);

    void checkIfEmailExists(@NonNull String email, @NonNull DatabaseService.DatabaseCallback<Boolean> callback);

    void updateUser(@NonNull User user, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void getUserMedicationList(@NonNull String uid, @NonNull DatabaseService.DatabaseCallback<List<Medication>> callback);

    String generateMedicationId(@NonNull String uid);

    void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void updateMedication(String uid, Medication medication, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    String generateForumMessageId();

    void sendForumMessage(ForumMessage message, DatabaseService.DatabaseCallback<Void> callback);

    void getForumMessagesRealtime(DatabaseService.DatabaseCallback<List<ForumMessage>> callback);

    void deleteForumMessage(@NonNull String messageId, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void findOrCreateRoom(User user, DatabaseService.DatabaseCallback<GameRoom> callback);

    void getAllRoomsRealtime(@NonNull DatabaseService.DatabaseCallback<List<GameRoom>> callback);

    ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull DatabaseService.RoomStatusCallback callback);

    void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener);

    void cancelRoom(@NonNull String roomId, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseService.DatabaseCallback<Void> callback);

    void listenToGame(String roomId, DatabaseService.DatabaseCallback<GameRoom> callback);

    void stopListeningToGame(String roomId);

    void updateRoomField(String roomId, String field, Object value);

    void updateCardStatus(String roomId, int index, boolean revealed, boolean matched);

    void setProcessing(String roomId, boolean isProcessing);

    void addUserWin(String uid);

    void setupForfeitOnDisconnect(String roomId, String opponentUid);

    void removeForfeitOnDisconnect(String roomId);

    void getAllImages(DatabaseService.DatabaseCallback<List<ImageData>> callback);

    void createImage(@NonNull ImageData image, @Nullable DatabaseService.DatabaseCallback<Void> callback);

    void updateAllImages(List<ImageData> list, DatabaseService.DatabaseCallback<Void> callback);

    void addCorrectAnswer(String uid);

    void addWrongAnswer(String uid);

    void resetMathStats(@NonNull String uid);
}
