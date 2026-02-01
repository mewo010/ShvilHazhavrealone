package com.example.sagivproject.services;

import com.example.sagivproject.models.User;
import com.example.sagivproject.models.enums.UserRole;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.HashMap;

import javax.inject.Inject;

public class AuthService implements IAuthService {
    private final IDatabaseService iDatabaseService;
    private final SharedPreferencesUtil sharedPreferencesUtil;

    @Inject
    public AuthService(IDatabaseService iDatabaseService, SharedPreferencesUtil sharedPreferencesUtil) {
        this.iDatabaseService = iDatabaseService;
        this.sharedPreferencesUtil = sharedPreferencesUtil;
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
        iDatabaseService.checkIfEmailExists(email, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Boolean exists) {
                if (exists) {
                    callback.onError("אימייל זה תפוס");
                } else {
                    createUser(firstName, lastName, birthDateMillis, email, password, new CreateUserCallback() {
                        @Override
                        public void onSuccess(User user) {
                            sharedPreferencesUtil.saveUser(user);
                            callback.onSuccess();
                        }

                        @Override
                        public void onError(String message) {
                            sharedPreferencesUtil.signOutUser();
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
        User user = sharedPreferencesUtil.getUser();

        String email = user != null ? user.getEmail() : "";
        sharedPreferencesUtil.signOutUser();

        return email;
    }

    private interface CreateUserCallback {
        void onSuccess(User user);

        void onError(String message);
    }
}