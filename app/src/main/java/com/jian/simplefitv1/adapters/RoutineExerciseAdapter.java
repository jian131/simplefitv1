package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.RoutineExercise;

import java.util.List;
import java.util.Map;

/**
 * Adapter para mostrar los ejercicios dentro de una rutina con detalles sobre series y repeticiones
 */
public class RoutineExerciseAdapter extends RecyclerView.Adapter<RoutineExerciseAdapter.RoutineExerciseViewHolder> {

    private Context context;
    private List<RoutineExercise> routineExercises;
    private Map<String, Exercise> exerciseDetailsMap;
    private boolean isEditable;

    /**
     * Constructor para el modo de visualización (RoutineActivity)
     */
    public RoutineExerciseAdapter(Context context, List<RoutineExercise> routineExercises,
                                  Map<String, Exercise> exerciseDetailsMap) {
        this.context = context;
        this.routineExercises = routineExercises;
        this.exerciseDetailsMap = exerciseDetailsMap;
        this.isEditable = false;
    }

    /**
     * Constructor para el modo de edición (CreateRoutineFragment)
     */
    public RoutineExerciseAdapter(Context context, List<RoutineExercise> routineExercises,
                                  Map<String, Exercise> exerciseDetailsMap, boolean isEditable) {
        this.context = context;
        this.routineExercises = routineExercises;
        this.exerciseDetailsMap = exerciseDetailsMap;
        this.isEditable = isEditable;
    }

    @NonNull
    @Override
    public RoutineExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(
                isEditable ? R.layout.item_routine_exercise_editable : R.layout.item_routine_exercise,
                parent, false);
        return new RoutineExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineExerciseViewHolder holder, int position) {
        RoutineExercise routineExercise = routineExercises.get(position);
        Exercise exercise = exerciseDetailsMap.get(routineExercise.getExerciseId());

        if (exercise != null) {
            // Configurar información básica del ejercicio
            holder.tvExerciseName.setText(exercise.getName());

            // Mostrar músculos trabajados
            if (exercise.getMuscleGroups() != null && !exercise.getMuscleGroups().isEmpty()) {
                holder.tvMuscleGroup.setText(String.join(", ", exercise.getMuscleGroups()));
            } else {
                holder.tvMuscleGroup.setText("");
            }

            // Configurar imagen si está disponible
            if (holder.ivExercise != null && exercise.getDrawableResourceName() != null) {
                int resourceId = context.getResources().getIdentifier(
                        exercise.getDrawableResourceName(), "drawable", context.getPackageName());
                if (resourceId != 0) {
                    Glide.with(context)
                            .load(resourceId)
                            .into(holder.ivExercise);
                }
            }
        }

        // Configurar detalles de series y repeticiones
        if (holder.tvSets != null) {
            holder.tvSets.setText(String.format("%d series", routineExercise.getSets()));
        }

        if (holder.tvReps != null) {
            holder.tvReps.setText(String.format("%d repeticiones", routineExercise.getReps()));
        }

        // Configurar campos editables si estamos en modo edición
        if (isEditable && holder.etSets != null && holder.etReps != null) {
            holder.etSets.setText(String.valueOf(routineExercise.getSets()));
            holder.etReps.setText(String.valueOf(routineExercise.getReps()));

            // Agregar TextWatchers para actualizar el modelo cuando cambian los valores
            setupTextWatcher(holder.etSets, position, true);
            setupTextWatcher(holder.etReps, position, false);

            // Configurar botón de eliminar
            if (holder.ibDelete != null) {
                holder.ibDelete.setOnClickListener(v -> {
                    routineExercises.remove(position);
                    notifyDataSetChanged();
                });
            }
        }
    }

    private void setupTextWatcher(EditText editText, int position, boolean isSets) {
        editText.removeTextChangedListener(editText.getTag() instanceof TextWatcher ?
                (TextWatcher) editText.getTag() : null);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int value = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    if (position < routineExercises.size()) {
                        if (isSets) {
                            routineExercises.get(position).setSets(value);
                        } else {
                            routineExercises.get(position).setReps(value);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorar entradas no numéricas
                }
            }
        };

        editText.setTag(watcher);
        editText.addTextChangedListener(watcher);
    }

    @Override
    public int getItemCount() {
        return routineExercises.size();
    }

    public static class RoutineExerciseViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvMuscleGroup, tvSets, tvReps;
        ImageView ivExercise;
        EditText etSets, etReps;
        ImageButton ibDelete;
        CardView cardView;

        public RoutineExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tv_exercise_name);
            tvMuscleGroup = itemView.findViewById(R.id.tv_muscle_group);
            ivExercise = itemView.findViewById(R.id.iv_exercise);
            cardView = itemView.findViewById(R.id.card_view);

            // Estos pueden ser null dependiendo del layout
            tvSets = itemView.findViewById(R.id.tv_sets);
            tvReps = itemView.findViewById(R.id.tv_reps);
            etSets = itemView.findViewById(R.id.et_sets);
            etReps = itemView.findViewById(R.id.et_reps);
            ibDelete = itemView.findViewById(R.id.ib_delete);
        }
    }
}