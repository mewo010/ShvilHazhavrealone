package com.example.sagivproject.services.interfaces;

import com.example.sagivproject.models.GameRoom;

public interface RoomStatusCallback {
    void onRoomStarted(GameRoom room);

    void onRoomDeleted();

    void onFailed(Exception e);
}
