package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IUserService;

import java.util.List;

import javax.inject.Inject;

public class UserService implements IUserService {
    private final IDatabaseService databaseService;

    @Inject
    public UserService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public String generateUserId() {
        return databaseService.generateUserId();
    }

    @Override
    public void createNewUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback) {
        databaseService.createNewUser(user, callback);
    }

    @Override
    public void getUser(@NonNull String uid, @NonNull DatabaseCallback<User> callback) {
        databaseService.getUser(uid, callback);
    }

    @Override
    public void getUserList(@NonNull DatabaseCallback<List<User>> callback) {
        databaseService.getUserList(callback);
    }

    @Override
    public void deleteUser(@NonNull String uid, @Nullable DatabaseCallback<Void> callback) {
        databaseService.deleteUser(uid, callback);
    }

    @Override
    public void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseCallback<User> callback) {
        databaseService.getUserByEmailAndPassword(email, password, callback);
    }

    @Override
    public void checkIfEmailExists(@NonNull String email, @NonNull DatabaseCallback<Boolean> callback) {
        databaseService.checkIfEmailExists(email, callback);
    }

    @Override
    public void updateUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback) {
        databaseService.updateUser(user, callback);
    }

    @Override
    public void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseCallback<Void> callback) {
        databaseService.updateUserRole(uid, role, callback);
    }
}
