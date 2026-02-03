package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IUserService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UserService implements IUserService {

    private static final String USERS_PATH = "users";
    private final DatabaseReference databaseReference;

    @Inject
    public UserService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(USERS_PATH);
    }

    @Override
    public String generateUserId() {
        return databaseReference.push().getKey();
    }

    @Override
    public void createNewUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(user.getUid()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void getUser(@NonNull String uid, @NonNull DatabaseCallback<User> callback) {
        databaseReference.child(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                User user = task.getResult().getValue(User.class);
                callback.onCompleted(user);
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void getUserList(@NonNull DatabaseCallback<List<User>> callback) {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }
                callback.onCompleted(userList);
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void deleteUser(@NonNull String uid, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(uid).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseCallback<User> callback) {
        Query query = databaseReference.orderByChild("email").equalTo(email);
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
        Query query = databaseReference.orderByChild("email").equalTo(email);
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
        databaseReference.child(user.getUid()).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(uid).child("role").setValue(role).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }
}
