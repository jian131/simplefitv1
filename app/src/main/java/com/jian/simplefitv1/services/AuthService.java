package com.jian.simplefitv1.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthService {

    private static final String TAG = "AuthService";
    private FirebaseAuth mAuth;

    // Constructor
    public AuthService() {
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Register a new user with email and password
     * @param email User email
     * @param password User password
     * @param listener Callback to handle success or failure
     */
    public void registerUser(String email, String password, OnAuthCompleteListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Registration successful
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d(TAG, "createUserWithEmail:success");
                    listener.onSuccess(user);
                } else {
                    // Registration failed
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    listener.onError(task.getException());
                }
            });
    }

    /**
     * Login an existing user with email and password
     * @param email User email
     * @param password User password
     * @param listener Callback to handle success or failure
     */
    public void loginUser(String email, String password, OnAuthCompleteListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Login successful
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d(TAG, "signInWithEmail:success");
                    listener.onSuccess(user);
                } else {
                    // Login failed
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    listener.onError(task.getException());
                }
            });
    }

    /**
     * Logout the current user
     */
    public void logoutUser() {
        mAuth.signOut();
    }

    /**
     * Check if a user is currently logged in
     * @return FirebaseUser if logged in, null otherwise
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Interface for handling authentication callbacks
     */
    public interface OnAuthCompleteListener {
        void onSuccess(FirebaseUser user);
        void onError(Exception e);
    }
}
