package com.jian.simplefitv1.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.adapters.WorkoutExerciseAdapter;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.models.WorkoutSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutActivity";

    // UI Components
    private Toolbar toolbar;
    private Chronometer chronometer;
    private TextView tvRoutineName;
    private RecyclerView rvExercises;
    private Button btnFinishWorkout;
    private FloatingActionButton fabAddExercise;

    // Data
    private Workout currentWorkout;
    private Routine selectedRoutine;
    private List<Exercise> workoutExercises = new ArrayList<>();
    private WorkoutExerciseAdapter adapter;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Workout timing
    private boolean isWorkoutActive = false;
    private long workoutStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup UI
        initializeViews();
        setupToolbar();

        // Initialize workout data
        setupWorkout();

        // Setup RecyclerView
        setupRecyclerView();

        // Set listeners
        setupListeners();

        // Start workout timer
        startWorkout();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        chronometer = findViewById(R.id.chronometer);
        tvRoutineName = findViewById(R.id.tv_routine_name);
        rvExercises = findViewById(R.id.rv_exercises);
        btnFinishWorkout = findViewById(R.id.btn_finish_workout);
        fabAddExercise = findViewById(R.id.fab_add_exercise);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Current Workout");
        }
    }

    private void setupWorkout() {
        // Check if a routine was passed
        String routineId = getIntent().getStringExtra("ROUTINE_ID");

        if (routineId != null) {
            // Load routine from Firestore
            loadRoutine(routineId);
        } else {
            // Create a new empty workout
            tvRoutineName.setText("Quick Workout");
            currentWorkout = new Workout();
            currentWorkout.setUserId(mAuth.getCurrentUser().getUid());
            currentWorkout.setStartTime(new Date().getTime());
        }
    }

    private void loadRoutine(String routineId) {
        db.collection("routines").document(routineId).get()
            .addOnSuccessListener(documentSnapshot -> {
                selectedRoutine = documentSnapshot.toObject(Routine.class);
                if (selectedRoutine != null) {
                    tvRoutineName.setText(selectedRoutine.getName());
                    currentWorkout = new Workout();
                    currentWorkout.setUserId(mAuth.getCurrentUser().getUid());
                    currentWorkout.setRoutineId(routineId);
                    currentWorkout.setStartTime(new Date().getTime());

                    // Load exercises for this routine
                    loadRoutineExercises(routineId);
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading routine: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
    }

    private void loadRoutineExercises(String routineId) {
        db.collection("routines").document(routineId)
            .collection("exercises").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<RoutineExercise> routineExercises = queryDocumentSnapshots.toObjects(RoutineExercise.class);

                // For each routine exercise, fetch the actual exercise details
                for (RoutineExercise routineExercise : routineExercises) {
                    db.collection("exercises").document(routineExercise.getExerciseId()).get()
                        .addOnSuccessListener(exerciseSnapshot -> {
                            Exercise exercise = exerciseSnapshot.toObject(Exercise.class);
                            if (exercise != null) {
                                workoutExercises.add(exercise);
                                adapter.notifyDataSetChanged();
                            }
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading exercises: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
    }

    private void setupRecyclerView() {
        adapter = new WorkoutExerciseAdapter(this, workoutExercises);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(adapter);
    }

    private void setupListeners() {
        btnFinishWorkout.setOnClickListener(v -> finishWorkout());
        fabAddExercise.setOnClickListener(v -> openAddExerciseDialog());
    }

    private void startWorkout() {
        isWorkoutActive = true;
        workoutStartTime = SystemClock.elapsedRealtime();
        chronometer.setBase(workoutStartTime);
        chronometer.start();
    }

    private void finishWorkout() {
        if (!isWorkoutActive) return;

        isWorkoutActive = false;
        chronometer.stop();

        // Calculate workout duration
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        currentWorkout.setDuration(elapsedMillis);
        currentWorkout.setEndTime(new Date().getTime());

        // Show completion dialog
        showWorkoutCompletionDialog();
    }

    private void showWorkoutCompletionDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Workout Completed")
            .setMessage("Great job! You've completed your workout.")
            .setPositiveButton("Save", (dialog, which) -> saveWorkout())
            .setNegativeButton("Discard", (dialog, which) -> finish())
            .setCancelable(false)
            .show();
    }

    private void saveWorkout() {
        // Save workout to Firestore
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection("workouts").add(currentWorkout)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving workout: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void openAddExerciseDialog() {
        Intent intent = new Intent(this, ExerciseLibraryActivity.class);
        intent.putExtra("ADD_TO_WORKOUT", true);
        startActivityForResult(intent, 100);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isWorkoutActive) {
            new AlertDialog.Builder(this)
                .setTitle("Exit Workout")
                .setMessage("Are you sure you want to exit? Your current workout will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
        } else {
            super.onBackPressed();
        }
    }
}
