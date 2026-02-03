package com.example.sagivproject.services.interfaces;

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
    /**
     * generate a new id for a new user in the database
     *
     * @return a new id for the user
     */
    String generateUserId();

    /**
     * create a new user in the database
     *
     * @param user     the user object to create
     * @param callback the callback to call when the operation is completed
     */
    void createNewUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback);

    /**
     * get a user from the database
     *
     * @param uid      the id of the user to get
     * @param callback the callback to call when the operation is completed
     */
    void getUser(@NonNull String uid, @NonNull DatabaseCallback<User> callback);

    /**
     * get all the users from the database
     *
     * @param callback the callback to call when the operation is completed
     */
    void getUserList(@NonNull DatabaseCallback<List<User>> callback);

    /**
     * delete a user from the database
     *
     * @param uid      the user id to delete
     * @param callback the callback to call when the operation is completed
     */
    void deleteUser(@NonNull String uid, @Nullable DatabaseCallback<Void> callback);

    /**
     * get a user by email and password
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @param callback the callback to call when the operation is completed
     */
    void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseCallback<User> callback);

    /**
     * check if an email already exists in the database
     *
     * @param email    the email to check
     * @param callback the callback to call when the operation is completed
     */
    void checkIfEmailExists(@NonNull String email, @NonNull DatabaseCallback<Boolean> callback);

    /**
     * update a user in the database
     *
     * @param user     the user object to update
     * @param callback the callback to call when the operation is completed
     */
    void updateUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback);

    /**
     * update only the admin status of a user
     *
     * @param uid      user id
     * @param role     new role
     * @param callback result callback
     */
    void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseCallback<Void> callback);

    /**
     * create a new medication in the database
     *
     * @param uid        the id of the user
     * @param medication the medication object to create
     * @param callback   the callback to call when the operation is completed
     */
    void createNewMedication(@NonNull String uid, @NonNull Medication medication, @Nullable DatabaseCallback<Void> callback);

    /**
     * get all the medications of a specific user
     *
     * @param uid      the id of the user
     * @param callback the callback
     */
    void getUserMedicationList(@NonNull String uid, @NonNull DatabaseCallback<List<Medication>> callback);

    /**
     * generate a new id for a medication under a specific user
     *
     * @param uid the id of the user
     * @return a new id for the medication
     */
    String generateMedicationId(@NonNull String uid);

    /**
     * delete a medication from the database
     *
     * @param uid          user id
     * @param medicationId id to delete
     * @param callback     callback
     */
    void deleteMedication(@NonNull String uid, @NonNull String medicationId, @Nullable DatabaseCallback<Void> callback);

    /**
     * update a medication in the database
     *
     * @param uid        user id
     * @param medication medication to update
     * @param callback   callback
     */
    void updateMedication(String uid, Medication medication, @Nullable DatabaseCallback<Void> callback);

    /**
     * generate a new id for a new forum message
     *
     * @return a new id for the forum message
     */
    String generateForumMessageId();

    /**
     * send a new message to the forum
     *
     * @param message  the ForumMessage object to send
     * @param callback the callback to call when the operation is completed
     */
    void sendForumMessage(ForumMessage message, DatabaseCallback<Void> callback);

    /**
     * get all forum messages in realtime (live updates)
     *
     * @param callback the callback that will receive a List<ForumMessage> when data changes
     */
    void getForumMessagesRealtime(DatabaseCallback<List<ForumMessage>> callback);

    /**
     * delete a specific forum message from the database
     *
     * @param messageId the id of the forum message to delete
     * @param callback  the callback to call when the operation is completed
     */
    void deleteForumMessage(@NonNull String messageId, @Nullable DatabaseCallback<Void> callback);

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
    ValueEventListener listenToRoomStatus(@NonNull String roomId, @NonNull RoomStatusCallback callback);

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
     * get all images from the database
     *
     * @param callback the callback to call when the operation is completed
     */
    void getAllImages(DatabaseCallback<List<ImageData>> callback);

    /**
     * create a new image in the database
     *
     * @param image    the image object to create
     * @param callback the callback to call when the operation is completed
     */
    void createImage(@NonNull ImageData image, @Nullable DatabaseCallback<Void> callback);

    /**
     * update all images in the database
     *
     * @param list     the list of images to update
     * @param callback the callback to call when the operation is completed
     */
    void updateAllImages(List<ImageData> list, DatabaseCallback<Void> callback);

    /**
     * add a correct answer to the user's stats
     *
     * @param uid the UID of the user
     */
    void addCorrectAnswer(String uid);

    /**
     * add a wrong answer to the user's stats
     *
     * @param uid the UID of the user
     */
    void addWrongAnswer(String uid);

    /**
     * reset math problems statistics for a user
     *
     * @param uid the UID of the user
     */
    void resetMathStats(@NonNull String uid);

    /**
     * callback interface for database operations
     *
     * @param <T> the type of the object to return
     */
    interface DatabaseCallback<T> {
        /**
         * called when the operation is completed successfully
         *
         * @param object the object to return
         */
        void onCompleted(T object);

        /**
         * called when the operation fails
         *
         * @param e the exception that was thrown
         */
        void onFailed(Exception e);
    }

    /**
     * callback interface for realtime room status updates
     */
    interface RoomStatusCallback {
        /**
         * called when the room status changes to "playing"
         *
         * @param room the updated GameRoom object
         */
        void onRoomStarted(GameRoom room);

        /**
         * called when the room no longer exists in the database
         */
        void onRoomDeleted();

        /**
         * called when the listener fails due to a database error
         *
         * @param e the exception describing the failure
         */
        void onFailed(Exception e);
    }
}