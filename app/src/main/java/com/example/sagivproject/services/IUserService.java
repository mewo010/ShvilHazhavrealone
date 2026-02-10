package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.util.List;

public interface IUserService {
    /**
     * generate a new id for a new user in the database
     *
     * @return a new id for the user
     */
    String generateUserId();

    /**
     * create a new user in the database
     *
     * @param user     the user object to create
     * @param callback the callback to call when the operation is completed
     */
    void createNewUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback);

    /**
     * get a user from the database
     *
     * @param uid      the id of the user to get
     * @param callback the callback to call when the operation is completed
     */
    void getUser(@NonNull String uid, @NonNull DatabaseCallback<User> callback);

    /**
     * get all the users from the database
     *
     * @param callback the callback to call when the operation is completed
     */
    void getUserList(@NonNull DatabaseCallback<List<User>> callback);

    /**
     * delete a user from the database
     *
     * @param uid      the user id to delete
     * @param callback the callback to call when the operation is completed
     */
    void deleteUser(@NonNull String uid, @Nullable DatabaseCallback<Void> callback);

    /**
     * get a user by email and password
     *
     * @param email    the email of the user
     * @param password the password of the user
     * @param callback the callback to call when the operation is completed
     */
    void getUserByEmailAndPassword(@NonNull String email, @NonNull String password, @NonNull DatabaseCallback<User> callback);

    /**
     * check if an email already exists in the database
     *
     * @param email    the email to check
     * @param callback the callback to call when the operation is completed
     */
    void checkIfEmailExists(@NonNull String email, @NonNull DatabaseCallback<Boolean> callback);

    /**
     * update a user in the database
     *
     * @param user     the user object to update
     * @param callback the callback to call when the operation is completed
     */
    void updateUser(@NonNull User user, @Nullable DatabaseCallback<Void> callback);

    /**
     * update only the admin status of a user
     *
     * @param uid      user id
     * @param role     new role
     * @param callback result callback
     */
    void updateUserRole(@NonNull String uid, @NonNull UserRole role, @Nullable DatabaseCallback<Void> callback);
}