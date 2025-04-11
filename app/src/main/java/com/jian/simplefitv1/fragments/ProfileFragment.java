package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.AuthActivity;
import com.jian.simplefitv1.activities.EditProfileActivity;
import com.jian.simplefitv1.activities.SettingsActivity;
import com.jian.simplefitv1.models.User;
import com.jian.simplefitv1.models.WorkoutSummary;
import com.jian.simplefitv1.utils.TimeUtils;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    // UI elements
    private ImageView ivProfilePicture;
    private TextView tvDisplayName, tvEmail, tvMemberSince;
    private TextView tvTotalWorkouts, tvTotalTime;
    private Button btnEditProfile, btnSettings, btnLogout;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private User userProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI components
        initializeViews(view);

        // Set click listeners
        setupClickListeners();

        // Load user data
        loadUserProfile();
        loadWorkoutSummary();
    }

    private void initializeViews(View view) {
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvMemberSince = view.findViewById(R.id.tv_member_since);
        tvTotalWorkouts = view.findViewById(R.id.tv_workouts_completed);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Always load default profile image since we don't use Firebase Storage
        loadDefaultProfileImage();
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> navigateToEditProfile());
        btnSettings.setOnClickListener(v -> navigateToSettings());
        btnLogout.setOnClickListener(v -> logoutUser());

        // No action for profile picture click as we don't support image uploads
        ivProfilePicture.setOnClickListener(v -> showToast("Chức năng thay đổi ảnh đại diện không có sẵn"));
    }

    private void loadUserProfile() {
        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded()) return; // Fragment not attached to activity

                    if (documentSnapshot.exists()) {
                        userProfile = documentSnapshot.toObject(User.class);
                        updateProfileUI();
                    } else {
                        Log.d(TAG, "Không tìm thấy hồ sơ người dùng");
                        createNewUserProfile();
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;

                    Log.e(TAG, "Lỗi khi tải hồ sơ người dùng", e);
                    showToast("Lỗi tải hồ sơ: " + e.getMessage());
                });
    }

    private void loadDefaultProfileImage() {
        if (isAdded() && ivProfilePicture != null) {
            Glide.with(requireContext())
                    .load(R.drawable.default_profile_image)
                    .circleCrop()
                    .into(ivProfilePicture);
        }
    }

    private void loadWorkoutSummary() {
        if (currentUser == null || !isAdded()) return;

        String userId = currentUser.getUid();
        db.collection("workout_summaries").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!isAdded()) return;

                    if (documentSnapshot.exists()) {
                        WorkoutSummary summary = documentSnapshot.toObject(WorkoutSummary.class);
                        updateWorkoutStatsUI(summary);
                    } else {
                        // No workout history yet
                        updateWorkoutStatsUI(new WorkoutSummary());
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    Log.e(TAG, "Lỗi khi tải thống kê tập luyện", e);
                });
    }

    private void createNewUserProfile() {
        if (currentUser == null || !isAdded()) return;

        User newUser = new User();
        newUser.setUserId(currentUser.getUid());
        newUser.setEmail(currentUser.getEmail());
        newUser.setDisplayName(currentUser.getDisplayName() != null ?
                currentUser.getDisplayName() : "Người dùng SimpleFit");
        newUser.setCreatedAt(System.currentTimeMillis());

        db.collection("users").document(currentUser.getUid())
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    if (!isAdded()) return;

                    userProfile = newUser;
                    updateProfileUI();
                    showToast("Đã tạo hồ sơ mới");
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;

                    Log.e(TAG, "Lỗi khi tạo hồ sơ mới", e);
                    showToast("Lỗi khi tạo hồ sơ: " + e.getMessage());
                });
    }

    private void updateProfileUI() {
        if (userProfile != null && isAdded()) {
            tvDisplayName.setText(userProfile.getDisplayName());
            tvEmail.setText(userProfile.getEmail());

            // Format the timestamp to a readable date using TimeUtils
            String memberSince = "Thành viên từ: " + TimeUtils.formatDate(userProfile.getCreatedAt());
            tvMemberSince.setText(memberSince);
        }
    }

    private void updateWorkoutStatsUI(WorkoutSummary summary) {
        if (!isAdded()) return;

        tvTotalWorkouts.setText(String.valueOf(summary.getTotalWorkouts()));

        // Format total time (stored in minutes) to hours and minutes
        long totalMinutes = summary.getTotalTimeMinutes();
        String formattedTime = String.format("%d giờ %d phút",
                totalMinutes / 60, totalMinutes % 60);
        tvTotalTime.setText(formattedTime);
    }

    private void navigateToEditProfile() {
        if (!isAdded() || getActivity() == null) return;

        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        if (!isAdded() || getActivity() == null) return;

        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {
        if (!isAdded()) return;

        mAuth.signOut();
        navigateToLogin();
    }

    private void navigateToLogin() {
        if (!isAdded() || getActivity() == null) return;

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload user profile in case it was updated
        if (isAdded() && currentUser != null) {
            loadUserProfile();
            loadWorkoutSummary();
        }
    }
}