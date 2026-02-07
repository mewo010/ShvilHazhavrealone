package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.services.DatabaseCallback;
import com.example.sagivproject.services.IUserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UserService extends BaseDatabaseService<User> implements IUserService {

    private static final String USERS_PATH = "users";
    private final DatabaseReference usersRef;

    @Inject
    public UserService(DatabaseReference databaseReference) {
        super(databaseReference);
        this.usersRef = databaseReference.child(USERS_PATH);
    }

    @Override
    public String generateUserId() {
        return super.generateId(usersRef);
    }

    @Override
    public void createNewUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback) {
        super.create(usersRef, user.getUid(), user, callback);
    }

    @Override
    public void getUser(@NonNull String uid, @NonNull DatabaseCallback<User> callback) {
        super.getById(usersRef, uid, User.class, callback);
    }

    @Override
    public void getUserList(@NonNull DatabaseCallback<List<User>> callback) {
        super.getAll(usersRef, User.class, callback);
    }

    @Override
    public void deleteUser(@NonNull String uid, @Nullable DatabaseCallback<Void> callback) {
        super.delete(usersRef, uid, callback);
    }

    @Override
    public void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseCallback<User> callback) {
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        if (user != null && Objects.equals(user.getPassword(), password)) {
                            callback.onCompleted(user);
                            return;
                        }
                    }
                }
                callback.onCompleted(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        });
    }

    @Override
    public void checkIfEmailExists(@NonNull String email, @NonNull DatabaseCallback<Boolean> callback) {
        Query query = usersRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCompleted(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailed(error.toException());
            }
        });
    }

    @Override
    public void updateUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback) {
        super.update(usersRef, user.getUid(), user, callback);
    }

    @Override
    public void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseCallback<Void> callback) {
        usersRef.child(uid).child("role").setValue(role).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }
}
