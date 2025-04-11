package com.jian.simplefitv1.models;

/**
 * Model class representing a single set of an exercise in a workout
 */
public class WorkoutSet {
    private int reps;
    private float weight;
    private boolean completed;
    private long timestamp;

    /**
     * Required empty constructor for Firestore
     */
    public WorkoutSet() {
        this.reps = 0;
        this.weight = 0;
        this.completed = false;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor with all fields
     */
    public WorkoutSet(int reps, float weight, boolean completed, long timestamp) {
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
        this.timestamp = timestamp;
    }

    /**
     * Constructor with just reps and weight
     */
    public WorkoutSet(int reps, float weight) {
        this(reps, weight, false, System.currentTimeMillis());
    }

    // Getters and setters
    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Calculate one rep max (Epley formula)
     * @return Estimated one rep max or 0 if reps is 0
     */
    public float calculateOneRepMax() {
        if (reps == 0) return 0;

        // Epley formula: 1RM = w * (1 + r/30)
        return weight * (1 + (float)reps / 30);
    }

    @Override
    public String toString() {
        return reps + " x " + weight + "kg" + (completed ? " ✓" : "");
    }
}
