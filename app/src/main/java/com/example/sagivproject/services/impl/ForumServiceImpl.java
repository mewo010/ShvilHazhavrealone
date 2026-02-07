package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.DatabaseCallback;
import com.example.sagivproject.services.IForumService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ForumServiceImpl extends BaseDatabaseService<ForumMessage> implements IForumService {

    private static final String FORUM_PATH = "forum";
    private final DatabaseReference forumRef;

    @Inject
    public ForumServiceImpl(DatabaseReference databaseReference) {
        super(databaseReference);
        this.forumRef = databaseReference.child(FORUM_PATH);
    }

    @Override
    public void sendMessage(User user, String text, @Nullable DatabaseCallback<Void> callback) {
        String messageId = generateId(forumRef);
        ForumMessage msg = new ForumMessage(messageId, user.getFullName(), user.getEmail(), text, System.currentTimeMillis(), user.getUid(), user.isAdmin());
        super.create(forumRef, messageId, msg, callback);
    }

    @Override
    public void listenToMessages(DatabaseCallback<List<ForumMessage>> callback) {
        forumRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ForumMessage> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ForumMessage msg = child.getValue(ForumMessage.class);
                    list.add(msg);
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
    public void deleteMessage(@NonNull String messageId, @Nullable DatabaseCallback<Void> callback) {
        super.delete(forumRef, messageId, callback);
    }
}
