package com.jian.simplefitv1.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.fragments.LoginFragment;
import com.jian.simplefitv1.fragments.RegisterFragment;

/**
 * Activity for handling user authentication (login and registration)
 */
public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already authenticated
        if (mAuth.getCurrentUser() != null) {
            // User is already logged in, redirect to main activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // By default, show the login fragment
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    /**
     * Load a fragment into the fragment container
     * @param fragment The fragment to load
     */
    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.auth_fragment_container, fragment);
        transaction.commit();
    }

    /**
     * Navigate to the register fragment
     */
    public void navigateToRegister() {
        loadFragment(new RegisterFragment());
    }

    /**
     * Navigate to the login fragment
     */
    public void navigateToLogin() {
        loadFragment(new LoginFragment());
    }

    /**
     * Navigate to the main activity after successful authentication
     */
    public void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in and update UI accordingly
        if (mAuth.getCurrentUser() != null) {
            navigateToMainActivity();
        }
    }

    @Override
    public void onBackPressed() {
        // If on register fragment, go back to login fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.auth_fragment_container);
        if (currentFragment instanceof RegisterFragment) {
            navigateToLogin();
        } else {
            // If on login fragment, exit the app
            super.onBackPressed();
        }
    }
}
