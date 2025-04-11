package com.jian.simplefitv1.models;
import com.google.firebase.firestore.Exclude;

/**
 * Model class linking exercises to routines with specific configuration
 */
public class RoutineExercise {
    private String id;
    private String exerciseId;
    private int order;
    private int sets;
    private int reps;
    private float weight;
    private int restSeconds;
    private String notes;


    /**
     * Required empty constructor for Firestore
     */
    public RoutineExercise() {
        this.order = 0;
        this.sets = 3; // Default 3 sets
        this.reps = 10; // Default 10 reps
        this.weight = 0;
        this.restSeconds = 60; // Default 60 seconds rest
    }

    /**
     * Constructor with all fields
     */
    public RoutineExercise(String id, String exerciseId, int order, int sets, int reps,
                          float weight, int restSeconds, String notes) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.order = order;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.restSeconds = restSeconds;
        this.notes = notes;
    }

    /**
     * Basic constructor with essential fields
     */
    public RoutineExercise(String exerciseId, int order, int sets, int reps) {
        this();
        this.exerciseId = exerciseId;
        this.order = order;
        this.sets = sets;
        this.reps = reps;
    }

    /**
     * Get a formatted string representation of the sets and reps
     * @return String in format "3 x 10"
     */
    @Exclude
    public String getSetsRepsString() {
        return sets + " x " + reps;
    }

    /**
     * Get a formatted string representation of the weight
     * @return String in format "10 kg" or empty string if weight is 0
     */
    @Exclude
    public String getWeightString() {
        if (weight <= 0) return "";
        return weight + " kg";
    }



    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(String exerciseId) {
        this.exerciseId = exerciseId;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

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

    public int getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(int restSeconds) {
        this.restSeconds = restSeconds;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }


    /**
     * Get a formatted string representation of the sets and reps
     * @return String in format "3 x 10"
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getSetsRepsString());
        if (weight > 0) {
            sb.append(" @ ").append(getWeightString());
        }
        return sb.toString();
    }
}
