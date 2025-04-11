package com.jian.simplefitv1.models;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private String id;
    private String name;
    private String description;
    private String drawableResourceName;
    private List<String> muscleGroups;
    private String difficulty;
    private List<String> instructions;
    private String youtubeLink;

    // Required empty constructor for Firestore
    public Exercise() {
        muscleGroups = new ArrayList<>();
        instructions = new ArrayList<>();
    }

    public Exercise(String id, String name, String description, String drawableResourceName,
                   List<String> muscleGroups, String difficulty, List<String> instructions,
                   String youtubeLink) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.drawableResourceName = drawableResourceName;
        this.muscleGroups = muscleGroups != null ? muscleGroups : new ArrayList<>();
        this.difficulty = difficulty;
        this.instructions = instructions != null ? instructions : new ArrayList<>();
        this.youtubeLink = youtubeLink;
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

    public List<String> getMuscleGroups() {
        return muscleGroups;
    }

    public void setMuscleGroups(List<String> muscleGroups) {
        this.muscleGroups = muscleGroups;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public String getYoutubeLink() {
        return youtubeLink;
    }

    public void setYoutubeLink(String youtubeLink) {
        this.youtubeLink = youtubeLink;
    }

    public void addMuscleGroup(String muscleGroup) {
        if (muscleGroups == null) {
            muscleGroups = new ArrayList<>();
        }
        muscleGroups.add(muscleGroup);
    }

    public void addInstruction(String instruction) {
        if (instructions == null) {
            instructions = new ArrayList<>();
        }
        instructions.add(instruction);
    }
}
