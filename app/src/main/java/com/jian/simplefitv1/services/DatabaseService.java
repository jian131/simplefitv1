package com.jian.simplefitv1.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.MuscleGroup;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;
import com.jian.simplefitv1.models.User;
import com.jian.simplefitv1.models.Workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for handling operations with Firestore database
 */
public class DatabaseService {

    private static final String TAG = "DatabaseService";

    // Firebase instances
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    // Collection references
    private CollectionReference usersRef;
    private CollectionReference exercisesRef;
    private CollectionReference muscleGroupsRef;
    private CollectionReference routinesRef;

    /**
     * Constructor initializes Firebase instances and references
     */
    public DatabaseService() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        usersRef = db.collection("users");
        exercisesRef = db.collection("exercises");
        muscleGroupsRef = db.collection("muscleGroups");
        routinesRef = db.collection("routines");
    }

    /**
     * Get the current authenticated user
     * @return Current FirebaseUser or null if not authenticated
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Check if user is authenticated
     * @return true if user is logged in, false otherwise
     */
    public boolean isUserAuthenticated() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Get user data from Firestore
     * @param userId User ID
     * @param listener Callback for completion
     */
    public void getUserData(String userId, OnUserDataListener listener) {
        usersRef.document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                User user = documentSnapshot.toObject(User.class);
                listener.onUserDataLoaded(user);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting user data", e);
                listener.onError(e);
            });
    }

    /**
     * Save user data to Firestore
     * @param user User object to save
     * @param listener Callback for completion
     */
    public void saveUserData(User user, OnCompleteListener<Void> listener) {
        usersRef.document(user.getUserId()).set(user)
            .addOnCompleteListener(listener);
    }

    /**
     * Get all exercises from Firestore
     * @param listener Callback with list of exercises
     */
    public void getAllExercises(OnExercisesLoadedListener listener) {
        exercisesRef.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Exercise> exercises = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Exercise exercise = document.toObject(Exercise.class);
                    if (exercise != null) {
                        exercise.setId(document.getId());
                        exercises.add(exercise);
                    }
                }
                listener.onExercisesLoaded(exercises);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting exercises", e);
                listener.onError(e);
            });
    }

    /**
     * Get exercises by muscle group
     * @param muscleGroupId Muscle group ID
     * @param listener Callback with list of exercises
     */
    public void getExercisesByMuscleGroup(String muscleGroupId, OnExercisesLoadedListener listener) {
        exercisesRef.whereArrayContains("muscleGroups", muscleGroupId).get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Exercise> exercises = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Exercise exercise = document.toObject(Exercise.class);
                    if (exercise != null) {
                        exercise.setId(document.getId());
                        exercises.add(exercise);
                    }
                }
                listener.onExercisesLoaded(exercises);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting exercises by muscle group", e);
                listener.onError(e);
            });
    }

    /**
     * Get all muscle groups from Firestore
     * @param listener Callback with list of muscle groups
     */
    public void getAllMuscleGroups(OnMuscleGroupsLoadedListener listener) {
        muscleGroupsRef.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<MuscleGroup> muscleGroups = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    MuscleGroup muscleGroup = document.toObject(MuscleGroup.class);
                    if (muscleGroup != null) {
                        muscleGroup.setId(document.getId());
                        muscleGroups.add(muscleGroup);
                    }
                }
                listener.onMuscleGroupsLoaded(muscleGroups);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting muscle groups", e);
                listener.onError(e);
            });
    }

    /**
     * Save a routine to Firestore
     * @param routine Routine to save
     * @param exercises List of exercises for this routine
     * @param listener Callback for completion
     */
    public void saveRoutine(Routine routine, List<RoutineExercise> exercises, OnRoutineSavedListener listener) {
        // Check if user is authenticated
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("User not authenticated"));
            return;
        }

        // Set user ID if not set
        if (routine.getUserId() == null || routine.getUserId().isEmpty()) {
            routine.setUserId(currentUser.getUid());
        }

        // Set timestamps if not set
        long now = System.currentTimeMillis();
        if (routine.getCreatedAt() == 0) {
            routine.setCreatedAt(now);
        }
        routine.setUpdatedAt(now);

        // Set exercise count
        routine.setExerciseCount(exercises.size());

        // Create or update routine document
        DocumentReference routineRef;
        if (routine.getId() != null && !routine.getId().isEmpty()) {
            routineRef = routinesRef.document(routine.getId());
        } else {
            routineRef = routinesRef.document();
            routine.setId(routineRef.getId());
        }

        // Save routine to Firestore
        routineRef.set(routine)
            .addOnSuccessListener(aVoid -> {
                // Save exercises for this routine
                saveRoutineExercises(routineRef, exercises, listener);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving routine", e);
                listener.onError(e);
            });
    }

    /**
     * Save exercises for a routine
     * @param routineRef Reference to routine document
     * @param exercises List of exercises to save
     * @param listener Callback for completion
     */
    private void saveRoutineExercises(DocumentReference routineRef,
                                     List<RoutineExercise> exercises,
                                     OnRoutineSavedListener listener) {
        CollectionReference exercisesRef = routineRef.collection("exercises");

        // First delete all existing exercises
        exercisesRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Create a batch to delete old exercises
            com.google.firebase.firestore.WriteBatch batch = db.batch();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                batch.delete(document.getReference());
            }

            // Execute the batch
            batch.commit().addOnSuccessListener(aVoid -> {
                // Now add new exercises
                addRoutineExercises(exercisesRef, exercises, listener);
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error deleting existing exercises", e);
                listener.onError(e);
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error getting existing exercises", e);
            listener.onError(e);
        });
    }

    /**
     * Add exercises to a routine
     * @param exercisesRef Reference to exercises collection of a routine
     * @param exercises List of exercises to add
     * @param listener Callback for completion
     */
    private void addRoutineExercises(CollectionReference exercisesRef,
                                    List<RoutineExercise> exercises,
                                    OnRoutineSavedListener listener) {
        // Create a batch to add exercises
        com.google.firebase.firestore.WriteBatch batch = db.batch();

        // Add each exercise to the batch
        for (int i = 0; i < exercises.size(); i++) {
            RoutineExercise exercise = exercises.get(i);
            exercise.setOrder(i); // Ensure order is set correctly
            DocumentReference docRef = exercisesRef.document();
            batch.set(docRef, exercise);
        }

        // Execute the batch
        batch.commit().addOnSuccessListener(aVoid -> {
            listener.onRoutineSaved();
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error adding new exercises", e);
            listener.onError(e);
        });
    }

    /**
     * Get all routines for current user
     * @param listener Callback with list of routines
     */
    public void getUserRoutines(OnRoutinesLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("User not authenticated"));
            return;
        }

        routinesRef.whereEqualTo("userId", currentUser.getUid())
                  .orderBy("updatedAt", Query.Direction.DESCENDING)
                  .get()
                  .addOnSuccessListener(queryDocumentSnapshots -> {
                      List<Routine> routines = new ArrayList<>();
                      for (DocumentSnapshot document : queryDocumentSnapshots) {
                          Routine routine = document.toObject(Routine.class);
                          if (routine != null) {
                              routine.setId(document.getId());
                              routines.add(routine);
                          }
                      }
                      listener.onRoutinesLoaded(routines);
                  })
                  .addOnFailureListener(e -> {
                      Log.e(TAG, "Error getting user routines", e);
                      listener.onError(e);
                  });
    }

    /**
     * Save a workout to Firestore
     * @param workout Workout to save
     * @param listener Callback for completion
     */
    public void saveWorkout(Workout workout, OnWorkoutSavedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("User not authenticated"));
            return;
        }

        // Ensure user ID is set
        if (workout.getUserId() == null || workout.getUserId().isEmpty()) {
            workout.setUserId(currentUser.getUid());
        }

        // Create reference to user's workouts collection
        CollectionReference workoutsRef = usersRef.document(currentUser.getUid()).collection("workouts");

        // Create or update workout document
        DocumentReference workoutRef;
        if (workout.getId() != null && !workout.getId().isEmpty()) {
            workoutRef = workoutsRef.document(workout.getId());
        } else {
            workoutRef = workoutsRef.document();
            workout.setId(workoutRef.getId());
        }

        // Save workout to Firestore
        workoutRef.set(workout)
            .addOnSuccessListener(aVoid -> {
                listener.onWorkoutSaved();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving workout", e);
                listener.onError(e);
            });
    }

    /**
     * Get user workout history
     * @param limit Maximum number of workouts to retrieve (0 for no limit)
     * @param listener Callback with list of workouts
     */
    public void getUserWorkouts(int limit, OnWorkoutsLoadedListener listener) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError(new Exception("User not authenticated"));
            return;
        }

        // Reference to user's workouts collection
        CollectionReference workoutsRef = usersRef.document(currentUser.getUid()).collection("workouts");

        // Create query
        Query query = workoutsRef.orderBy("startTime", Query.Direction.DESCENDING);
        if (limit > 0) {
            query = query.limit(limit);
        }

        // Execute query
        query.get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Workout> workouts = new ArrayList<>();
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    Workout workout = document.toObject(Workout.class);
                    if (workout != null) {
                        workout.setId(document.getId());
                        workouts.add(workout);
                    }
                }
                listener.onWorkoutsLoaded(workouts);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting user workouts", e);
                listener.onError(e);
            });
    }

    /**
     * Delete a routine from Firestore
     * @param routineId ID of routine to delete
     * @param listener Callback for completion
     */
    public void deleteRoutine(String routineId, OnSuccessListener<Void> successListener,
                             OnFailureListener failureListener) {
        routinesRef.document(routineId).delete()
            .addOnSuccessListener(successListener)
            .addOnFailureListener(failureListener);
    }

    /**
     * Interface for user data callbacks
     */
    public interface OnUserDataListener {
        void onUserDataLoaded(User user);
        void onError(Exception e);
    }

    /**
     * Interface for exercises callbacks
     */
    public interface OnExercisesLoadedListener {
        void onExercisesLoaded(List<Exercise> exercises);
        void onError(Exception e);
    }

    /**
     * Interface for muscle groups callbacks
     */
    public interface OnMuscleGroupsLoadedListener {
        void onMuscleGroupsLoaded(List<MuscleGroup> muscleGroups);
        void onError(Exception e);
    }

    /**
     * Interface for routine callbacks
     */
    public interface OnRoutinesLoadedListener {
        void onRoutinesLoaded(List<Routine> routines);
        void onError(Exception e);
    }

    /**
     * Interface for routine saved callback
     */
    public interface OnRoutineSavedListener {
        void onRoutineSaved();
        void onError(Exception e);
    }

    /**
     * Interface for workout callbacks
     */
    public interface OnWorkoutsLoadedListener {
        void onWorkoutsLoaded(List<Workout> workouts);
        void onError(Exception e);
    }

    /**
     * Interface for workout saved callback
     */
    public interface OnWorkoutSavedListener {
        void onWorkoutSaved();
        void onError(Exception e);
    }
}
