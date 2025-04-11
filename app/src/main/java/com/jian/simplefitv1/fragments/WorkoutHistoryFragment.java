package com.jian.simplefitv1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
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
    private TextView tvTotalWorkouts, tvTotalDuration, tvTotalExercises;

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

        // Initialize views
        rvWorkouts = view.findViewById(R.id.rv_workout_history);
        tvNoWorkouts = view.findViewById(R.id.tv_no_workouts);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        // Initialize statistics views
        tvTotalWorkouts = view.findViewById(R.id.tv_total_workouts);
        tvTotalDuration = view.findViewById(R.id.tv_total_duration);
        tvTotalExercises = view.findViewById(R.id.tv_total_exercises);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        if (!isAdded() || currentUser == null) {
            return;
        }

        swipeRefreshLayout.setRefreshing(true);
        workouts.clear();

        try {
            db.collection("workouts")
                    .whereEqualTo("userId", currentUser.getUid())
                    .whereEqualTo("completed", true)
                    .orderBy("endTime", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!isAdded()) return;

                        swipeRefreshLayout.setRefreshing(false);

                        if (queryDocumentSnapshots.isEmpty()) {
                            showEmptyState();
                            return;
                        }

                        tvNoWorkouts.setVisibility(View.GONE);
                        rvWorkouts.setVisibility(View.VISIBLE);

                        int totalWorkouts = 0;
                        long totalDuration = 0;
                        int totalExercises = 0;

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Workout workout = document.toObject(Workout.class);
                            workout.setId(document.getId());
                            workouts.add(workout);

                            totalWorkouts++;
                            totalDuration += workout.getDuration();
                            totalExercises += workout.getExerciseCount();
                        }

                        adapter.notifyDataSetChanged();
                        updateWorkoutStatistics(totalWorkouts, totalDuration, totalExercises);

                        Log.d(TAG, "Loaded " + workouts.size() + " workouts");
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            swipeRefreshLayout.setRefreshing(false);
                            handleQueryError(e);
                        }
                    });
        } catch (Exception e) {
            swipeRefreshLayout.setRefreshing(false);
            handleQueryError(e);
        }
    }

    private void handleQueryError(Exception e) {
        Log.e(TAG, "Error loading workouts", e);

        if (e instanceof FirebaseFirestoreException) {
            String message = e.getMessage();
            if (message != null && message.contains("index")) {
                // Extract the index link from the error message
                int linkStart = message.indexOf("https://");
                if (linkStart != -1) {
                    String indexLink = message.substring(linkStart);
                    // Show dialog with option to create the index
                    showCreateIndexDialog(indexLink);
                    return;
                }
            }
        }

        // Default error handling
        showEmptyState();
        Toast.makeText(getContext(), "Error loading workouts. Please try again later.", Toast.LENGTH_SHORT).show();
    }

    private void showCreateIndexDialog(String indexLink) {
        if (!isAdded()) return;

        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Index Required")
                .setMessage("This query requires a Firestore index. You need to create it to view your workout history.")
                .setPositiveButton("Create Index", (dialog, which) -> {
                    // Open the browser with the index creation link
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(indexLink));
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEmptyState() {
        if (!isAdded()) return;

        tvNoWorkouts.setVisibility(View.VISIBLE);
        rvWorkouts.setVisibility(View.GONE);
        updateWorkoutStatistics(0, 0, 0);
    }

    private void updateWorkoutStatistics(int workoutCount, long totalDuration, int totalExercises) {
        if (!isAdded()) return;

        try {
            if (tvTotalWorkouts != null) {
                tvTotalWorkouts.setText(String.valueOf(workoutCount));
            }
            if (tvTotalDuration != null) {
                tvTotalDuration.setText(TimeUtils.formatDuration(totalDuration));
            }
            if (tvTotalExercises != null) {
                tvTotalExercises.setText(String.valueOf(totalExercises));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics", e);
        }
    }

    @Override
    public void onWorkoutClick(Workout workout) {
        if (isAdded() && workout != null && workout.getId() != null) {
            WorkoutDetailDialogFragment dialogFragment =
                    WorkoutDetailDialogFragment.newInstance(workout.getId());
            dialogFragment.show(getParentFragmentManager(), "workout_detail");
        }
    }
}