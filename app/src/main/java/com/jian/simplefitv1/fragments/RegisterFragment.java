package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.AuthActivity;
import com.jian.simplefitv1.activities.MainActivity;
import com.jian.simplefitv1.models.User;
import com.jian.simplefitv1.services.AuthService;
import com.jian.simplefitv1.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    private EditText etEmail, etPassword, etConfirmPassword, etDisplayName;
    private Button btnRegister;
    private TextView tvLogin;
    private AuthService authService;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = new AuthService();
        db = FirebaseFirestore.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        etDisplayName = view.findViewById(R.id.et_display_name);
        btnRegister = view.findViewById(R.id.btn_register);
        tvLogin = view.findViewById(R.id.tv_login);

        // Set click listeners
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void attemptRegister() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String displayName = etDisplayName.getText().toString().trim();

        // Validate input
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return;
        }

        if (displayName.isEmpty()) {
            etDisplayName.setError("Display name is required");
            return;
        }

        // Attempt registration
        btnRegister.setEnabled(false);
        authService.registerUser(email, password, new AuthService.OnAuthCompleteListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "Registration successful: " + user.getEmail());
                // Save additional user information to Firestore
                saveUserToFirestore(user.getUid(), email, displayName);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Registration failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    });
                }
            }
        });
    }

    private void saveUserToFirestore(String userId, String email, String displayName) {
        User user = new User(userId, email, displayName, System.currentTimeMillis());

        db.collection("users").document(userId)
            .set(user)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "User profile saved to Firestore");
                navigateToMainActivity();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error writing user to Firestore", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Error creating profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnRegister.setEnabled(true);
                    });
                }
            });
    }

    private void navigateToLogin() {
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).navigateToLogin();
        }
    }

    private void navigateToMainActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
