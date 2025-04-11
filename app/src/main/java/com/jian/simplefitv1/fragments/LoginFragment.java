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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.AuthActivity;
import com.jian.simplefitv1.activities.MainActivity;
import com.jian.simplefitv1.services.AuthService;
import com.jian.simplefitv1.utils.ValidationUtils;

public class LoginFragment extends Fragment {

    private static final String TAG = "LoginFragment";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private AuthService authService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authService = new AuthService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        tvRegister = view.findViewById(R.id.tv_register);

        // Set click listeners
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvRegister.setOnClickListener(v -> navigateToRegister());
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (!ValidationUtils.isValidEmail(email)) {
            etEmail.setError("Please enter a valid email address");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            etPassword.setError("Password must be at least 6 characters");
            return;
        }

        // Attempt login
        btnLogin.setEnabled(false);
        authService.loginUser(email, password, new AuthService.OnAuthCompleteListener() {
            @Override
            public void onSuccess(FirebaseUser user) {
                Log.d(TAG, "Login successful: " + user.getEmail());
                navigateToMainActivity();
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Login failed", e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        btnLogin.setEnabled(true);
                    });
                }
            }
        });
    }

    private void navigateToRegister() {
        if (getActivity() instanceof AuthActivity) {
            ((AuthActivity) getActivity()).navigateToRegister();
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
