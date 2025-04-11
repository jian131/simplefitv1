package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.ExerciseLibraryActivity;
import com.jian.simplefitv1.activities.RoutineActivity;
import com.jian.simplefitv1.activities.WorkoutActivity;
import com.jian.simplefitv1.adapters.RoutineAdapter;
import com.jian.simplefitv1.adapters.WorkoutAdapter;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.utils.TimeUtils;
import com.jian.simplefitv1.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final int LIMIT_ROUTINES = 3;
    private static final int LIMIT_WORKOUTS = 3;

    private TextView tvWelcome, tvNoRoutines, tvNoWorkouts;
    private RecyclerView rvRecentRoutines, rvRecentWorkouts;
    private CardView cardQuickStart, cardExploreExercises;
    private Button btnViewAllRoutines, btnViewAllWorkouts;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private List<Routine> recentRoutines = new ArrayList<>();
    private List<Workout> recentWorkouts = new ArrayList<>();
    private RoutineAdapter routineAdapter;
    private WorkoutAdapter workoutAdapter;

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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvWelcome = view.findViewById(R.id.tv_welcome);
        rvRecentRoutines = view.findViewById(R.id.rv_recent_routines);
        rvRecentWorkouts = view.findViewById(R.id.rv_recent_workouts);
        tvNoRoutines = view.findViewById(R.id.tv_no_routines);
        tvNoWorkouts = view.findViewById(R.id.tv_no_workouts);
        cardQuickStart = view.findViewById(R.id.card_quick_start);
        cardExploreExercises = view.findViewById(R.id.card_explore_exercises);
        btnViewAllRoutines = view.findViewById(R.id.btn_view_all_routines);
        btnViewAllWorkouts = view.findViewById(R.id.btn_view_all_workouts);

        // Setup welcome message
        setupWelcomeMessage();

        // Setup RecyclerViews
        setupRecentRoutines();
        setupRecentWorkouts();

        // Setup click listeners
        setupClickListeners();
    }

    private void setupWelcomeMessage() {
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvWelcome.setText(getString(R.string.welcome_user, displayName));
            } else {
                tvWelcome.setText(R.string.welcome);
            }
        }
    }

    private void setupRecentRoutines() {
        rvRecentRoutines.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        routineAdapter = new RoutineAdapter(getContext(), recentRoutines);
        rvRecentRoutines.setAdapter(routineAdapter);

        if (currentUser != null) {
            db.collection("routines")
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(LIMIT_ROUTINES)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        tvNoRoutines.setVisibility(View.GONE);
                        rvRecentRoutines.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Routine routine = document.toObject(Routine.class);
                            routine.setId(document.getId());
                            recentRoutines.add(routine);
                        }
                        routineAdapter.notifyDataSetChanged();
                    } else {
                        tvNoRoutines.setVisibility(View.VISIBLE);
                        rvRecentRoutines.setVisibility(View.GONE);
                    }
                });
        }
    }

    private void setupRecentWorkouts() {
        rvRecentWorkouts.setLayoutManager(new LinearLayoutManager(getContext()));
        workoutAdapter = new WorkoutAdapter(getContext(), recentWorkouts);
        rvRecentWorkouts.setAdapter(workoutAdapter);

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                .collection("workouts")
                .orderBy("startTime", Query.Direction.DESCENDING)
                .limit(LIMIT_WORKOUTS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        tvNoWorkouts.setVisibility(View.GONE);
                        rvRecentWorkouts.setVisibility(View.VISIBLE);

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Workout workout = document.toObject(Workout.class);
                            workout.setId(document.getId());
                            recentWorkouts.add(workout);
                        }
                        workoutAdapter.notifyDataSetChanged();
                    } else {
                        tvNoWorkouts.setVisibility(View.VISIBLE);
                        rvRecentWorkouts.setVisibility(View.GONE);
                    }
                });
        }
    }

    private void setupClickListeners() {
        cardQuickStart.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WorkoutActivity.class);
            startActivity(intent);
        });

        cardExploreExercises.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ExerciseLibraryActivity.class);
            startActivity(intent);
        });

        btnViewAllRoutines.setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                // Sử dụng phương thức điều hướng public nếu có
                ((MainActivity) getActivity()).navigateToFragment(new RoutineFragment());
                // Hoặc chuyển đến bottom navigation item tương ứng
                ((MainActivity) getActivity()).selectNavItem(R.id.nav_routines);
            }
        });

        btnViewAllWorkouts.setOnClickListener(v -> {
            if (getActivity() != null && getActivity() instanceof MainActivity) {
                // Sử dụng phương thức điều hướng public nếu có
                ((MainActivity) getActivity()).navigateToFragment(new WorkoutHistoryFragment());
                // Hoặc chuyển đến bottom navigation item tương ứng
                ((MainActivity) getActivity()).selectNavItem(R.id.nav_history);
            }
        });
    }
}
