package com.jian.simplefitv1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.adapters.WorkoutAdapter;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryFragment extends Fragment implements WorkoutAdapter.OnWorkoutClickListener {

    private static final String TAG = "WorkoutHistoryFragment";

    private RecyclerView rvWorkouts;
    private TextView tvNoWorkouts;
    private SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private List<Workout> workouts = new ArrayList<>();
    private WorkoutAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_history, container, false);

        // Using correct RecyclerView ID that matches the layout file
        rvWorkouts = view.findViewById(R.id.rv_workout_history);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Set up RecyclerView
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        workouts = new ArrayList<>();
        adapter = new WorkoutAdapter(getContext(), workouts);
        rvWorkouts.setAdapter(adapter);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadWorkoutHistory);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Corrigindo ID de rv_workouts para rv_workout_history
        rvWorkouts = view.findViewById(R.id.rv_workout_history);
        tvNoWorkouts = view.findViewById(R.id.tv_no_workouts);

        // Corrigindo ID de swipe_refresh para swipe_refresh_layout
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadWorkoutHistory);

        // Load data
        loadWorkoutHistory();
    }

    private void setupRecyclerView() {
        rvWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new WorkoutAdapter(getContext(), workouts, this);
        rvWorkouts.setAdapter(adapter);
    }

    private void loadWorkoutHistory() {
        if (!isAdded()) {
            return; // Fragment not attached to activity
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle not logged in state
            return;
        }

        // Start loading indicator
        swipeRefreshLayout.setRefreshing(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("workouts")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("endTime", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Check if fragment is still attached
                    if (!isAdded() || getView() == null) {
                        return;
                    }

                    // Process results and update UI
                    // ... existing code ...
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        swipeRefreshLayout.setRefreshing(false);
                        // Show error message
                    }
                });
    }

    private void updateWorkoutStatistics(int workoutCount, long totalDuration, int totalExercises) {
        // Make sure fragment is still attached and view exists
        View view = getView();
        if (view == null || !isAdded()) {
            return;  // Fragment view is null or fragment is not attached to activity
        }

        try {
            // Now safely find the views with correct IDs
            TextView tvWorkoutCount = view.findViewById(R.id.tv_stats_workout_count);
            TextView tvTotalDuration = view.findViewById(R.id.tv_stats_duration);
            TextView tvTotalExercises = view.findViewById(R.id.tv_stats_exercise_count);

            if (tvWorkoutCount != null) {
                tvWorkoutCount.setText(String.valueOf(workoutCount));
            }

            if (tvTotalDuration != null) {
                tvTotalDuration.setText(TimeUtils.formatDuration(totalDuration));
            }

            if (tvTotalExercises != null) {
                tvTotalExercises.setText(String.valueOf(totalExercises));
            }
        } catch (Exception e) {
            Log.e("WorkoutHistoryFragment", "Error updating statistics", e);
        }
    }

    private void showEmptyState() {
        tvNoWorkouts.setVisibility(View.VISIBLE);
        rvWorkouts.setVisibility(View.GONE);
    }

    @Override
    public void onWorkoutClick(Workout workout) {
        // Show workout details dialog or navigate to detail screen
        WorkoutDetailDialogFragment dialog = WorkoutDetailDialogFragment.newInstance(workout.getId());
        dialog.show(getChildFragmentManager(), "WorkoutDetailDialog");
    }
}
