package com.jian.simplefitv1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.adapters.RoutineExerciseAdapter;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutineActivity extends AppCompatActivity {

    private static final String TAG = "RoutineActivity";
    private static final int ADD_EXERCISE_REQUEST = 101;

    // UI Components
    private Toolbar toolbar;
    private TextView tvRoutineName, tvRoutineDescription, tvEstimatedTime;
    private RecyclerView rvExercises;
    private Button btnStartRoutine;
    private FloatingActionButton fabAddExercise;

    // Data
    private String routineId;
    private Routine routine;
    private List<RoutineExercise> routineExercises = new ArrayList<>();
    private RoutineExerciseAdapter adapter;
    private Map<String, Exercise> exerciseMap = new HashMap<>();

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);

        // Get routine ID from intent
        routineId = getIntent().getStringExtra("ROUTINE_ID");
        if (routineId == null) {
            Toast.makeText(this, "Error: Routine not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI
        initializeViews();
        setupToolbar();

        // Setup RecyclerView
        setupRecyclerView();

        // Load routine data
        loadRoutineData();

        // Set listeners
        setupListeners();
    }

    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar);
        tvRoutineName = findViewById(R.id.tv_routine_name);
        tvRoutineDescription = findViewById(R.id.tv_routine_description);
        tvEstimatedTime = findViewById(R.id.tv_estimated_time);
        rvExercises = findViewById(R.id.rv_exercises);
        btnStartRoutine = findViewById(R.id.btn_start_routine);
        fabAddExercise = findViewById(R.id.fab_add_exercise);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Routine Details");
        }
    }

    private void setupRecyclerView() {
        adapter = new RoutineExerciseAdapter(this, routineExercises, exerciseMap);
        rvExercises.setLayoutManager(new LinearLayoutManager(this));
        rvExercises.setAdapter(adapter);
    }

    private void loadRoutineData() {
        // Load the routine data
        db.collection("routines").document(routineId).get()
            .addOnSuccessListener(documentSnapshot -> {
                routine = documentSnapshot.toObject(Routine.class);
                if (routine != null) {
                    updateUI();
                    loadRoutineExercises();
                } else {
                    Toast.makeText(this, "Error: Routine not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading routine: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
                finish();
            });
    }

    private void updateUI() {
        tvRoutineName.setText(routine.getName());
        tvRoutineDescription.setText(routine.getDescription());

        // Estimated time is calculated based on exercises, but display a default for now
        tvEstimatedTime.setText(String.format("Estimated time: %d min", routine.getEstimatedMinutes()));

        // Only show edit button if user is the creator
        if (mAuth.getCurrentUser() != null &&
                mAuth.getCurrentUser().getUid().equals(routine.getUserId())) {
            fabAddExercise.setVisibility(View.VISIBLE);
        } else {
            fabAddExercise.setVisibility(View.GONE);
        }
    }

    private void loadRoutineExercises() {
        // Clear previous data
        routineExercises.clear();
        exerciseMap.clear();

        // Load the exercises in this routine
        db.collection("routines").document(routineId)
            .collection("exercises").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    RoutineExercise routineExercise = document.toObject(RoutineExercise.class);
                    routineExercise.setId(document.getId());
                    routineExercises.add(routineExercise);

                    // Load full exercise details
                    loadExerciseDetails(routineExercise.getExerciseId());
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading exercises: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
    }

    private void loadExerciseDetails(String exerciseId) {
        db.collection("exercises").document(exerciseId).get()
            .addOnSuccessListener(documentSnapshot -> {
                Exercise exercise = documentSnapshot.toObject(Exercise.class);
                if (exercise != null) {
                    exerciseMap.put(exerciseId, exercise);
                    adapter.notifyDataSetChanged();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Error loading exercise details: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            });
    }

    private void setupListeners() {
        btnStartRoutine.setOnClickListener(v -> startRoutineWorkout());
        fabAddExercise.setOnClickListener(v -> openAddExerciseDialog());
    }

    private void startRoutineWorkout() {
        Intent intent = new Intent(this, WorkoutActivity.class);
        intent.putExtra("ROUTINE_ID", routineId);
        startActivity(intent);
    }

    private void openAddExerciseDialog() {
        Intent intent = new Intent(this, ExerciseLibraryActivity.class);
        intent.putExtra("ADD_TO_ROUTINE", true);
        intent.putExtra("ROUTINE_ID", routineId);
        startActivityForResult(intent, ADD_EXERCISE_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Only show edit options if user is the creator
        if (routine != null && mAuth.getCurrentUser() != null &&
                mAuth.getCurrentUser().getUid().equals(routine.getUserId())) {
            getMenuInflater().inflate(R.menu.menu_routine_detail, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_edit) {
            editRoutine();
            return true;
        } else if (itemId == R.id.action_delete) {
            deleteRoutine();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editRoutine() {
        Intent intent = new Intent(this, CreateRoutineActivity.class);
        intent.putExtra("ROUTINE_ID", routineId);
        startActivity(intent);
    }

    private void deleteRoutine() {
        // Show confirmation dialog
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Routine")
            .setMessage("Are you sure you want to delete this routine? This action cannot be undone.")
            .setPositiveButton("Delete", (dialog, which) -> {
                db.collection("routines").document(routineId).delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Routine deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error deleting routine: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_EXERCISE_REQUEST && resultCode == RESULT_OK) {
            // Refresh the exercises list
            loadRoutineExercises();
        }
    }
}
