package com.example.sagivproject.services.interfaces;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.ForumService;

import java.util.List;

public interface IForumService {
    void sendMessage(User user, String text, ForumService.ForumCallback<Void> callback);

    void listenToMessages(ForumService.ForumCallback<List<ForumMessage>> callback);

    void deleteMessage(@NonNull String messageId, ForumService.ForumCallback<Void> callback);
}
