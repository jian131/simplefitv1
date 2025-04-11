package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.Exercise;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private Context context;
    private List<Exercise> exercises;
    private OnExerciseClickListener listener;

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseClickListener listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);

        holder.tvExerciseName.setText(exercise.getName());
        holder.tvMuscleGroups.setText(formatMuscleGroups(exercise.getMuscleGroups()));
        holder.tvDifficulty.setText(exercise.getDifficulty());

        // Set difficulty color based on level
        String difficulty = exercise.getDifficulty().toLowerCase();
        int colorResId;
        switch (difficulty) {
            case "beginner":
                colorResId = R.color.difficulty_beginner;
                break;
            case "intermediate":
                colorResId = R.color.difficulty_intermediate;
                break;
            case "advanced":
                colorResId = R.color.difficulty_advanced;
                break;
            default:
                colorResId = R.color.difficulty_beginner;
        }
        holder.tvDifficulty.setTextColor(context.getResources().getColor(colorResId));

        // Load image using Glide
        int resourceId = context.getResources().getIdentifier(
                exercise.getDrawableResourceName(), "drawable", context.getPackageName());

        if (resourceId != 0) {
            Glide.with(context)
                    .load(resourceId)
                    .centerCrop()
                    .into(holder.ivExerciseImage);
        }

        // Set click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(exercise);
            }
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * Update the adapter data with filtered results
     * @param filteredList New list of exercises to display
     */
    public void filterList(List<Exercise> filteredList) {
        this.exercises = filteredList;
        notifyDataSetChanged();
    }

    /**
     * Format muscle groups list into a comma-separated string
     */
    private String formatMuscleGroups(List<String> muscleGroups) {
        if (muscleGroups == null || muscleGroups.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < muscleGroups.size(); i++) {
            sb.append(muscleGroups.get(i));
            if (i < muscleGroups.size() - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    // View Holder
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivExerciseImage;
        TextView tvExerciseName;
        TextView tvMuscleGroups;
        TextView tvDifficulty;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivExerciseImage = itemView.findViewById(R.id.iv_exercise_image);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvMuscleGroups = itemView.findViewById(R.id.tv_muscle_groups);
            tvDifficulty = itemView.findViewById(R.id.tv_difficulty);
        }
    }

    // Callback interface for click events
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
    }
}
