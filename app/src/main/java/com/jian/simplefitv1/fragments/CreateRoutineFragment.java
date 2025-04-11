package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import static android.app.Activity.RESULT_OK;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.ExerciseLibraryActivity;
import com.jian.simplefitv1.activities.RoutineActivity;
import com.jian.simplefitv1.adapters.RoutineExerciseAdapter;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRoutineFragment extends Fragment {

    private static final String TAG = "CreateRoutineFragment";
    private static final int EXERCISE_SELECTION_REQUEST = 100;

    private EditText etRoutineName, etRoutineDescription;
    private Button btnSaveRoutine, btnCancel;
    private RecyclerView rvExercises;
    private FloatingActionButton fabAddExercise;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String editRoutineId;
    private boolean isEditMode = false;
    private Routine currentRoutine;

    private List<RoutineExercise> exerciseList = new ArrayList<>();
    private Map<String, Exercise> exerciseDetailsMap = new HashMap<>();
    private RoutineExerciseAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if we are editing an existing routine
        if (getArguments() != null) {
            editRoutineId = getArguments().getString("ROUTINE_ID");
            isEditMode = (editRoutineId != null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_routine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etRoutineName = view.findViewById(R.id.et_routine_name);
        etRoutineDescription = view.findViewById(R.id.et_routine_description);
        rvExercises = view.findViewById(R.id.rv_exercises);
        fabAddExercise = view.findViewById(R.id.fab_add_exercise);
        btnSaveRoutine = view.findViewById(R.id.btn_save_routine);
        btnCancel = view.findViewById(R.id.btn_cancel);

        // Set up RecyclerView
        setupRecyclerView();

        // Set up click listeners
        setupClickListeners();

        // Load routine data if in edit mode
        if (isEditMode) {
            loadRoutineData();
        }
    }

    private void setupRecyclerView() {
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoutineExerciseAdapter(getContext(), exerciseList, exerciseDetailsMap, true);
        rvExercises.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAddExercise.setOnClickListener(v -> openExerciseSelectionActivity());
        btnSaveRoutine.setOnClickListener(v -> saveRoutine());
        btnCancel.setOnClickListener(v -> getActivity().onBackPressed());
    }

    private void loadRoutineData() {
        if (editRoutineId == null) return;

        db.collection("routines").document(editRoutineId).get()
            .addOnSuccessListener(documentSnapshot -> {
                currentRoutine = documentSnapshot.toObject(Routine.class);
                if (currentRoutine != null) {
                    etRoutineName.setText(currentRoutine.getName());
                    etRoutineDescription.setText(currentRoutine.getDescription());

                    // Load exercises for this routine
                    loadRoutineExercises();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error loading routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void loadRoutineExercises() {
        db.collection("routines").document(editRoutineId)
            .collection("exercises").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                exerciseList.clear();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    RoutineExercise exercise = document.toObject(RoutineExercise.class);
                    exercise.setId(document.getId());
                    exerciseList.add(exercise);

                    // Load full exercise details
                    loadExerciseDetails(exercise.getExerciseId());
                }

                adapter.notifyDataSetChanged();
            })
            .addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Error loading exercises: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void loadExerciseDetails(String exerciseId) {
        db.collection("exercises").document(exerciseId).get()
            .addOnSuccessListener(documentSnapshot -> {
                Exercise exercise = documentSnapshot.toObject(Exercise.class);
                if (exercise != null) {
                    exerciseDetailsMap.put(exerciseId, exercise);
                    adapter.notifyDataSetChanged();
                }
            });
    }

    private void openExerciseSelectionActivity() {
        Intent intent = new Intent(getActivity(), ExerciseLibraryActivity.class);
        intent.putExtra("SELECTION_MODE", true);
        startActivityForResult(intent, EXERCISE_SELECTION_REQUEST);
    }

    private void saveRoutine() {
        String name = etRoutineName.getText().toString().trim();
        String description = etRoutineDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etRoutineName.setError("Name is required");
            return;
        }

        if (exerciseList.isEmpty()) {
            Toast.makeText(getContext(), "Add at least one exercise", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser == null) {
            Toast.makeText(getContext(), "You must be logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable buttons to prevent multiple submissions
        btnSaveRoutine.setEnabled(false);

        // Create or update routine object
        Routine routine;
        if (isEditMode && currentRoutine != null) {
            routine = currentRoutine;
            routine.setName(name);
            routine.setDescription(description);
            routine.setUpdatedAt(System.currentTimeMillis());
        } else {
            routine = new Routine();
            routine.setUserId(currentUser.getUid());
            routine.setName(name);
            routine.setDescription(description);
            routine.setCreatedAt(System.currentTimeMillis());
            routine.setUpdatedAt(System.currentTimeMillis());
        }

        // Calculate estimated time
        int estimatedMinutes = 0;
        for (RoutineExercise exercise : exerciseList) {
            // Each set takes roughly 1 minute
            estimatedMinutes += exercise.getSets() * 1;
        }
        routine.setEstimatedMinutes(estimatedMinutes);

        // Save to Firestore
        final DocumentReference routineRef;
        if (isEditMode) {
            routineRef = db.collection("routines").document(editRoutineId);
        } else {
            routineRef = db.collection("routines").document();
        }

        routineRef.set(routine)
            .addOnSuccessListener(aVoid -> {
                String routineId = routineRef.getId();

                // Save all exercises
                saveExercisesToRoutine(routineId);
            })
            .addOnFailureListener(e -> {
                btnSaveRoutine.setEnabled(true);
                Toast.makeText(getContext(), "Error saving routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void saveExercisesToRoutine(String routineId) {
        // First, delete existing exercises if in edit mode
        if (isEditMode) {
            db.collection("routines").document(routineId)
                .collection("exercises")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Delete each exercise document
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }

                    // Now add the new/updated exercises
                    addExercisesToRoutine(routineId);
                })
                .addOnFailureListener(e -> {
                    btnSaveRoutine.setEnabled(true);
                    Toast.makeText(getContext(), "Error updating exercises: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        } else {
            // Just add the exercises
            addExercisesToRoutine(routineId);
        }
    }

    private void addExercisesToRoutine(String routineId) {
        // Counter to track completed operations
        final int[] completedCount = {0};
        final int totalExercises = exerciseList.size();

        for (RoutineExercise exercise : exerciseList) {
            DocumentReference exerciseRef = db.collection("routines").document(routineId)
                .collection("exercises").document();

            exerciseRef.set(exercise)
                .addOnSuccessListener(aVoid -> {
                    completedCount[0]++;

                    // Check if all exercises have been saved
                    if (completedCount[0] == totalExercises) {
                        btnSaveRoutine.setEnabled(true);
                        Toast.makeText(getContext(), isEditMode ? "Routine updated" : "Routine created", Toast.LENGTH_SHORT).show();

                        // Navigate to routine detail
                        Intent intent = new Intent(getActivity(), RoutineActivity.class);
                        intent.putExtra("ROUTINE_ID", routineId);
                        startActivity(intent);

                        // Go back to routines list
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(e -> {
                    btnSaveRoutine.setEnabled(true);
                    Toast.makeText(getContext(), "Error saving exercise: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXERCISE_SELECTION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                String exerciseId = data.getStringExtra("EXERCISE_ID");
                if (exerciseId != null) {
                    // Add exercise to list
                    RoutineExercise routineExercise = new RoutineExercise();
                    routineExercise.setExerciseId(exerciseId);
                    routineExercise.setOrder(exerciseList.size());
                    routineExercise.setSets(3); // Default 3 sets
                    routineExercise.setReps(10); // Default 10 reps

                    exerciseList.add(routineExercise);
                    loadExerciseDetails(exerciseId);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    // Factory method to create a new instance for editing
    public static CreateRoutineFragment newInstance(String routineId) {
        CreateRoutineFragment fragment = new CreateRoutineFragment();
        Bundle args = new Bundle();
        args.putString("ROUTINE_ID", routineId);
        fragment.setArguments(args);
        return fragment;
    }
}
