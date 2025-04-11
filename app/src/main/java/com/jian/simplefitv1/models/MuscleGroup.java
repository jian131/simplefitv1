package com.jian.simplefitv1.models;

public class MuscleGroup {
    private String id;
    private String name;
    private String description;
    private String drawableResourceName;

    // Required empty constructor for Firestore
    public MuscleGroup() {
    }

    public MuscleGroup(String id, String name, String description, String drawableResourceName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.drawableResourceName = drawableResourceName;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getDrawableResourceName() {
        return drawableResourceName;
    }

    public void setDrawableResourceName(String drawableResourceName) {
        this.drawableResourceName = drawableResourceName;
    }
}
