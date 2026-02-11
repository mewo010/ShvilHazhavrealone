package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Card;
import com.example.sagivproject.models.GameRoom;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public interface IGameService {
    /**
     * find an existing waiting room or create a new one if none is available
     *
     * @param user     the user who wants to join or create a game room
     * @param callback callback that returns the matched or newly created GameRoom
     */
    void findOrCreateRoom(User user, DatabaseCallback<GameRoom> callback);

    /**
     * Listen to all game rooms in real-time
     *
     * @param callback the callback that will receive the updated list of rooms
     */
    void getAllRoomsRealtime(@NonNull DatabaseCallback<List<GameRoom>> callback);

    /**
     * listen in realtime to changes in a specific room status
     *
     * @param roomId   the id of the room to listen to
     * @param callback callback to notify about room start, deletion or errors
     * @return the ValueEventListener instance so it can later be removed
     */
    ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull IRoomStatusCallback callback);

    /**
     * remove a previously registered room status listener
     *
     * @param roomId   the id of the room
     * @param listener the listener instance returned from listenToRoomStatus
     */
    void removeRoomListener(@NonNull String roomId, @NonNull ValueEventListener listener);

    /**
     * cancel and delete a game room from the database
     *
     * @param roomId   the id of the room to cancel
     * @param callback optional callback for success or failure
     */
    void cancelRoom(@NonNull String roomId, @Nullable DatabaseCallback<Void> callback);

    /**
     * initialize the game board data for a room
     *
     * @param roomId       the id of the game room
     * @param cards        the shuffled list of cards for the game
     * @param firstTurnUid the UID of the player who starts the game
     * @param callback     callback for success or failure
     */
    void initGameBoard(String roomId, List<Card> cards, String firstTurnUid, DatabaseCallback<Void> callback);

    /**
     * listen in realtime to all changes in a game room
     *
     * @param roomId   the id of the game room
     * @param callback callback that receives updated GameRoom objects
     */
    void listenToGame(String roomId, DatabaseCallback<GameRoom> callback);

    /**
     * stop listening to realtime game updates
     *
     * @param roomId the id of the game room
     */
    void stopListeningToGame(String roomId);

    /**
     * update a single field inside a game room
     *
     * @param roomId the id of the game room
     * @param field  the field name to update
     * @param value  the new value for the field
     */
    void updateRoomField(String roomId, String field, Object value);

    /**
     * update the reveal and match state of a specific card in the game board
     *
     * @param roomId   the id of the game room
     * @param index    the index of the card in the cards list
     * @param revealed whether the card is currently revealed
     * @param matched  whether the card has been successfully matched
     */
    void updateCardStatus(String roomId, int index, boolean revealed, boolean matched);

    /**
     * set the processing state of the game
     *
     * @param roomId       the id of the game room
     * @param isProcessing true if the game is currently processing a move
     */
    void setProcessing(String roomId, boolean isProcessing);

    /**
     * increment the win counter of a user
     *
     * @param uid the UID of the winning user
     */
    void addUserWin(String uid);

    /**
     * define automatic forfeit behavior when a player disconnects unexpectedly
     *
     * @param roomId      the id of the game room
     * @param opponentUid the UID of the opponent who will win by forfeit
     */
    void setupForfeitOnDisconnect(String roomId, String opponentUid);

    /**
     * cancel previously defined onDisconnect forfeit actions
     *
     * @param roomId the id of the game room
     */
    void removeForfeitOnDisconnect(String roomId);

    /**
     * A callback interface for monitoring the status of a game room.
     */
    interface IRoomStatusCallback {
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
}
