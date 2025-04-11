package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.Workout;
import com.jian.simplefitv1.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private Context context;
    private List<Workout> workouts;
    private OnWorkoutClickListener listener;

    // Constructor with click listener (for WorkoutHistoryFragment)
    public WorkoutAdapter(Context context, List<Workout> workouts, OnWorkoutClickListener listener) {
        this.context = context;
        this.workouts = workouts;
        this.listener = listener;
    }

    // Constructor without click listener (for HomeFragment)
    public WorkoutAdapter(Context context, List<Workout> workouts) {
        this.context = context;
        this.workouts = workouts;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workouts.get(position);

        // Format date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String workoutDate = dateFormat.format(new Date(workout.getStartTime()));

        // Format duration
        String duration = TimeUtils.formatDuration(workout.getDuration());

        // Set workout name (or "Quick Workout" if no routine was used)
        String workoutName = workout.getRoutineName();
        if (workoutName == null || workoutName.isEmpty()) {
            workoutName = context.getString(R.string.quick_workout);
        }

        holder.tvWorkoutName.setText(workoutName);
        holder.tvWorkoutDate.setText(workoutDate);
        holder.tvWorkoutDuration.setText(duration);

        // Set exercises count if available
        int exerciseCount = workout.getExerciseCount();
        if (exerciseCount > 0) {
            holder.tvExerciseCount.setText(String.format("%d bài tập", exerciseCount));
        } else {
            holder.tvExerciseCount.setVisibility(View.GONE);
        }

        // Set click listener if available
        if (listener != null) {
            holder.cardView.setOnClickListener(v -> listener.onWorkoutClick(workout));
        }
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    // ViewHolder class for workout items
    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvWorkoutName;
        TextView tvWorkoutDate;
        TextView tvWorkoutDuration;
        TextView tvExerciseCount;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvWorkoutName = itemView.findViewById(R.id.tv_workout_name);
            tvWorkoutDate = itemView.findViewById(R.id.tv_workout_date);
            tvWorkoutDuration = itemView.findViewById(R.id.tv_workout_duration);
            tvExerciseCount = itemView.findViewById(R.id.tv_exercise_count);
        }
    }

    // Interface for handling workout clicks
    public interface OnWorkoutClickListener {
        void onWorkoutClick(Workout workout);
    }
}
