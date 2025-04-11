package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.AuthActivity;
import com.jian.simplefitv1.activities.EditProfileActivity;
import com.jian.simplefitv1.activities.SettingsActivity;
import com.jian.simplefitv1.models.User;
import com.jian.simplefitv1.models.WorkoutSummary;
import com.jian.simplefitv1.utils.TimeUtils;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final int PICK_IMAGE_REQUEST = 1;

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
    private StorageReference storageRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
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
        ivProfilePicture = view.findViewById(R.id.iv_profile_picture);
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvMemberSince = view.findViewById(R.id.tv_member_since);
        tvTotalWorkouts = view.findViewById(R.id.tv_workouts_completed);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Set click listeners
        btnEditProfile.setOnClickListener(v -> navigateToEditProfile());
        btnSettings.setOnClickListener(v -> navigateToSettings());
        btnLogout.setOnClickListener(v -> logoutUser());

        // Profile picture click - allow changing
        ivProfilePicture.setOnClickListener(v -> chooseProfilePicture());

        // Load user data
        loadUserProfile();
        loadWorkoutSummary();
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
                    if (documentSnapshot.exists()) {
                        userProfile = documentSnapshot.toObject(User.class);
                        updateProfileUI();
                    } else {
                        Log.d(TAG, "Không tìm thấy hồ sơ người dùng");
                        createNewUserProfile();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tải hồ sơ người dùng", e);
                    Toast.makeText(getContext(), "Lỗi tải hồ sơ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Load profile picture
        loadProfilePicture(userId);
    }

    private void loadProfilePicture(String userId) {
        StorageReference profileRef = storageRef.child("profile_images/" + userId + "/profile.jpg");
        profileRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    if (getContext() != null && ivProfilePicture != null) {
                        Glide.with(getContext())
                                .load(uri)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .circleCrop()
                                .into(ivProfilePicture);
                    }
                })
                .addOnFailureListener(e -> {
                    // Use default image
                    if (getContext() != null && ivProfilePicture != null) {
                        Glide.with(getContext())
                                .load(R.drawable.default_profile_image)
                                .circleCrop()
                                .into(ivProfilePicture);
                    }
                });
    }

    private void loadWorkoutSummary() {
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        db.collection("workout_summaries").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        WorkoutSummary summary = documentSnapshot.toObject(WorkoutSummary.class);
                        updateWorkoutStatsUI(summary);
                    } else {
                        // No workout history yet
                        updateWorkoutStatsUI(new WorkoutSummary());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tải thống kê tập luyện", e);
                });
    }

    private void createNewUserProfile() {
        if (currentUser == null) return;

        User newUser = new User();
        // Sửa setUid thành setUserId
        newUser.setUserId(currentUser.getUid());
        newUser.setEmail(currentUser.getEmail());
        newUser.setDisplayName(currentUser.getDisplayName() != null ?
                currentUser.getDisplayName() : "Người dùng SimpleFit");
        newUser.setCreatedAt(System.currentTimeMillis());

        db.collection("users").document(currentUser.getUid())
                .set(newUser)
                .addOnSuccessListener(aVoid -> {
                    userProfile = newUser;
                    updateProfileUI();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tạo hồ sơ mới", e);
                });
    }

    private void updateProfileUI() {
        if (userProfile != null && getView() != null) {
            tvDisplayName.setText(userProfile.getDisplayName());
            tvEmail.setText(userProfile.getEmail());

            // Format the timestamp to a readable date
            // Sửa formatTimestamp thành formatDate
            String memberSince = "Thành viên từ: " + TimeUtils.formatDate(userProfile.getCreatedAt());
            tvMemberSince.setText(memberSince);
        }
    }

    private void updateWorkoutStatsUI(WorkoutSummary summary) {
        if (getView() == null) return;

        tvTotalWorkouts.setText(String.valueOf(summary.getTotalWorkouts()));

        // Format total time (stored in minutes) to hours and minutes
        long totalMinutes = summary.getTotalTimeMinutes();
        String formattedTime = String.format("%d giờ %d phút",
                totalMinutes / 60, totalMinutes % 60);
        tvTotalTime.setText(formattedTime);
    }

    private void chooseProfilePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadProfilePicture(imageUri);
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        if (currentUser == null) return;

        // Show loading indicator
        Toast.makeText(getContext(), "Đang tải ảnh lên...", Toast.LENGTH_SHORT).show();

        StorageReference profileRef = storageRef.child("profile_images/" + currentUser.getUid() + "/profile.jpg");

        profileRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get download URL and update profile
                    profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update user profile with new image URL
                        db.collection("users").document(currentUser.getUid())
                                .update("profileImageUrl", uri.toString())
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();

                                    // Update image in UI
                                    if (getContext() != null) {
                                        Glide.with(getContext())
                                                .load(uri)
                                                .circleCrop()
                                                .into(ivProfilePicture);
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error uploading profile picture", e);
                });
    }

    private void navigateToEditProfile() {
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {
        mAuth.signOut();
        navigateToLogin();
    }

    private void navigateToLogin() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload user profile in case it was updated
        if (currentUser != null) {
            loadUserProfile();
            loadWorkoutSummary();
        }
    }
}