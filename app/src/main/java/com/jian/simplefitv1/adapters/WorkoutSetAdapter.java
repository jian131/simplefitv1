package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jian.simplefitv1.R;
import com.jian.simplefitv1.models.WorkoutSet;

import java.util.List;

public class WorkoutSetAdapter extends RecyclerView.Adapter<WorkoutSetAdapter.WorkoutSetViewHolder> {

    private Context context;
    private List<WorkoutSet> sets;
    private String exerciseName;
    private OnSetChangeListener listener;

    public WorkoutSetAdapter(Context context, List<WorkoutSet> sets, String exerciseName, OnSetChangeListener listener) {
        this.context = context;
        this.sets = sets;
        this.exerciseName = exerciseName;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutSetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workout_set, parent, false);
        return new WorkoutSetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutSetViewHolder holder, int position) {
        WorkoutSet set = sets.get(position);

        // Set number (1-based)
        holder.tvSetNumber.setText(String.format("Set %d", position + 1));

        // Set current values
        holder.etReps.setText(String.valueOf(set.getReps()));
        holder.etWeight.setText(String.valueOf(set.getWeight()));

        // Clear previous text watchers to avoid callback loops
        if (holder.repsTextWatcher != null) {
            holder.etReps.removeTextChangedListener(holder.repsTextWatcher);
        }
        if (holder.weightTextWatcher != null) {
            holder.etWeight.removeTextChangedListener(holder.weightTextWatcher);
        }

        // Create and set new text watchers
        holder.repsTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int reps = s.toString().isEmpty() ? 0 : Integer.parseInt(s.toString());
                    set.setReps(reps);
                    if (listener != null) {
                        listener.onSetChanged(position, set);
                    }
                } catch (NumberFormatException e) {
                    holder.etReps.setText("0");
                }
            }
        };

        holder.weightTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    float weight = s.toString().isEmpty() ? 0 : Float.parseFloat(s.toString());
                    set.setWeight(weight);
                    if (listener != null) {
                        listener.onSetChanged(position, set);
                    }
                } catch (NumberFormatException e) {
                    holder.etWeight.setText("0");
                }
            }
        };

        // Add the text watchers
        holder.etReps.addTextChangedListener(holder.repsTextWatcher);
        holder.etWeight.addTextChangedListener(holder.weightTextWatcher);

        // Set completed button state
        updateCompletedState(holder, set.isCompleted());

        // Set click listeners
        holder.btnComplete.setOnClickListener(v -> {
            boolean newState = !set.isCompleted();
            set.setCompleted(newState);
            updateCompletedState(holder, newState);
            if (listener != null) {
                listener.onSetCompleted(position, set);
            }
        });

        // Show remove button for all but the first set
        if (position > 0) {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSetRemoved(position);
                }
            });
        } else {
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    private void updateCompletedState(WorkoutSetViewHolder holder, boolean completed) {
        if (completed) {
            // Visual feedback for completed set
            holder.btnComplete.setChecked(true);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.completed_background));
            holder.tvSetNumber.setTextColor(context.getResources().getColor(R.color.accent));
        } else {
            // Reset to default state
            holder.btnComplete.setChecked(false);
            holder.itemView.setBackground(null);
            holder.tvSetNumber.setTextColor(context.getResources().getColor(R.color.text_primary));
        }
    }

    @Override
    public int getItemCount() {
        return sets.size();
    }

    // Add a new set
    public void addSet() {
        // Create a new set with the same weight as the last set, if there is one
        WorkoutSet newSet;
        if (!sets.isEmpty()) {
            WorkoutSet lastSet = sets.get(sets.size() - 1);
            newSet = new WorkoutSet();
            newSet.setReps(lastSet.getReps());
            newSet.setWeight(lastSet.getWeight());
        } else {
            newSet = new WorkoutSet();
            newSet.setReps(10); // Default 10 reps
            newSet.setWeight(0); // Default 0 weight
        }
        sets.add(newSet);
        notifyItemInserted(sets.size() - 1);
    }

    // Remove a set
    public void removeSet(int position) {
        if (position >= 0 && position < sets.size()) {
            sets.remove(position);
            notifyItemRemoved(position);
            // Update set numbers for remaining items
            notifyItemRangeChanged(position, sets.size() - position);
        }
    }

    // ViewHolder class for workout set items
    static class WorkoutSetViewHolder extends RecyclerView.ViewHolder {
        TextView tvSetNumber;
        EditText etReps;
        EditText etWeight;
        CheckBox btnComplete;  // Change from ImageButton to CheckBox
        ImageButton btnRemove;

        // Text watchers for input fields
        TextWatcher repsTextWatcher;
        TextWatcher weightTextWatcher;

        public WorkoutSetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSetNumber = itemView.findViewById(R.id.tv_set_number);
            etReps = itemView.findViewById(R.id.et_reps);
            etWeight = itemView.findViewById(R.id.et_weight);
            btnComplete = itemView.findViewById(R.id.btn_complete);  // This should match a CheckBox in the layout
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }
    }

    // Interface for handling set changes
    public interface OnSetChangeListener {
        void onSetChanged(int position, WorkoutSet set);
        void onSetCompleted(int position, WorkoutSet set);
        void onSetRemoved(int position);
    }
}
