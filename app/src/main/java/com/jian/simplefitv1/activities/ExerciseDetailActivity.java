package com.jian.simplefitv1.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.data.ExerciseData;
import com.jian.simplefitv1.models.Exercise;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

public class ExerciseDetailActivity extends AppCompatActivity {

    private static final String TAG = "ExerciseDetailActivity";
    public static final String EXTRA_EXERCISE_ID = "exercise_id";

    private ImageView ivExerciseImage;
    private TextView tvExerciseName, tvExerciseDescription, tvDifficulty;
    private ChipGroup chipGroupMuscles;
    private RecyclerView rvInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_detail);

        // Initialize views
        ivExerciseImage = findViewById(R.id.iv_exercise_image);
        tvExerciseName = findViewById(R.id.tv_exercise_name);
        tvExerciseDescription = findViewById(R.id.tv_exercise_description);
        tvDifficulty = findViewById(R.id.tv_difficulty);
        chipGroupMuscles = findViewById(R.id.chip_group_muscles);
        rvInstructions = findViewById(R.id.rv_instructions);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get exercise ID from intent
        String exerciseId = getIntent().getStringExtra(EXTRA_EXERCISE_ID);
        if (exerciseId != null) {
            loadExerciseDetails(exerciseId);
        } else {
            Toast.makeText(this, "Error: Exercise not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadExerciseDetails(String exerciseId) {
        // In a real app, you'd get this from Firebase/database
        Exercise exercise = ExerciseData.getExerciseById(exerciseId);

        if (exercise != null) {
            updateUI(exercise);
        } else {
            Toast.makeText(this, "Error: Exercise not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void updateUI(Exercise exercise) {
        // Set exercise name and description
        tvExerciseName.setText(exercise.getName());
        tvExerciseDescription.setText(exercise.getDescription());
        tvDifficulty.setText(exercise.getDifficulty());

        // Set exercise image
        int resourceId = getResources().getIdentifier(
                exercise.getDrawableResourceName(), "drawable", getPackageName());

        if (resourceId != 0) {
            Glide.with(this)
                    .load(resourceId)
                    .into(ivExerciseImage);
        }

        // Add muscle group chips
        for (String muscle : exercise.getMuscleGroups()) {
            Chip chip = new Chip(this);
            chip.setText(muscle);
            chip.setClickable(false);
            chipGroupMuscles.addView(chip);
        }

        // Set up instructions recycler view
        if (exercise.getInstructions() != null && !exercise.getInstructions().isEmpty()) {
            rvInstructions.setLayoutManager(new LinearLayoutManager(this));
            ExerciseInstructionAdapter adapter = new ExerciseInstructionAdapter(exercise.getInstructions());
            rvInstructions.setAdapter(adapter);
        }

        // Set YouTube link if available
        if (!TextUtils.isEmpty(exercise.getYoutubeLink())) {
            // Optionally add a button to view the YouTube tutorial
        }

        // Set the title in the toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(exercise.getName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Inner adapter class for exercise instructions
    private static class ExerciseInstructionAdapter extends RecyclerView.Adapter<ExerciseInstructionAdapter.ViewHolder> {

        private final List<String> instructions;

        public ExerciseInstructionAdapter(List<String> instructions) {
            this.instructions = instructions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String instruction = instructions.get(position);
            holder.textView.setText(String.format("%d. %s", position + 1, instruction));
        }

        @Override
        public int getItemCount() {
            return instructions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
