package com.jian.simplefitv1.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Utility class for time and date operations
 */
public class TimeUtils {

    /**
     * Format milliseconds duration to human-readable format (e.g., "1h 30m")
     * @param millis Duration in milliseconds
     * @return Formatted duration string
     */
    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "0m";
        }

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) -
                      TimeUnit.HOURS.toMinutes(hours);

        StringBuilder builder = new StringBuilder();

        if (hours > 0) {
            builder.append(hours).append("h ");
        }

        builder.append(minutes).append("m");

        return builder.toString();
    }

    /**
     * Format seconds to MM:SS format
     * @param seconds Duration in seconds
     * @return Formatted time string (e.g., "01:30")
     */
    public static String formatSeconds(int seconds) {
        if (seconds < 0) {
            seconds = 0;
        }

        int minutes = seconds / 60;
        int secs = seconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
    }

    /**
     * Format a timestamp to date string
     * @param timestamp Timestamp in milliseconds
     * @return Formatted date string (e.g., "01/01/2023")
     */
    public static String formatDate(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    /**
     * Format a timestamp to time string
     * @param timestamp Timestamp in milliseconds
     * @return Formatted time string (e.g., "13:45")
     */
    public static String formatTime(long timestamp) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT_DISPLAY, Locale.getDefault());
        return timeFormat.format(new Date(timestamp));
    }

    /**
     * Format a timestamp to date and time string
     * @param timestamp Timestamp in milliseconds
     * @return Formatted date and time string (e.g., "01/01/2023 13:45")
     */
    public static String formatDateTime(long timestamp) {
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat(Constants.DATETIME_FORMAT_DISPLAY, Locale.getDefault());
        return dateTimeFormat.format(new Date(timestamp));
    }

    /**
     * Get a relative time string (e.g., "2 days ago", "just now")
     * @param timestamp Timestamp in milliseconds
     * @return Relative time string
     */
    public static String getRelativeTimeSpan(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        // Less than a minute
        if (diff < Constants.MILLIS_PER_MINUTE) {
            return "just now";
        }

        // Less than an hour
        if (diff < Constants.MILLIS_PER_HOUR) {
            long minutes = diff / Constants.MILLIS_PER_MINUTE;
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }

        // Less than a day
        if (diff < Constants.MILLIS_PER_DAY) {
            long hours = diff / Constants.MILLIS_PER_HOUR;
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        // Less than a week
        if (diff < Constants.MILLIS_PER_DAY * 7) {
            long days = diff / Constants.MILLIS_PER_DAY;
            return days + (days == 1 ? " day ago" : " days ago");
        }

        // More than a week, return formatted date
        return formatDate(timestamp);
    }

    /**
     * Get beginning of day timestamp
     * @param timestamp Timestamp in milliseconds
     * @return Timestamp of beginning of day
     */
    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    /**
     * Get end of day timestamp
     * @param timestamp Timestamp in milliseconds
     * @return Timestamp of end of day
     */
    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    /**
     * Get beginning of week timestamp (assuming week starts on Monday)
     * @param timestamp Timestamp in milliseconds
     * @return Timestamp of beginning of week
     */
    public static long getStartOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return getStartOfDay(calendar.getTimeInMillis());
    }

    /**
     * Get end of week timestamp (assuming week ends on Sunday)
     * @param timestamp Timestamp in milliseconds
     * @return Timestamp of end of week
     */
    public static long getEndOfWeek(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.WEEK_OF_YEAR, 1); // Move to next Sunday if the current day is Sunday
        return getEndOfDay(calendar.getTimeInMillis());
    }

    /**
     * Get beginning of month timestamp
     * @param timestamp Timestamp in milliseconds
     * @return Timestamp of beginning of month
     */
    public static long getStartOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getStartOfDay(calendar.getTimeInMillis());
    }

    /**
     * Get end of month timestamp
     * @param timestamp Timestamp in milliseconds
     * @return Timestamp of end of month
     */
    public static long getEndOfMonth(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return getEndOfDay(calendar.getTimeInMillis());
    }

    /**
     * Check if two dates are on the same day
     * @param timestamp1 First timestamp
     * @param timestamp2 Second timestamp
     * @return true if dates are on the same day
     */
    public static boolean isSameDay(long timestamp1, long timestamp2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(timestamp1);
        cal2.setTimeInMillis(timestamp2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Get timestamp for X days ago
     * @param days Number of days
     * @return Timestamp X days ago
     */
    public static long getDaysAgo(int days) {
        return System.currentTimeMillis() - (days * Constants.MILLIS_PER_DAY);
    }

    /**
     * Get timestamp for X days in future
     * @param days Number of days
     * @return Timestamp X days in future
     */
    public static long getDaysFromNow(int days) {
        return System.currentTimeMillis() + (days * Constants.MILLIS_PER_DAY);
    }
}
