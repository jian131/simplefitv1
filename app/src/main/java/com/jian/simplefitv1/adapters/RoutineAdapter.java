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
import com.jian.simplefitv1.models.Routine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.RoutineViewHolder> {

    private Context context;
    private List<Routine> routines;
    private OnRoutineClickListener listener;

    // Constructor for use with click listener (for RoutineFragment)
    public RoutineAdapter(Context context, List<Routine> routines, OnRoutineClickListener listener) {
        this.context = context;
        this.routines = routines;
        this.listener = listener;
    }

    // Constructor without click listener (for HomeFragment)
    public RoutineAdapter(Context context, List<Routine> routines) {
        this.context = context;
        this.routines = routines;
    }

    @NonNull
    @Override
    public RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_routine, parent, false);
        return new RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = routines.get(position);

        holder.tvRoutineName.setText(routine.getName());
        holder.tvEstimatedTime.setText(String.format("%d phút", routine.getEstimatedMinutes()));

        // Format and display creation date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String createdDate = dateFormat.format(new Date(routine.getCreatedAt()));
        holder.tvCreatedDate.setText(createdDate);

        // Set exercise count
        if (routine.getExerciseCount() > 0) {
            holder.tvExerciseCount.setText(String.format("%d bài tập", routine.getExerciseCount()));
        } else {
            holder.tvExerciseCount.setText(R.string.no_exercises);
        }

        // Set click listener if available
        if (listener != null) {
            holder.cardView.setOnClickListener(v -> listener.onRoutineClick(routine));
        }
    }

    @Override
    public int getItemCount() {
        return routines.size();
    }

    // ViewHolder class for routine items
    static class RoutineViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvRoutineName;
        TextView tvEstimatedTime;
        TextView tvExerciseCount;
        TextView tvCreatedDate;

        public RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            tvRoutineName = itemView.findViewById(R.id.tv_routine_name);
            tvEstimatedTime = itemView.findViewById(R.id.tv_estimated_time);
            tvExerciseCount = itemView.findViewById(R.id.tv_exercise_count);
            tvCreatedDate = itemView.findViewById(R.id.tv_created_date);
        }
    }

    // Interface for handling routine clicks
    public interface OnRoutineClickListener {
        void onRoutineClick(Routine routine);
    }
}
