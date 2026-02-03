package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IDatabaseService.RoomStatusCallback;
import com.example.sagivproject.services.interfaces.IGameService;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import javax.inject.Inject;

public class GameService implements IGameService {
    private final IDatabaseService databaseService;

    @Inject
    public GameService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void findOrCreateRoom(User user, DatabaseCallback<GameRoom> callback) {
        databaseService.findOrCreateRoom(user, callback);
    }

    @Override
    public void getAllRoomsRealtime(@NonNull DatabaseCallback<List<GameRoom>> callback) {
        databaseService.getAllRoomsRealtime(callback);
    }

    @Override
    public ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull RoomStatusCallback callback) {
        return databaseService.listenToRoomStatus(roomId, callback);
    }

    @Override
    public void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener) {
        databaseService.removeRoomListener(roomId, listener);
    }

    @Override
    public void cancelRoom(@NonNull String roomId, @Nullable DatabaseCallback<Void> callback) {
        databaseService.cancelRoom(roomId, callback);
    }

    @Override
    public void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseCallback<Void> callback) {
        databaseService.initGameBoard(roomId, cards, firstTurnUid, callback);
    }

    @Override
    public void listenToGame(String roomId, DatabaseCallback<GameRoom> callback) {
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
}
