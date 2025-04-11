package com.jian.simplefitv1.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Utility class for managing SharedPreferences
 */
public class SharedPreferencesUtils {

    private static final String PREF_NAME = "SimpleFitPrefs";

    // Preference Keys
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences sharedPreferences;

    public SharedPreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Save user data to SharedPreferences
     * @param userId User ID
     * @param email User email
     * @param displayName User display name
     * @param createdAt User creation timestamp
     */
    public void saveUserData(String userId, String email, String displayName, long createdAt) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_DISPLAY_NAME, displayName);
        editor.putLong(KEY_CREATED_AT, createdAt);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Get user ID from SharedPreferences
     * @return User ID or null if not found
     */
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    /**
     * Get user email from SharedPreferences
     * @return User email or null if not found
     */
    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    /**
     * Get user display name from SharedPreferences
     * @return User display name or null if not found
     */
    public String getDisplayName() {
        return sharedPreferences.getString(KEY_DISPLAY_NAME, null);
    }

    /**
     * Get user creation timestamp from SharedPreferences
     * @return User creation timestamp or 0 if not found
     */
    public long getCreatedAt() {
        return sharedPreferences.getLong(KEY_CREATED_AT, 0);
    }

    /**
     * Check if user is logged in based on SharedPreferences
     * @return true if user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Clear all user data from SharedPreferences
     */
    public void clearUserData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Save a string value to SharedPreferences
     * @param key Preference key
     * @param value Value to save
     */
    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Get a string value from SharedPreferences
     * @param key Preference key
     * @param defaultValue Default value if key not found
     * @return String value or defaultValue if not found
     */
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    /**
     * Save a boolean value to SharedPreferences
     * @param key Preference key
     * @param value Value to save
     */
    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get a boolean value from SharedPreferences
     * @param key Preference key
     * @param defaultValue Default value if key not found
     * @return Boolean value or defaultValue if not found
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }
}
