package com.jian.simplefitv1.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.firebase.firestore.Exclude;

/**
 * Model class representing a workout session
 */
public class Workout {
    private String id;
    private String userId;
    private String routineId;
    private String routineName;
    private long startTime;
    private long endTime;
    private long duration;
    private int exerciseCount;
    private Map<String, List<WorkoutSet>> exercises; // Map of exercise IDs to sets
    private boolean completed;
    private String notes;

    /**
     * Required empty constructor for Firestore
     */
    public Workout() {
        exercises = new HashMap<>();
        completed = false;
    }

    /**
     * Constructor with all fields
     */
    public Workout(String id, String userId, String routineId, String routineName,
                  long startTime, long endTime, long duration,
                  Map<String, List<WorkoutSet>> exercises, boolean completed, String notes) {
        this.id = id;
        this.userId = userId;
        this.routineId = routineId;
        this.routineName = routineName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = duration;
        this.exercises = exercises != null ? exercises : new HashMap<>();
        this.completed = completed;
        this.notes = notes;

        // Calculate exercise count
        this.exerciseCount = this.exercises.size();
    }

    // The current Workout model already has a Map<String, List<WorkoutSet>> exercises field that maps
// exercise IDs to lists of sets, so it's already structured to handle multiple exercises.
// Let's add more utility methods to better work with exercises.

    /**
     * Add an exercise to this workout (without any sets)
     * @param exerciseId The exercise ID to add
     */
    public void addExercise(String exerciseId) {
        if (!exercises.containsKey(exerciseId)) {
            exercises.put(exerciseId, new ArrayList<>());
            this.exerciseCount = this.exercises.size();
        }
    }

    /**
     * Remove an exercise and all its sets from this workout
     * @param exerciseId The exercise ID to remove
     * @return true if removed, false if not found
     */
    public boolean removeExercise(String exerciseId) {
        boolean removed = exercises.remove(exerciseId) != null;
        if (removed) {
            this.exerciseCount = this.exercises.size();
        }
        return removed;
    }

    /**
     * Get all exercise IDs in this workout
     * @return Set of exercise IDs
     */
    @Exclude // This won't be stored in Firestore directly
    public Set<String> getExerciseIds() {
        return exercises.keySet();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoutineId() {
        return routineId;
    }

    public void setRoutineId(String routineId) {
        this.routineId = routineId;
    }

    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(String routineName) {
        this.routineName = routineName;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Map<String, List<WorkoutSet>> getExercises() {
        return exercises;
    }

    public void setExercises(Map<String, List<WorkoutSet>> exercises) {
        this.exercises = exercises != null ? exercises : new HashMap<>();
        this.exerciseCount = this.exercises.size();
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Add a set to an exercise in this workout
     * @param exerciseId The exercise ID
     * @param set The workout set to add
     */
    public void addSet(String exerciseId, WorkoutSet set) {
        if (!exercises.containsKey(exerciseId)) {
            exercises.put(exerciseId, new ArrayList<>());
        }
        exercises.get(exerciseId).add(set);
    }

    /**
     * Get all sets for a specific exercise
     * @param exerciseId The exercise ID
     * @return List of workout sets, or empty list if not found
     */
    public List<WorkoutSet> getSetsForExercise(String exerciseId) {
        return exercises.getOrDefault(exerciseId, new ArrayList<>());
    }

    /**
     * Calculate overall completion percentage for the workout
     * @return Percentage from 0 to 100
     */
    public int calculateCompletionPercentage() {
        if (exercises.isEmpty()) {
            return 0;
        }

        int totalSets = 0;
        int completedSets = 0;

        for (List<WorkoutSet> sets : exercises.values()) {
            totalSets += sets.size();
            for (WorkoutSet set : sets) {
                if (set.isCompleted()) {
                    completedSets++;
                }
            }
        }

        return totalSets > 0 ? (completedSets * 100) / totalSets : 0;
    }
}
