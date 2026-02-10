package com.example.sagivproject.services;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.util.List;

/**
 * An interface for managing forum messages.
 */
public interface IForumService {
    /**
     * Sends a message to the forum.
     *
     * @param user       The user sending the message.
     * @param text       The content of the message.
     * @param categoryId The ID of the category to send the message to.
     * @param callback   A callback to be invoked when the operation is complete.
     */
    void sendMessage(User user, String text, String categoryId, DatabaseCallback<Void> callback);

    /**
     * Listens for real-time updates to forum messages.
     *
     * @param categoryId The ID of the category to listen to.
     * @param callback   A callback to be invoked with the list of messages.
     */
    void listenToMessages(String categoryId, DatabaseCallback<List<ForumMessage>> callback);

    /**
     * Deletes a message from the forum.
     *
     * @param messageId The ID of the message to delete.
     * @param callback  A callback to be invoked when the operation is complete.
     */
    void deleteMessage(@NonNull String messageId, String categoryId, DatabaseCallback<Void> callback);
}
