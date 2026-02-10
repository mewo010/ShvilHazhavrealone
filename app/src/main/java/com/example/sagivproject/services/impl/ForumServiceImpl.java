package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.IForumService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ForumServiceImpl extends BaseDatabaseService<ForumMessage> implements IForumService {
    private static final String FORUM_PATH = "forum_messages";

    @Inject
    public ForumServiceImpl() {
        super(FORUM_PATH, ForumMessage.class);
    }

    private String getCategoryPath(String categoryId) {
        return FORUM_PATH + "/" + categoryId;
    }

    private DatabaseReference getCategoryMessagesRef(String categoryId) {
        // 'databaseReference' is the protected root reference from BaseDatabaseService
        return databaseReference.child(getCategoryPath(categoryId));
    }

    @Override
    public void sendMessage(User user, String text, String categoryId, @Nullable DatabaseCallback<Void> callback) {
        DatabaseReference categoryMessagesRef = getCategoryMessagesRef(categoryId);
        String messageId = categoryMessagesRef.push().getKey();

        if (messageId == null) {
            if (callback != null)
                callback.onFailed(new Exception("Failed to generate message ID."));
            return;
        }

        ForumMessage msg = new ForumMessage(messageId, user.getFullName(), user.getEmail(), text, System.currentTimeMillis(), user.getId(), user.isAdmin());

        // Use the protected 'writeData' helper from BaseDatabaseService with a full path
        writeData(getCategoryPath(categoryId) + "/" + messageId, msg, callback);
    }

    @Override
    public void listenToMessages(String categoryId, DatabaseCallback<List<ForumMessage>> callback) {
        getCategoryMessagesRef(categoryId).orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ForumMessage> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ForumMessage msg = child.getValue(ForumMessage.class);
                    if (msg != null) {
                        list.add(msg);
                    }
                }
                if (callback != null) callback.onCompleted(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) callback.onFailed(error.toException());
            }
        });
    }

    @Override
    public void deleteMessage(@NonNull String messageId, String categoryId, @Nullable DatabaseCallback<Void> callback) {
        // Use the protected 'deleteData' helper from BaseDatabaseService with a full path
        deleteData(getCategoryPath(categoryId) + "/" + messageId, callback);
    }
}
