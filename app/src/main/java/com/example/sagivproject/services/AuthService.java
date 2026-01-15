package com.example.sagivproject.services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.User;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.HashMap;

public class AuthService {
    private final Context context;
    private final DatabaseService databaseService;

    public AuthService(@NonNull Context context) {
        this.context = context.getApplicationContext();
        this.databaseService = DatabaseService.getInstance();
    }

    public interface LoginCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface RegisterCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface AddUserCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    private interface CreateUserCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public void login(String email, String password, LoginCallback callback) {
        databaseService.getUserByEmailAndPassword(email, password, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user == null) {
                    callback.onError("אימייל או סיסמה שגויים");
                    return;
                }

                SharedPreferencesUtil.saveUser(context, user);
                callback.onSuccess(user);
            }

            @Override
            public void onFailed(Exception e) {
                SharedPreferencesUtil.signOutUser(context);
                callback.onError("שגיאה בהתחברות המשתמש");
            }
        });
    }

    public void register(String firstName, String lastName, String email, String password, RegisterCallback callback) {
        databaseService.checkIfEmailExists(email, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    callback.onError("אימייל זה תפוס");
                } else {
                    createUser(firstName, lastName, email, password, new CreateUserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            SharedPreferencesUtil.saveUser(context, user);
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String message) {
                            SharedPreferencesUtil.signOutUser(context);
                            callback.onError(message);
                        }
                    });
                }
            }

            @Override
            public void onFailed(Exception e) {
                callback.onError("שגיאה בבדיקת אימייל");
            }
        });
    }

    public void addUser(String firstName, String lastName, String email, String password, AddUserCallback callback) {
        databaseService.checkIfEmailExists(email, new DatabaseService.DatabaseCallback<Boolean>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    callback.onError("אימייל זה תפוס");
                } else {
                    createUser(firstName, lastName, email, password, new CreateUserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            callback.onSuccess(user);
                        }

                        @Override
                        public void onError(String message) {
                            callback.onError(message);
                        }
                    });
                }
            }

            @Override
            public void onFailed(Exception e) {
                callback.onError("שגיאה בבדיקת אימייל");
            }
        });
    }

    private void createUser(String firstName, String lastName, String email, String password, CreateUserCallback callback) {
        String uid = databaseService.generateUserId();

        User user = new User(uid, firstName, lastName, email, password, false, null, new HashMap<>(), 0);

        databaseService.createNewUser(user, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                callback.onSuccess(user);
            }

            @Override
            public void onFailed(Exception e) {
                callback.onError("שגיאה בשמירת הנתונים");
            }
        });
    }

    //התנתקות
    public String logout() {
        User user = SharedPreferencesUtil.getUser(context);

        String email = user != null ? user.getEmail() : "";
        SharedPreferencesUtil.signOutUser(context);

        return email;
    }
}