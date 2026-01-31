package com.example.sagivproject.services;

import com.example.sagivproject.models.User;

public interface IAuthService {
    void login(String email, String password, LoginCallback callback);

    void register(String firstName, String lastName, long birthDateMillis, String email, String password, RegisterCallback callback);

    void addUser(String firstName, String lastName, long birthDateMillis, String email, String password, AddUserCallback callback);

    void updateUser(User user, String newFirstName, String newLastName, long newBirthDateMillis, String newEmail, String newPassword, UpdateUserCallback callback);

    String logout();

    interface LoginCallback {
        void onSuccess(User user);

        void onError(String message);
    }

    interface RegisterCallback {
        void onSuccess();

        void onError(String message);
    }

    interface AddUserCallback {
        void onSuccess(User user);

        void onError(String message);
    }

    interface UpdateUserCallback {
        void onSuccess(User updatedUser);

        void onError(String message);
    }
}