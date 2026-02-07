package com.example.sagivproject.services;

import com.example.sagivproject.models.GameRoom;

/**
 * A callback interface for monitoring the status of a game room.
 */
public interface RoomStatusCallback {
    /**
     * Called when the game room has started.
     *
     * @param room The game room that has started.
     */
    void onRoomStarted(GameRoom room);

    /**
     * Called when the game room has been deleted.
     */
    void onRoomDeleted();

    /**
     * Called when the game room has finished.
     *
     */
    void onRoomFinished(GameRoom ignoredRoom);

    /**
     * Called when an error occurs while monitoring the game room.
     *
     * @param ignoredE The exception that occurred.
     */
    void onFailed(Exception ignoredE);
}
