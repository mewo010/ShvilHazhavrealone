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

public class DatabaseServiceAdapter implements IDatabaseService {

    private final DatabaseService databaseService;

    public DatabaseServiceAdapter() {
        this.databaseService = DatabaseService.getInstance();
    }

    @Override
    public String generateUserId() {
        return databaseService.generateUserId();
    }

    @Override
    public void createNewUser(@NonNull User user, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.createNewUser(user, callback);
    }

    @Override
    public void getUser(@NonNull String uid, @NonNull DatabaseService.DatabaseCallback<User> callback) {
        databaseService.getUser(uid, callback);
    }

    @Override
    public void getUserList(@NonNull DatabaseService.DatabaseCallback<List<User>> callback) {
        databaseService.getUserList(callback);
    }

    @Override
    public void deleteUser(@NonNull String uid, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.deleteUser(uid, callback);
    }

    @Override
    public void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseService.DatabaseCallback<User> callback) {
        databaseService.getUserByEmailAndPassword(email, password, callback);
    }

    @Override
    public void checkIfEmailExists(@NonNull String email, @NonNull DatabaseService.DatabaseCallback<Boolean> callback) {
        databaseService.checkIfEmailExists(email, callback);
    }

    @Override
    public void updateUser(@NonNull User user, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.updateUser(user, callback);
    }

    @Override
    public void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.updateUserRole(uid, role, callback);
    }

    @Override
    public void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.createNewMedication(uid, medication, callback);
    }

    @Override
    public void getUserMedicationList(@NonNull String uid, @NonNull DatabaseService.DatabaseCallback<List<Medication>> callback) {
        databaseService.getUserMedicationList(uid, callback);
    }

    @Override
    public String generateMedicationId(@NonNull String uid) {
        return databaseService.generateMedicationId(uid);
    }

    @Override
    public void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.deleteMedication(uid, medicationId, callback);
    }

    @Override
    public void updateMedication(String uid, Medication medication, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.updateMedication(uid, medication, callback);
    }

    @Override
    public String generateForumMessageId() {
        return databaseService.generateForumMessageId();
    }

    @Override
    public void sendForumMessage(ForumMessage message, DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.sendForumMessage(message, callback);
    }

    @Override
    public void getForumMessagesRealtime(DatabaseService.DatabaseCallback<List<ForumMessage>> callback) {
        databaseService.getForumMessagesRealtime(callback);
    }

    @Override
    public void deleteForumMessage(@NonNull String messageId, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.deleteForumMessage(messageId, callback);
    }

    @Override
    public void findOrCreateRoom(User user, DatabaseService.DatabaseCallback<GameRoom> callback) {
        databaseService.findOrCreateRoom(user, callback);
    }

    @Override
    public void getAllRoomsRealtime(@NonNull DatabaseService.DatabaseCallback<List<GameRoom>> callback) {
        databaseService.getAllRoomsRealtime(callback);
    }

    @Override
    public ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull DatabaseService.RoomStatusCallback callback) {
        return databaseService.listenToRoomStatus(roomId, callback);
    }

    @Override
    public void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener) {
        databaseService.removeRoomListener(roomId, listener);
    }

    @Override
    public void cancelRoom(@NonNull String roomId, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.cancelRoom(roomId, callback);
    }

    @Override
    public void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.initGameBoard(roomId, cards, firstTurnUid, callback);
    }

    @Override
    public void listenToGame(String roomId, DatabaseService.DatabaseCallback<GameRoom> callback) {
        databaseService.listenToGame(roomId, callback);
    }

    @Override
    public void stopListeningToGame(String roomId) {
        databaseService.stopListeningToGame(roomId);
    }

    @Override
    public void updateRoomField(String roomId, String field, Object value) {
        databaseService.updateRoomField(roomId, field, value);
    }

    @Override
    public void updateCardStatus(String roomId, int index, boolean revealed, boolean matched) {
        databaseService.updateCardStatus(roomId, index, revealed, matched);
    }

    @Override
    public void setProcessing(String roomId, boolean isProcessing) {
        databaseService.setProcessing(roomId, isProcessing);
    }

    @Override
    public void addUserWin(String uid) {
        databaseService.addUserWin(uid);
    }

    @Override
    public void setupForfeitOnDisconnect(String roomId, String opponentUid) {
        databaseService.setupForfeitOnDisconnect(roomId, opponentUid);
    }

    @Override
    public void removeForfeitOnDisconnect(String roomId) {
        databaseService.removeForfeitOnDisconnect(roomId);
    }

    @Override
    public void getAllImages(DatabaseService.DatabaseCallback<List<ImageData>> callback) {
        databaseService.getAllImages(callback);
    }

    @Override
    public void createImage(@NonNull ImageData image, @Nullable DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.createImage(image, callback);
    }

    @Override
    public void updateAllImages(List<ImageData> list, DatabaseService.DatabaseCallback<Void> callback) {
        databaseService.updateAllImages(list, callback);
    }

    @Override
    public void addCorrectAnswer(String uid) {
        databaseService.addCorrectAnswer(uid);
    }

    @Override
    public void addWrongAnswer(String uid) {
        databaseService.addWrongAnswer(uid);
    }

    @Override
    public void resetMathStats(@NonNull String uid) {
        databaseService.resetMathStats(uid);
    }
}
