package com.jian.simplefitv1.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtils {

    private static final int MIN_PASSWORD_LENGTH = 6;

    /**
     * Check if email is valid
     * @param email Email to check
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check if password is valid
     * @param password Password to check
     * @return true if password length is at least MIN_PASSWORD_LENGTH, false otherwise
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Check if string is not empty
     * @param text Text to check
     * @return true if text is not empty, false otherwise
     */
    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text);
    }

    /**
     * Check if text contains only letters and spaces
     * @param text Text to check
     * @return true if text contains only letters and spaces, false otherwise
     */
    public static boolean isValidName(String text) {
        return !TextUtils.isEmpty(text) && text.matches("[a-zA-Z ]+");
    }
}
