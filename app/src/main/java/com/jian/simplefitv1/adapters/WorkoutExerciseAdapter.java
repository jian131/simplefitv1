package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.WorkoutSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar los ejercicios durante una sesión de entrenamiento
 */
public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.WorkoutExerciseViewHolder> {

    private Context context;
    private List<Exercise> exercises;
    private List<List<WorkoutSet>> exerciseSets; // Lista de conjuntos de sets para cada ejercicio

    public WorkoutExerciseAdapter(Context context, List<Exercise> exercises) {
        this.context = context;
        this.exercises = exercises;
        this.exerciseSets = new ArrayList<>();

        // Inicializar listas de sets para cada ejercicio
        for (int i = 0; i < exercises.size(); i++) {
            List<WorkoutSet> sets = new ArrayList<>();
            // Por defecto, 3 sets para cada ejercicio
            for (int j = 0; j < 3; j++) {
                sets.add(new WorkoutSet(10, 0)); // Default: 10 reps, sin peso
            }
            exerciseSets.add(sets);
        }
    }

    @NonNull
    @Override
    public WorkoutExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout_exercise, parent, false);
        return new WorkoutExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutExerciseViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        List<WorkoutSet> sets = exerciseSets.get(position);

        // Configurar información del ejercicio
        holder.tvExerciseName.setText(exercise.getName());

        if (exercise.getMuscleGroups() != null && !exercise.getMuscleGroups().isEmpty()) {
            holder.tvMuscleGroups.setText(String.join(", ", exercise.getMuscleGroups()));
        } else {
            holder.tvMuscleGroups.setVisibility(View.GONE);
        }

        // Configurar imagen del ejercicio
        if (exercise.getDrawableResourceName() != null) {
            int resourceId = context.getResources().getIdentifier(
                    exercise.getDrawableResourceName(), "drawable", context.getPackageName());
            if (resourceId != 0) {
                Glide.with(context)
                        .load(resourceId)
                        .into(holder.ivExercise);
            }
        }

        // Configurar RecyclerView para los sets
        WorkoutSetAdapter setAdapter = new WorkoutSetAdapter(context, sets, exercise.getName(),
                new WorkoutSetAdapter.OnSetChangeListener() {
                    @Override
                    public void onSetChanged(int position, WorkoutSet set) {
                        sets.set(position, set);
                    }

                    @Override
                    public void onSetCompleted(int position, WorkoutSet set) {
                        sets.get(position).setCompleted(set.isCompleted());
                    }

                    @Override
                    public void onSetRemoved(int position) {
                        sets.remove(position);
                    }
                });

        holder.rvSets.setLayoutManager(new LinearLayoutManager(context));
        holder.rvSets.setAdapter(setAdapter);

        // Botón para añadir set
        holder.btnAddSet.setOnClickListener(v -> {
            sets.add(new WorkoutSet(10, 0));
            setAdapter.notifyItemInserted(sets.size() - 1);
        });
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    /**
     * Obtener todos los sets de ejercicios registrados
     */
    public List<List<WorkoutSet>> getAllExerciseSets() {
        return exerciseSets;
    }

    static class WorkoutExerciseViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivExercise;
        TextView tvExerciseName, tvMuscleGroups;
        RecyclerView rvSets;
        Button btnAddSet;

        public WorkoutExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivExercise = itemView.findViewById(R.id.iv_exercise);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvMuscleGroups = itemView.findViewById(R.id.tv_muscle_groups);
            rvSets = itemView.findViewById(R.id.rv_sets);
            btnAddSet = itemView.findViewById(R.id.btn_add_set);
        }
    }
}