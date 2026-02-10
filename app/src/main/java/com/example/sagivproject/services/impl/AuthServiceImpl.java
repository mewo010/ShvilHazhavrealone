package com.example.sagivproject.services.impl;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.services.IAuthService;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.IUserService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.HashMap;

import javax.inject.Inject;

public class AuthServiceImpl implements IAuthService {
    private final IUserService userService;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    @Inject
    public AuthServiceImpl(IUserService userService, SharedPreferencesUtil sharedPreferencesUtil) {
        this.userService = userService;
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @Override
    public void login(String email, String password, LoginCallback callback) {
        userService.getUserByEmailAndPassword(email, password, new DatabaseCallback<>() {
            @Override
            public void onCompleted(User user) {
                if (user == null) {
                    callback.onError("אימייל או סיסמה שגויים");
                    return;
                }

                sharedPreferencesUtil.saveUser(user);
                callback.onSuccess(user);
            }

            @Override
            public void onFailed(Exception e) {
                sharedPreferencesUtil.signOutUser();
                callback.onError("שגיאה בהתחברות המשתמש");
            }
        });
    }

    @Override
    public void register(String firstName, String lastName, long birthDateMillis, String email, String password, RegisterCallback callback) {
        handleUserCreation(firstName, lastName, birthDateMillis, email, password, new DatabaseCallback<>() {
            @Override
            public void onCompleted(User user) {
                sharedPreferencesUtil.saveUser(user);
                callback.onSuccess(user);
            }

            @Override
            public void onFailed(Exception e) {
                sharedPreferencesUtil.signOutUser();
                callback.onError(e.getMessage());
            }
        }, callback::onError);
    }

    @Override
    public void addUser(String firstName, String lastName, long birthDateMillis, String email, String password, AddUserCallback callback) {
        handleUserCreation(firstName, lastName, birthDateMillis, email, password, new DatabaseCallback<>() {
            @Override
            public void onCompleted(User user) {
                callback.onSuccess(user);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onError(e.getMessage());
            }
        }, callback::onError);
    }

    private void handleUserCreation(String firstName, String lastName, long birthDateMillis, String email, String password, DatabaseCallback<User> successCallback, java.util.function.Consumer<String> errorCallback) {
        userService.checkIfEmailExists(email, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    errorCallback.accept("אימייל זה תפוס");
                } else {
                    createUser(firstName, lastName, birthDateMillis, email, password, successCallback);
                }
            }

            @Override
            public void onFailed(Exception e) {
                errorCallback.accept("שגיאה בבדיקת אימייל");
            }
        });
    }

    @Override
    public void updateUser(User user, String newFirstName, String newLastName, long newBirthDateMillis, String newEmail, String newPassword, UpdateUserCallback callback) {
        boolean emailChanged = !newEmail.equals(user.getEmail());

        if (emailChanged) {
            userService.checkIfEmailExists(newEmail, new DatabaseCallback<>() {
                @Override
                public void onCompleted(Boolean exists) {
                    if (exists) {
                        callback.onError("אימייל זה תפוס");
                    } else {
                        applyUserUpdate(user, newFirstName, newLastName, newBirthDateMillis, newEmail, newPassword, callback);
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    callback.onError("שגיאה בבדיקת אימייל");
                }
            });
        } else {
            applyUserUpdate(user, newFirstName, newLastName, newBirthDateMillis, newEmail, newPassword, callback);
        }
    }

    private void createUser(String firstName, String lastName, long birthDateMillis, String email, String password, DatabaseCallback<User> callback) {
        String uid = userService.generateUserId();

        User user = new User(uid, firstName, lastName, birthDateMillis, email, password, UserRole.REGULAR, null, new HashMap<>());

        userService.createNewUser(user, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                callback.onCompleted(user);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onFailed(e);
            }
        });
    }

    private void applyUserUpdate(User user, String firstName, String lastName, long birthDateMillis, String email, String password, UpdateUserCallback callback) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthDateMillis(birthDateMillis);
        user.setEmail(email);
        user.setPassword(password);

        userService.updateUser(user, new DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
                sharedPreferencesUtil.saveUser(user);
                callback.onSuccess(user);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onError("שגיאה בעדכון הפרטים");
            }
        });
    }

    @Override
    public String logout() {
        User user = sharedPreferencesUtil.getUser();

        String email = user != null ? user.getEmail() : "";
        sharedPreferencesUtil.signOutUser();

        return email;
    }
}
