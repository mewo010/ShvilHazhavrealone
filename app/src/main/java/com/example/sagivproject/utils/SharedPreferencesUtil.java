package com.example.sagivproject.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.sagivproject.models.User;
import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class SharedPreferencesUtil {
    private static final String PREF_NAME = "com.example.sagivproject.PREFERENCE_FILE_KEY";
    private static final String KEY_USER = "user";
    private static final String KEY_DARK_MODE = "dark_mode";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    @Inject
    public SharedPreferencesUtil(@ApplicationContext Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    /**
     * Save a string to shared preferences.
     *
     * @param key   The key to save the string with.
     * @param value The string to save.
     * @see SharedPreferences.Editor#putString(String, String)
     */
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a string from shared preferences.
     *
     * @param key          The key to get the string with.
     * @param defaultValue The default value to return if the key is not found.
     * @return The string value stored in shared preferences.
     * @see SharedPreferences#getString(String, String)
     */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Save an integer to shared preferences.
     *
     * @param key   The key to save the integer with.
     * @param value The integer to save.
     * @see SharedPreferences.Editor#putInt(String, int)
     */
    public void saveInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Get an integer from shared preferences.
     *
     * @param key          The key to get the integer with.
     * @param defaultValue The default value to return if the key is not found.
     * @return The integer value stored in shared preferences.
     * @see SharedPreferences#getInt(String, int)
     */
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Save a boolean to shared preferences.
     *
     * @param key   The key to save the boolean with.
     * @param value The boolean to save.
     * @see SharedPreferences.Editor#putBoolean(String, boolean)
     */
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a boolean from shared preferences.
     *
     * @param key          The key to get the boolean with.
     * @param defaultValue The default value to return if the key is not found.
     * @return The boolean value stored in shared preferences.
     * @see SharedPreferences#getBoolean(String, boolean)
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Clear all data from shared preferences.
     *
     * @see SharedPreferences.Editor#clear()
     */
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Remove a specific key from shared preferences.
     *
     * @param key The key to remove.
     * @see SharedPreferences.Editor#remove(String)
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Check if a key exists in shared preferences.
     *
     * @param key The key to check.
     * @return true if the key exists, false otherwise.
     * @see SharedPreferences#contains(String)
     */
    public boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

    /**
     * Save an object to shared preferences.
     *
     * @param key    The key to save the object with.
     * @param object The object to save.
     * @param <T>    The type of the object.
     */
    public <T> void saveObject(String key, T object) {
        String json = gson.toJson(object);
        saveString(key, json);
    }

    /**
     * Get an object from shared preferences.
     *
     * @param key  The key to get the object with.
     * @param type The class of the object.
     * @param <T>  The type of the object.
     * @return The object stored in shared preferences.
     */
    public <T> T getObject(String key, Class<T> type) {
        String json = getString(key, null);
        if (json == null) {
            return null;
        }
        return gson.fromJson(json, type);
    }

    /**
     * Save a user object to shared preferences.
     *
     * @param user The user object to save.
     * @see User
     */
    public void saveUser(User user) {
        saveObject(KEY_USER, user);
    }

    /**
     * Get the user object from shared preferences.
     *
     * @return The user object stored in shared preferences, or null if not logged in.
     * @see User
     * @see #isUserLoggedIn()
     */
    public User getUser() {
        return getObject(KEY_USER, User.class);
    }

    /**
     * Sign out the user by removing user data from shared preferences.
     */
    public void signOutUser() {
        remove(KEY_USER);
    }

    /**
     * Check if a user is logged in by checking if the user data is present in shared preferences.
     *
     * @return true if the user is logged in, false otherwise.
     * @see #contains(String)
     */
    public boolean isUserLoggedIn() {
        return contains(KEY_USER);
    }

    /**
     * Checks if dark mode is enabled.
     *
     * @return true if dark mode is enabled, false otherwise.
     */
    public boolean isDarkMode() {
        return getBoolean(KEY_DARK_MODE, false);
    }

    /**
     * Sets the dark mode preference.
     *
     * @param isDarkMode true to enable dark mode, false to disable.
     */
    public void setDarkMode(boolean isDarkMode) {
        saveBoolean(KEY_DARK_MODE, isDarkMode);
    }

    /**
     * Get the user id of the logged-in user.
     *
     * @return The user id of the logged-in user, or null if no user is logged in.
     */
    @Nullable
    public String getUserId() {
        User user = getUser();
        if (user != null) {
            return user.getId();
        }
        return null;
    }
}
