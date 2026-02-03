package com.example.sagivproject.services;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.ForumMessage;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.interfaces.IForumService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class ForumService implements IForumService {

    private static final String FORUM_PATH = "forum";
    private final DatabaseReference databaseReference;

    @Inject
    public ForumService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(FORUM_PATH);
    }

    @Override
    public void sendMessage(User user, String text, ForumCallback<Void> callback) {
        String messageId = databaseReference.push().getKey();
        ForumMessage msg = new ForumMessage(messageId, user.getFullName(), user.getEmail(), text, System.currentTimeMillis(), user.getUid(), user.isAdmin());

        databaseReference.child(Objects.requireNonNull(messageId)).setValue(msg).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onSuccess(null);
            } else {
                if (callback != null) callback.onError(task.getException());
            }
        });
    }

    @Override
    public void listenToMessages(ForumCallback<List<ForumMessage>> callback) {
        databaseReference.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ForumMessage> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ForumMessage msg = child.getValue(ForumMessage.class);
                    list.add(msg);
                }
                if (callback != null) callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) callback.onError(error.toException());
            }
        });
    }

    @Override
    public void deleteMessage(@NonNull String messageId, ForumCallback<Void> callback) {
        databaseReference.child(messageId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onSuccess(null);
            } else {
                if (callback != null) callback.onError(task.getException());
            }
        });
    }

    public interface ForumCallback<T> {
        void onSuccess(T data);

        void onError(Exception e);
    }
}
