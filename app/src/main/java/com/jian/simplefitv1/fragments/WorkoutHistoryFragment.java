package com.jian.simplefitv1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvWorkouts = view.findViewById(R.id.rv_workouts);
        tvNoWorkouts = view.findViewById(R.id.tv_no_workouts);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);

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
        if (currentUser == null) return;

        swipeRefreshLayout.setRefreshing(true);
        workouts.clear();

        db.collection("users").document(currentUser.getUid())
            .collection("workouts")
            .orderBy("startTime", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                swipeRefreshLayout.setRefreshing(false);

                if (!queryDocumentSnapshots.isEmpty()) {
                    tvNoWorkouts.setVisibility(View.GONE);
                    rvWorkouts.setVisibility(View.VISIBLE);

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Workout workout = document.toObject(Workout.class);
                        workout.setId(document.getId());
                        workouts.add(workout);
                    }
                    adapter.notifyDataSetChanged();

                    updateWorkoutStatistics();
                } else {
                    showEmptyState();
                }
            })
            .addOnFailureListener(e -> {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Error loading workout history: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                showEmptyState();
            });
    }

    private void updateWorkoutStatistics() {
        // Calculate and display workout statistics (total workouts, average duration, etc.)
        TextView tvTotalWorkouts = getView().findViewById(R.id.tv_total_workouts);
        TextView tvTotalDuration = getView().findViewById(R.id.tv_total_duration);

        int totalWorkouts = workouts.size();
        long totalDurationMs = 0;

        for (Workout workout : workouts) {
            totalDurationMs += workout.getDuration();
        }

        // Convert milliseconds to minutes
        long totalMinutes = totalDurationMs / (60 * 1000);

        tvTotalWorkouts.setText(String.valueOf(totalWorkouts));
        tvTotalDuration.setText(String.format("%d min", totalMinutes));
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
