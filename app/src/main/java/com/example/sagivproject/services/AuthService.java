package com.example.sagivproject.services;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.HashMap;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class AuthService implements IAuthService {
    private final Context context;
    private final IDatabaseService iDatabaseService;

    @Inject
    public AuthService(@ApplicationContext @NonNull Context context, IDatabaseService iDatabaseService) {
        this.context = context.getApplicationContext();
        this.iDatabaseService = iDatabaseService;
    }

    @Override
    public void login(String email, String password, LoginCallback callback) {
        iDatabaseService.getUserByEmailAndPassword(email, password, new IDatabaseService.DatabaseCallback<>() {
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

    @Override
    public void register(String firstName, String lastName, long birthDateMillis, String email, String password, RegisterCallback callback) {
        iDatabaseService.checkIfEmailExists(email, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    callback.onError("אימייל זה תפוס");
                } else {
                    createUser(firstName, lastName, birthDateMillis, email, password, new CreateUserCallback() {
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

    @Override
    public void addUser(String firstName, String lastName, long birthDateMillis, String email, String password, AddUserCallback callback) {
        iDatabaseService.checkIfEmailExists(email, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    callback.onError("אימייל זה תפוס");
                } else {
                    createUser(firstName, lastName, birthDateMillis, email, password, new CreateUserCallback() {
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

    @Override
    public void updateUser(User user, String newFirstName, String newLastName, long newBirthDateMillis, String newEmail, String newPassword, UpdateUserCallback callback) {
        boolean emailChanged = !newEmail.equals(user.getEmail());

        if (emailChanged) {
            iDatabaseService.checkIfEmailExists(newEmail, new IDatabaseService.DatabaseCallback<>() {
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

    private void createUser(String firstName, String lastName, long birthDateMillis, String email, String password, CreateUserCallback callback) {
        String uid = iDatabaseService.generateUserId();

        User user = new User(uid, firstName, lastName, birthDateMillis, email, password, UserRole.REGULAR, null, new HashMap<>());

        iDatabaseService.createNewUser(user, new IDatabaseService.DatabaseCallback<>() {
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

    private void applyUserUpdate(User user, String firstName, String lastName, long birthDateMillis, String email, String password, UpdateUserCallback callback) {
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBirthDateMillis(birthDateMillis);
        user.setEmail(email);
        user.setPassword(password);

        iDatabaseService.updateUser(user, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void object) {
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
        User user = SharedPreferencesUtil.getUser(context);

        String email = user != null ? user.getEmail() : "";
        SharedPreferencesUtil.signOutUser(context);

        return email;
    }

    private interface CreateUserCallback {
        void onSuccess(User user);

        void onError(String message);
    }
}