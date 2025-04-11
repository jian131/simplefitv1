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
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.adapters.ExerciseAdapter;
import com.jian.simplefitv1.adapters.WorkoutSetAdapter;
import com.jian.simplefitv1.data.ExerciseData;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.models.WorkoutSet;
import com.jian.simplefitv1.services.DatabaseService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WorkoutActivity extends AppCompatActivity {

    private static final String TAG = "WorkoutActivity";

    // UI Components
    private Toolbar toolbar;
    private Chronometer chronometer;
    private TextView tvRoutineName;
    private TextView tvExercisesCount;
    private RecyclerView recyclerView;
    private Button btnFinishWorkout;
    private FloatingActionButton fabAddExercise;

    // Data
    private ExerciseAdapter adapter;
    private List<Exercise> workoutExercises;
    private Workout currentWorkout;
    private long workoutStartTime;
    private boolean isWorkoutActive = false;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private DatabaseService databaseService;

    // In your WorkoutActivity class, update the onCreate method:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        databaseService = new DatabaseService();

        // Initialize views
        initializeViews();

        // Setup toolbar
        setupToolbar();

        // Initialize exercise list and adapter
        workoutExercises = new ArrayList<>();
        adapter = new ExerciseAdapter(this, workoutExercises, new ExerciseAdapter.OnExerciseClickListener() {
            @Override
            public void onExerciseClick(Exercise exercise) {
                // Handle exercise click - we need to find the position first
                int position = workoutExercises.indexOf(exercise);
                showExerciseSets(exercise, position);
            }

            @Override
            public void onExerciseRemove(Exercise exercise, int position) {
                removeExerciseFromWorkout(exercise, position);
            }
        }, true); // true to show the remove button

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up listeners
        setupListeners();

        // Set up workout from intent or create a quick workout
        setupWorkout();

        // Start workout timer
        startWorkout();
    }

    /**
     * Remove an exercise from the current workout
     * @param exercise The exercise to remove
     * @param position The position in the RecyclerView
     */
    private void removeExerciseFromWorkout(Exercise exercise, int position) {
        // Confirm removal
        new AlertDialog.Builder(this)
                .setTitle(R.string.remove_exercise)
                .setMessage(getString(R.string.remove_exercise_confirm, exercise.getName()))
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    // Remove from list for adapter
                    workoutExercises.remove(position);

                    // Remove from workout object
                    currentWorkout.removeExercise(exercise.getId());

                    // Update adapter
                    adapter.notifyItemRemoved(position);

                    // Update exercise count
                    currentWorkout.setExerciseCount(workoutExercises.size());
                    updateExerciseCountDisplay();

                    Toast.makeText(this, R.string.exercise_removed, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        chronometer = findViewById(R.id.chronometer);
        tvRoutineName = findViewById(R.id.tv_routine_name);
        tvExercisesCount = findViewById(R.id.tv_exercises_count);
        recyclerView = findViewById(R.id.rv_exercises);
        btnFinishWorkout = findViewById(R.id.btn_finish_workout);
        fabAddExercise = findViewById(R.id.fab_add_exercise);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.workout);
        }
    }

    private void setupListeners() {
        // Finish workout button
        btnFinishWorkout.setOnClickListener(v -> finishWorkout());

        // Add exercise button
        fabAddExercise.setOnClickListener(v -> openAddExerciseDialog());
    }

    private void setupWorkout() {
        // Check if a routine was passed
        String routineId = getIntent().getStringExtra("ROUTINE_ID");

        if (routineId != null) {
            // Load routine from Firestore
            loadRoutine(routineId);
        } else {
            // Create a quick workout with random exercises
            tvRoutineName.setText(getString(R.string.quick_workout));
            currentWorkout = new Workout();

            if (mAuth.getCurrentUser() != null) {
                currentWorkout.setUserId(mAuth.getCurrentUser().getUid());
            }

            currentWorkout.setRoutineName(getString(R.string.quick_workout));
            currentWorkout.setStartTime(new Date().getTime());
            currentWorkout.setExercises(new HashMap<>()); // Initialize exercises map

            // Generate random exercises for a 5-minute workout
            generateQuickWorkout();

            // Update exercise count display
            updateExerciseCountDisplay();
        }
    }

    /**
     * Generate a random selection of exercises for a quick 5-minute workout
     */
    private void generateQuickWorkout() {
        // Get all exercises from ExerciseData
        List<Exercise> allExercises = ExerciseData.getAllExercises();

        if (allExercises.isEmpty()) {
            Toast.makeText(this, "Exercise data not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Clear any existing exercises
        workoutExercises.clear();
        if (currentWorkout.getExercises() == null) {
            currentWorkout.setExercises(new HashMap<>());
        }

        // Shuffle the exercise list to randomize
        Collections.shuffle(allExercises);

        // Select 3-5 exercises for a quick workout (randomly)
        int exerciseCount = new Random().nextInt(3) + 3; // 3 to 5 exercises

        // Limit to available exercises if we don't have enough
        exerciseCount = Math.min(exerciseCount, allExercises.size());

        // Add selected exercises to workout
        for (int i = 0; i < exerciseCount; i++) {
            Exercise exercise = allExercises.get(i);
            workoutExercises.add(exercise);

            // Add default sets for each exercise (3 sets of 10 reps)
            List<WorkoutSet> sets = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                sets.add(new WorkoutSet(10, 0));
            }
            currentWorkout.getExercises().put(exercise.getId(), sets);
        }

        // Set exercise count
        currentWorkout.setExerciseCount(exerciseCount);

        // Update adapter
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        // Set estimated workout duration to 5 minutes
        long fiveMinutesInMillis = TimeUnit.MINUTES.toMillis(5);
        currentWorkout.setDuration(fiveMinutesInMillis);
    }

    private void loadRoutine(String routineId) {
        // Show loading state
        // TODO: Add loading indicator

        // Get routine details
        db.collection("routines").document(routineId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Routine routine = documentSnapshot.toObject(Routine.class);
                    if (routine != null) {
                        // Update UI with routine details
                        tvRoutineName.setText(routine.getName());

                        // Create new workout based on routine
                        currentWorkout = new Workout();
                        currentWorkout.setRoutineId(routineId);
                        currentWorkout.setRoutineName(routine.getName());
                        currentWorkout.setStartTime(new Date().getTime());

                        // Set user ID
                        if (mAuth.getCurrentUser() != null) {
                            currentWorkout.setUserId(mAuth.getCurrentUser().getUid());
                        }

                        // Load routine exercises
                        loadRoutineExercises(routineId);
                    } else {
                        // Routine not found, create a quick workout instead
                        Toast.makeText(this, "Routine not found, creating quick workout", Toast.LENGTH_SHORT).show();
                        setupWorkout();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setupWorkout(); // Fallback to quick workout
                });
    }

    private void loadRoutineExercises(String routineId) {
        db.collection("routines").document(routineId).collection("exercises")
                .orderBy("order")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Clear existing lists
                    workoutExercises.clear();
                    Map<String, List<WorkoutSet>> exerciseSets = new HashMap<>();

                    // Process each exercise in the routine
                    for (RoutineExercise routineExercise : queryDocumentSnapshots.toObjects(RoutineExercise.class)) {
                        String exerciseId = routineExercise.getExerciseId();

                        // Get exercise details
                        Exercise exercise = ExerciseData.getExerciseById(exerciseId);
                        if (exercise != null) {
                            workoutExercises.add(exercise);

                            // Create sets based on routine configuration
                            List<WorkoutSet> sets = new ArrayList<>();
                            for (int i = 0; i < routineExercise.getSets(); i++) {
                                sets.add(new WorkoutSet(routineExercise.getReps(), routineExercise.getWeight()));
                            }

                            exerciseSets.put(exerciseId, sets);
                        }
                    }

                    // Update the workout with exercises and sets
                    currentWorkout.setExercises(exerciseSets);
                    currentWorkout.setExerciseCount(workoutExercises.size());

                    // Update adapter
                    adapter.notifyDataSetChanged();

                    // Update exercise count display
                    updateExerciseCountDisplay();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error loading exercises: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // Fallback to quick workout
                    generateQuickWorkout();
                });
    }

    private void updateExerciseCountDisplay() {
        if (tvExercisesCount != null) {
            tvExercisesCount.setText(getString(R.string.exercises_completed) + " " +
                    currentWorkout.getExerciseCount());
        }
    }

    private void showExerciseSets(Exercise exercise, int position) {
        // Create and configure the dialog for exercise sets
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_exercise_sets, null);

        // Setup dialog title with exercise name
        TextView tvTitle = dialogView.findViewById(R.id.tv_sets_title);
        tvTitle.setText(exercise.getName() + " Sets");

        // Get the RecyclerView for sets
        RecyclerView rvSets = dialogView.findViewById(R.id.rv_sets);
        rvSets.setLayoutManager(new LinearLayoutManager(this));

        // Get the sets for this exercise from workout, or create if not exists
        final String exerciseId = exercise.getId();
        List<WorkoutSet> exerciseSets = currentWorkout.getExercises().get(exerciseId);
        if (exerciseSets == null) {
            exerciseSets = new ArrayList<>();
            // Default: Add 3 sets of 10 reps
            for (int i = 0; i < 3; i++) {
                exerciseSets.add(new WorkoutSet(10, 0));
            }
            currentWorkout.getExercises().put(exerciseId, exerciseSets);
        }

        // Create a final reference for the sets to use in lambda
        final List<WorkoutSet> setsList = exerciseSets;

        // Initialize the adapter variable before setting up listeners
        final WorkoutSetAdapter[] adapterRef = new WorkoutSetAdapter[1];

        // Create adapter with the listener using the reference array
        WorkoutSetAdapter.OnSetChangeListener listener = new WorkoutSetAdapter.OnSetChangeListener() {
            @Override
            public void onSetChanged(int pos, WorkoutSet set) {
                // This method handles both reps and weight changes
                if (pos >= 0 && pos < setsList.size()) {
                    setsList.set(pos, set);
                }
            }

            @Override
            public void onSetCompleted(int pos, WorkoutSet set) {
                if (pos >= 0 && pos < setsList.size()) {
                    setsList.get(pos).setCompleted(set.isCompleted());
                }
            }

            @Override
            public void onSetRemoved(int pos) {
                if (pos >= 0 && pos < setsList.size()) {
                    setsList.remove(pos);
                    // Use the adapter from the reference array
                    if (adapterRef[0] != null) {
                        adapterRef[0].notifyDataSetChanged();
                    }
                }
            }
        };

        // Create the adapter and store it in the reference array
        adapterRef[0] = new WorkoutSetAdapter(this, setsList, exercise.getName(), listener);

        // Set the adapter to the RecyclerView
        rvSets.setAdapter(adapterRef[0]);

        // Setup "Add Set" button
        Button btnAddSet = dialogView.findViewById(R.id.btn_add_set);
        btnAddSet.setOnClickListener(v -> {
            setsList.add(new WorkoutSet(10, 0));
            if (adapterRef[0] != null) {
                adapterRef[0].notifyDataSetChanged();
            }
            final int lastPosition = setsList.size() - 1;
            rvSets.post(() -> rvSets.smoothScrollToPosition(lastPosition));
        });

        // Create and show the dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    // Update the adapter to refresh the exercise list
                    adapter.notifyItemChanged(position);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void startWorkout() {
        workoutStartTime = SystemClock.elapsedRealtime();
        chronometer.setBase(workoutStartTime);
        chronometer.start();
        isWorkoutActive = true;
    }

    /**
     * Finalize workout and save it to database
     */
    private void finishWorkout() {
        if (currentWorkout == null) return;

        // Stop the chronometer
        chronometer.stop();

        // Set workout end time
        currentWorkout.setEndTime(System.currentTimeMillis());

        // Calculate and set workout duration
        long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
        currentWorkout.setDuration(elapsedMillis);

        // Mark as completed
        currentWorkout.setCompleted(true);

        // Calculate and set exercise count (if not already set)
        if (currentWorkout.getExerciseCount() <= 0) {
            currentWorkout.setExerciseCount(workoutExercises.size());
        }

        // Show completion dialog
        showWorkoutCompletionDialog();
    }

    private void showWorkoutCompletionDialog() {
        // Create dialog view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_workout_completed, null);

        // Set up dialog content
        TextView tvDuration = dialogView.findViewById(R.id.tv_stats_duration);
        TextView tvExercises = dialogView.findViewById(R.id.tv_stats_exercise_count);
        EditText etNotes = dialogView.findViewById(R.id.et_notes);

        // Set values
        tvDuration.setText(formatDuration(currentWorkout.getDuration()));
        tvExercises.setText(String.valueOf(currentWorkout.getExerciseCount()));

        // Create dialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setView(dialogView)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    // Save notes if entered
                    if (etNotes != null && etNotes.getText() != null) {
                        String notes = etNotes.getText().toString().trim();
                        if (!notes.isEmpty()) {
                            currentWorkout.setNotes(notes);
                        }
                    }
                    saveWorkout();
                })
                .setNegativeButton(R.string.discard, (dialog, which) -> {
                    finish(); // Close activity without saving
                })
                .setCancelable(false); // Prevent dialog dismissal on outside touch

        // Show dialog
        builder.create().show();
    }

    private String formatDuration(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%d:%02d", minutes, seconds);
    }

    private void saveWorkout() {
        // Save workout to Firestore
        databaseService.saveWorkout(currentWorkout, new DatabaseService.OnWorkoutSavedListener() {
            @Override
            public void onWorkoutSaved(Workout workout) {
                Toast.makeText(WorkoutActivity.this, R.string.workout_complete, Toast.LENGTH_SHORT).show();
                finish(); // Close activity after saving
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(WorkoutActivity.this,
                        "Error saving workout: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddExerciseDialog() {
        // Open ExerciseLibraryActivity in selection mode
        Intent intent = new Intent(this, ExerciseLibraryActivity.class);
        intent.putExtra("SELECTION_MODE", true);
        startActivityForResult(intent, 100); // Request code for exercise selection
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Get selected exercise
            String exerciseId = data.getStringExtra("EXERCISE_ID");
            Exercise exercise = ExerciseData.getExerciseById(exerciseId);

            if (exercise != null) {
                // Add to workout
                workoutExercises.add(exercise);

                // Create default sets
                List<WorkoutSet> sets = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    sets.add(new WorkoutSet(10, 0));
                }
                currentWorkout.getExercises().put(exerciseId, sets);

                // Update adapter
                adapter.notifyDataSetChanged();

                // Update exercise count
                currentWorkout.setExerciseCount(workoutExercises.size());
                updateExerciseCountDisplay();
            }
        }
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
            // Confirm exit without saving
            new AlertDialog.Builder(this)
                    .setTitle(R.string.workout_in_progress)
                    .setMessage(R.string.exit_workout_confirm)
                    .setPositiveButton(R.string.yes, (dialog, which) -> super.onBackPressed())
                    .setNegativeButton(R.string.no, null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}