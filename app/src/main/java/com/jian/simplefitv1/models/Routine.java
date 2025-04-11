package com.jian.simplefitv1.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a workout routine
 */
public class Routine {
    private String id;
    private String userId;
    private String name;
    private String description;
    private long createdAt;
    private long updatedAt;
    private int estimatedMinutes;
    private int exerciseCount;
    private int workoutCount; // Added field for number of workouts
    private List<String> workoutIds; // Reference to workouts in this routine
    private List<String> tags;
    private boolean isPublic;

    /**
     * Required empty constructor for Firestore
     */
    public Routine() {
        this.tags = new ArrayList<>();
        this.isPublic = false;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    /**
     * Constructor with all fields
     */
    public Routine(String id, String userId, String name, String description,
                  long createdAt, long updatedAt, int estimatedMinutes,
                  int exerciseCount, List<String> tags, boolean isPublic) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.estimatedMinutes = estimatedMinutes;
        this.exerciseCount = exerciseCount;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.isPublic = isPublic;
    }

    /**
     * Basic constructor with essential fields
     */
    public Routine(String userId, String name, String description) {
        this();
        this.userId = userId;
        this.name = name;
        this.description = description;
    }

    // Add getters and setters for new fields
    public int getWorkoutCount() {
        return workoutCount;
    }


    public void setWorkoutCount(int workoutCount) {
        this.workoutCount = workoutCount;
    }

    public List<String> getWorkoutIds() {
        return workoutIds;
    }

    public void setWorkoutIds(List<String> workoutIds) {
        this.workoutIds = workoutIds != null ? workoutIds : new ArrayList<>();
        this.workoutCount = this.workoutIds.size();
    }

    // Add helper method to add a workout to this routine
    public void addWorkoutId(String workoutId) {
        if (this.workoutIds == null) {
            this.workoutIds = new ArrayList<>();
        }
        if (!this.workoutIds.contains(workoutId)) {
            this.workoutIds.add(workoutId);
            this.workoutCount = this.workoutIds.size();
        }
    }

    // Add helper method to remove a workout from this routine
    public void removeWorkoutId(String workoutId) {
        if (this.workoutIds != null) {
            this.workoutIds.remove(workoutId);
            this.workoutCount = this.workoutIds.size();
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public int getExerciseCount() {
        return exerciseCount;
    }

    public void setExerciseCount(int exerciseCount) {
        this.exerciseCount = exerciseCount;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * Add a tag to this routine
     * @param tag The tag to add
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    /**
     * Check if this routine has a specific tag
     * @param tag The tag to check
     * @return true if the routine has this tag
     */
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    @Override
    public String toString() {
        return name + " (" + exerciseCount + " exercises, " + estimatedMinutes + " min)";
    }
}
