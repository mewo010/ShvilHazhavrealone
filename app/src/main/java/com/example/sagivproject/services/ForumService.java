package com.example.sagivproject.services;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.util.List;

import javax.inject.Inject;

public class ForumService {
    private final IDatabaseService databaseService;

    @Inject
    public ForumService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void sendMessage(User user, String text, ForumCallback<Void> callback) {
        String messageId = databaseService.generateForumMessageId();
        ForumMessage msg = new ForumMessage(messageId, user.getFullName(), user.getEmail(), text, System.currentTimeMillis(), user.getUid(), user.isAdmin());

        databaseService.sendForumMessage(msg, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                if (callback != null) {
                    callback.onSuccess(object);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public void listenToMessages(ForumCallback<List<ForumMessage>> callback) {
        databaseService.getForumMessagesRealtime(new DatabaseCallback<>() {
            @Override
            public void onCompleted(List<ForumMessage> data) {
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public void deleteMessage(@NonNull String messageId, ForumCallback<Void> callback) {
        databaseService.deleteForumMessage(messageId, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void data) {
                if (callback != null) {
                    callback.onSuccess(data);
                }
            }

            @Override
            public void onFailed(Exception e) {
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }

    public interface ForumCallback<T> {
        void onSuccess(T data);

        void onError(Exception e);
    }
}
