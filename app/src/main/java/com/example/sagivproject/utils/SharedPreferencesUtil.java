package com.example.sagivproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.models.User;
import com.google.gson.Gson;

import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SharedPreferencesUtil {
    private static final String PREF_NAME = "com.example.sagivproject.PREFERENCE_FILE_KEY";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    @Inject
    public SharedPreferencesUtil(@ApplicationContext Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    private void saveString(String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", value);
        editor.apply();
    }

    private String getString() {
        return sharedPreferences.getString("user", null);
    }

    private void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void remove() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user");
        editor.apply();
    }

    private boolean contains() {
        return sharedPreferences.contains("user");
    }

    private <T> void saveObject(T object) {
        String json = gson.toJson(object);
        saveString(json);
    }

    private User getObject() {
        String json = getString();
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, User.class);
    }

    public void saveUser(User user) {
        saveObject(user);
    }

    public User getUser() {
        if (!isUserLoggedIn()) {
            return null;
        }
        return getObject();
    }

    public void signOutUser() {
        remove();
    }

    public boolean isUserLoggedIn() {
        return contains();
    }

    @Nullable
    public String getUserId() {
        User user = getUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    @Nullable
    public HashMap<String, Medication> getMedications() {
        User user = getUser();
        if (user != null) {
            return user.getMedications();
        }
        return null;
    }
}
