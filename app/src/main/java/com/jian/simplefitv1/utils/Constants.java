package com.jian.simplefitv1.utils;

/**
 * Class containing constant values used throughout the application
 */
public class Constants {

    // General constants
    public static final String APP_NAME = "SimpleFit";

    // Intent keys
    public static final String EXTRA_ROUTINE_ID = "ROUTINE_ID";
    public static final String EXTRA_EXERCISE_ID = "EXERCISE_ID";
    public static final String EXTRA_WORKOUT_ID = "WORKOUT_ID";
    public static final String EXTRA_ADD_TO_ROUTINE = "ADD_TO_ROUTINE";
    public static final String EXTRA_ADD_TO_WORKOUT = "ADD_TO_WORKOUT";
    public static final String EXTRA_SELECTION_MODE = "SELECTION_MODE";

    // SharedPreferences keys
    public static final String PREF_NAME = "SimpleFitPrefs";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_DISPLAY_NAME = "display_name";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";
    public static final String KEY_THEME = "theme";
    public static final String KEY_NOTIFICATIONS = "notifications";
    public static final String KEY_UNIT_SYSTEM = "unit_system";
    public static final String KEY_WORKOUT_REMINDER = "workout_reminder";
    public static final String KEY_FIRST_TIME = "first_time";

    // Difficulty levels (both English and Vietnamese)
    public static final String DIFFICULTY_BEGINNER = "Beginner";
    public static final String DIFFICULTY_INTERMEDIATE = "Intermediate";
    public static final String DIFFICULTY_ADVANCED = "Advanced";
    public static final String DIFFICULTY_BEGINNER_VI = "Cơ bản";
    public static final String DIFFICULTY_INTERMEDIATE_VI = "Trung bình";
    public static final String DIFFICULTY_ADVANCED_VI = "Nâng cao";

    // Unit systems
    public static final String UNIT_METRIC = "metric";
    public static final String UNIT_IMPERIAL = "imperial";

    // Exercise types
    public static final String TYPE_REPS = "reps";
    public static final String TYPE_TIME = "time";
    public static final String TYPE_DISTANCE = "distance";

    // Error messages
    public static final String ERROR_AUTH_FAILED = "Authentication failed";
    public static final String ERROR_NETWORK = "Network error. Please check your connection";
    public static final String ERROR_GENERAL = "An error occurred. Please try again";

    // Success messages
    public static final String SUCCESS_WORKOUT_SAVED = "Workout saved successfully";
    public static final String SUCCESS_ROUTINE_SAVED = "Routine saved successfully";
    public static final String SUCCESS_PROFILE_UPDATED = "Profile updated successfully";

    // Time constants
    public static final long MILLIS_PER_SECOND = 1000;
    public static final long SECONDS_PER_MINUTE = 60;
    public static final long MINUTES_PER_HOUR = 60;
    public static final long HOURS_PER_DAY = 24;
    public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * HOURS_PER_DAY;

    // Date formats
    public static final String DATE_FORMAT_DISPLAY = "dd/MM/yyyy";
    public static final String TIME_FORMAT_DISPLAY = "HH:mm";
    public static final String DATETIME_FORMAT_DISPLAY = "dd/MM/yyyy HH:mm";

    // Notification channels
    public static final String NOTIFICATION_CHANNEL_ID = "simplefit_channel";
    public static final String NOTIFICATION_CHANNEL_NAME = "SimpleFit Notifications";
    public static final String NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications for SimpleFit app";

    // Animation durations
    public static final int ANIMATION_DURATION_SHORT = 300; // milliseconds
    public static final int ANIMATION_DURATION_MEDIUM = 500; // milliseconds
    public static final int ANIMATION_DURATION_LONG = 800; // milliseconds

    // Workout constants
    public static final int DEFAULT_REST_SECONDS = 60;
    public static final int DEFAULT_SETS = 3;
    public static final int DEFAULT_REPS = 10;

    // Validation constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_DISPLAY_NAME_LENGTH = 30;
}
