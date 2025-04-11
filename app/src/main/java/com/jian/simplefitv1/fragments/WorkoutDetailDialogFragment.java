package com.jian.simplefitv1.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.utils.TimeUtils;

import java.util.Date;

public class WorkoutDetailDialogFragment extends DialogFragment {

    private static final String TAG = "WorkoutDetailDialog";
    private static final String ARG_WORKOUT_ID = "workout_id";

    private String workoutId;
    private Workout workout;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Các thành phần UI
    private TextView tvWorkoutName;
    private TextView tvWorkoutDate;
    private TextView tvWorkoutDuration;
    private RecyclerView rvExercises;
    private TextView tvNoExercises;

    /**
     * Tạo instance mới của WorkoutDetailDialogFragment
     * @param workoutId ID của buổi tập cần hiển thị
     * @return Instance mới của WorkoutDetailDialogFragment
     */
    public static WorkoutDetailDialogFragment newInstance(String workoutId) {
        WorkoutDetailDialogFragment fragment = new WorkoutDetailDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORKOUT_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_SimpleFit_NoActionBar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (getArguments() != null) {
            workoutId = getArguments().getString(ARG_WORKOUT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_workout_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo view
        tvWorkoutName = view.findViewById(R.id.tv_workout_name);
        tvWorkoutDate = view.findViewById(R.id.tv_workout_date);
        tvWorkoutDuration = view.findViewById(R.id.tv_workout_duration);
        rvExercises = view.findViewById(R.id.rv_exercises);
        tvNoExercises = view.findViewById(R.id.tv_no_exercises);

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dismiss());

        // Thiết lập RecyclerView
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        // Tải dữ liệu buổi tập
        loadWorkoutDetails();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void loadWorkoutDetails() {
        if (workoutId == null || mAuth.getCurrentUser() == null) {
            dismiss();
            return;
        }

        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("workouts").document(workoutId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        workout = documentSnapshot.toObject(Workout.class);
                        workout.setId(documentSnapshot.getId());
                        updateUI();
                    } else {
                        Log.e(TAG, "Không tìm thấy buổi tập");
                        dismiss();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi khi tải buổi tập", e);
                    dismiss();
                });
    }

    private void updateUI() {
        if (workout == null || getContext() == null) return;

        // Thiết lập tên buổi tập
        String workoutName = workout.getRoutineName();
        if (workoutName == null || workoutName.isEmpty()) {
            workoutName = getString(R.string.quick_workout);
        }
        tvWorkoutName.setText(workoutName);

        // Định dạng và hiển thị ngày
        String workoutDate = TimeUtils.formatDateTime(workout.getStartTime());
        tvWorkoutDate.setText(workoutDate);

        // Định dạng và hiển thị thời lượng
        String duration = TimeUtils.formatDuration(workout.getDuration());
        tvWorkoutDuration.setText(duration);

        // Tải bài tập
        loadExerciseDetails();
    }

    private void loadExerciseDetails() {
        // Hiển thị thông báo nếu không có bài tập
        if (workout.getExercises() == null || workout.getExercises().isEmpty()) {
            tvNoExercises.setVisibility(View.VISIBLE);
            rvExercises.setVisibility(View.GONE);
        } else {
            tvNoExercises.setVisibility(View.GONE);
            rvExercises.setVisibility(View.VISIBLE);

            // Trong triển khai đầy đủ, chúng ta sẽ tải chi tiết bài tập và thêm vào adapter
            // Đây chỉ là phiên bản đơn giản
        }
    }
}