package com.jian.simplefitv1.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.User;
import com.jian.simplefitv1.utils.FirebaseUtils;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "EditProfileActivity";

    private EditText etDisplayName;
    private Button btnSave;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Chỉnh sửa hồ sơ");

        // Init views
        etDisplayName = findViewById(R.id.et_display_name);
        btnSave = findViewById(R.id.btn_save);
        progressBar = findViewById(R.id.progress_bar);

        // Load user data
        if (currentUser != null) {
            loadUserProfile();
        } else {
            finish();
            return;
        }

        // Set click listeners
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void loadUserProfile() {
        showLoading(true);

        String userId = currentUser.getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    showLoading(false);
                    if (documentSnapshot.exists()) {
                        userProfile = documentSnapshot.toObject(User.class);
                        populateUI();
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error loading user profile", e);
                    Toast.makeText(this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void populateUI() {
        if (userProfile != null) {
            etDisplayName.setText(userProfile.getDisplayName());
        }
    }

    private void updateProfile() {
        String displayName = etDisplayName.getText().toString().trim();

        if (displayName.isEmpty()) {
            etDisplayName.setError("Vui lòng nhập tên hiển thị");
            return;
        }

        showLoading(true);

        // Update in Firestore
        String userId = currentUser.getUid();
        db.collection("users").document(userId)
                .update("displayName", displayName)
                .addOnSuccessListener(aVoid -> {
                    // Update in Firebase Auth
                    FirebaseUtils.updateUserDisplayName(displayName, new FirebaseUtils.OnProfileUpdateListener() {
                        @Override
                        public void onSuccess() {
                            showLoading(false);
                            Toast.makeText(EditProfileActivity.this, "Hồ sơ đã được cập nhật", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            showLoading(false);
                            Toast.makeText(EditProfileActivity.this,
                                    "Cập nhật thành công trong cơ sở dữ liệu nhưng không cập nhật được tên hiển thị xác thực",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Toast.makeText(EditProfileActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!isLoading);
        etDisplayName.setEnabled(!isLoading);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}