package com.jian.simplefitv1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jian.simplefitv1.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 1500; // milliseconds
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();

        // Delay to show splash screen
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthStatus, SPLASH_DURATION);
    }

    /**
     * Check if user is already authenticated and navigate accordingly
     */
    private void checkAuthStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Determine which activity to start
        Intent intent;
        if (currentUser != null) {
            // User is already logged in, go to MainActivity
            intent = new Intent(this, MainActivity.class);
        } else {
            // User is not logged in, go to AuthActivity
            intent = new Intent(this, AuthActivity.class);
        }

        // Start the next activity and finish this one
        startActivity(intent);
        finish();
    }
}
