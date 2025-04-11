package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
    private boolean showRemoveButton = false;

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseClickListener listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    public ExerciseAdapter(Context context, List<Exercise> exercises, OnExerciseClickListener listener, boolean showRemoveButton) {
        this(context, exercises, listener);
        this.showRemoveButton = showRemoveButton;
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
        holder.tvExerciseTarget.setText(exercise.getMuscleGroups().get(0));
        holder.tvExerciseDifficulty.setText(exercise.getDifficulty());

        // Load exercise image
        int resourceId = context.getResources().getIdentifier(
                exercise.getDrawableResourceName(),
                "drawable",
                context.getPackageName());

        if (resourceId != 0) {
            Glide.with(context)
                    .load(resourceId)
                    .centerCrop()
                    .into(holder.ivExerciseImage);
        }

        // Set click listener for the card
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExerciseClick(exercise);
            }
        });

        // Show or hide remove button based on configuration
        if (showRemoveButton) {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExerciseRemove(exercise, position);
                }
            });
        } else {
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    /**
     * Update the adapter data with filtered results
     * @param filteredList New list of exercises to display
     */
    public void updateData(List<Exercise> filteredList) {
        this.exercises = filteredList;
        notifyDataSetChanged();
    }

    /**
     * View holder for exercise items
     */
    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivExerciseImage;
        TextView tvExerciseName;
        TextView tvExerciseTarget;
        TextView tvExerciseDifficulty;
        ImageButton btnRemove;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivExerciseImage = itemView.findViewById(R.id.iv_exercise_image);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvExerciseTarget = itemView.findViewById(R.id.tv_exercise_target);
            tvExerciseDifficulty = itemView.findViewById(R.id.tv_exercise_difficulty);
            btnRemove = itemView.findViewById(R.id.btn_remove_exercise);
        }
    }

    /**
     * Interface for handling exercise click events
     */
    public interface OnExerciseClickListener {
        void onExerciseClick(Exercise exercise);
        void onExerciseRemove(Exercise exercise, int position);
    }
}