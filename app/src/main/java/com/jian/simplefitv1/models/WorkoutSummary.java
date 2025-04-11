package com.jian.simplefitv1.models;

public class WorkoutSummary {

    private String userId;
    private int totalWorkouts;
    private long totalTimeMinutes;
    private int totalExercises;
    private int totalSets;
    private int weeklyGoal;
    private int weeklyProgress;

    // Required empty constructor for Firestore
    public WorkoutSummary() {
        this.totalWorkouts = 0;
        this.totalTimeMinutes = 0;
        this.totalExercises = 0;
        this.totalSets = 0;
        this.weeklyGoal = 3;
        this.weeklyProgress = 0;
    }

    public WorkoutSummary(String userId) {
        this();
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getTotalWorkouts() {
        return totalWorkouts;
    }

    public void setTotalWorkouts(int totalWorkouts) {
        this.totalWorkouts = totalWorkouts;
    }

    public long getTotalTimeMinutes() {
        return totalTimeMinutes;
    }

    public void setTotalTimeMinutes(long totalTimeMinutes) {
        this.totalTimeMinutes = totalTimeMinutes;
    }

    public int getTotalExercises() {
        return totalExercises;
    }

    public void setTotalExercises(int totalExercises) {
        this.totalExercises = totalExercises;
    }

    public int getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(int totalSets) {
        this.totalSets = totalSets;
    }

    public int getWeeklyGoal() {
        return weeklyGoal;
    }

    public void setWeeklyGoal(int weeklyGoal) {
        this.weeklyGoal = weeklyGoal;
    }

    public int getWeeklyProgress() {
        return weeklyProgress;
    }

    public void setWeeklyProgress(int weeklyProgress) {
        this.weeklyProgress = weeklyProgress;
    }

    // Add workout stats
    public void addWorkout(int durationMinutes, int exercises, int sets) {
        totalWorkouts++;
        totalTimeMinutes += durationMinutes;
        totalExercises += exercises;
        totalSets += sets;
        weeklyProgress++; // Reset weekly on a scheduler
    }
}