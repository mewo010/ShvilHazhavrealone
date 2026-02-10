package com.example.sagivproject.services;

import com.example.sagivproject.models.User;

public interface IAuthService {
    /**
     * Logs in a user with the given email and password.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @param callback The callback to be invoked when the login process is complete.
     */
    void login(String email, String password, LoginCallback callback);

    /**
     * Registers a new user with the given details.
     *
     * @param firstName       The user's first name.
     * @param lastName        The user's last name.
     * @param birthDateMillis The user's birth date in milliseconds.
     * @param email           The user's email.
     * @param password        The user's password.
     * @param callback        The callback to be invoked when the registration process is complete.
     */
    void register(String firstName, String lastName, long birthDateMillis, String email, String password, RegisterCallback callback);

    /**
     * Adds a new user with the given details.
     *
     * @param firstName       The user's first name.
     * @param lastName        The user's last name.
     * @param birthDateMillis The user's birth date in milliseconds.
     * @param email           The user's email.
     * @param password        The user's password.
     * @param callback        The callback to be invoked when the user is added.
     */
    void addUser(String firstName, String lastName, long birthDateMillis, String email, String password, AddUserCallback callback);

    /**
     * Updates a user's details.
     *
     * @param user               The user to update.
     * @param newFirstName       The new first name.
     * @param newLastName        The new last name.
     * @param newBirthDateMillis The new birth date in milliseconds.
     * @param newEmail           The new email.
     * @param newPassword        The new password.
     * @param callback           The callback to be invoked when the update is complete.
     */
    void updateUser(User user, String newFirstName, String newLastName, long newBirthDateMillis, String newEmail, String newPassword, UpdateUserCallback callback);

    /**
     * Logs out the current user.
     *
     * @return The email of the logged-out user.
     */
    String logout();

    /**
     * Callback for the login process.
     */
    interface LoginCallback {
        /**
         * Called when the login is successful.
         *
         * @param user The logged-in user.
         */
        void onSuccess(User user);

        /**
         * Called when the login fails.
         *
         * @param message The error message.
         */
        void onError(String message);
    }

    /**
     * Callback for the registration process.
     */
    interface RegisterCallback {
        /**
         * Called when the registration is successful.
         */
        void onSuccess(User user);

        /**
         * Called when the registration fails.
         *
         * @param message The error message.
         */
        void onError(String message);
    }

    /**
     * Callback for adding a new user.
     */
    interface AddUserCallback {
        /**
         * Called when the user is added successfully.
         *
         * @param user The new user.
         */
        void onSuccess(User user);

        /**
         * Called when adding the user fails.
         *
         * @param message The error message.
         */
        void onError(String message);
    }

    /**
     * Callback for updating a user.
     */
    interface UpdateUserCallback {
        /**
         * Called when the user is updated successfully.
         *
         * @param ignoredUpdatedUser The updated user.
         */
        void onSuccess(User ignoredUpdatedUser);

        /**
         * Called when updating the user fails.
         *
         * @param message The error message.
         */
        void onError(String message);
    }
}