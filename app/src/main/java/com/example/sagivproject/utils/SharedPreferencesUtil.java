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

    private void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
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

    private void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    private <T> void saveObject(String key, T object) {
        String json = gson.toJson(object);
        saveString(key, json);
    }

    private <T> T getObject(String key, Class<T> type) {
        String json = getString(key, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, type);
    }

    public void saveUser(User user) {
        saveObject("user", user);
    }

    public User getUser() {
        if (!isUserLoggedIn()) {
            return null;
        }
        return getObject("user", User.class);
    }

    public void signOutUser() {
        remove("user");
    }

    public boolean isUserLoggedIn() {
        return contains("user");
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