package com.jian.simplefitv1.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.jian.simplefitv1.models.MuscleGroup;

import java.util.List;

public class MuscleGroupAdapter extends RecyclerView.Adapter<MuscleGroupAdapter.MuscleGroupViewHolder> {

    private Context context;
    private List<MuscleGroup> muscleGroups;
    private OnMuscleGroupClickListener listener;
    private int selectedPosition = -1; // -1 means no selection

    public MuscleGroupAdapter(Context context, List<MuscleGroup> muscleGroups, OnMuscleGroupClickListener listener) {
        this.context = context;
        this.muscleGroups = muscleGroups;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MuscleGroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_muscle_group, parent, false);
        return new MuscleGroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MuscleGroupViewHolder holder, int position) {
        MuscleGroup muscleGroup = muscleGroups.get(position);

        holder.tvMuscleGroupName.setText(muscleGroup.getName());

        // Load image using Glide
        int resourceId = context.getResources().getIdentifier(
                muscleGroup.getDrawableResourceName(), "drawable", context.getPackageName());

        if (resourceId != 0) {
            Glide.with(context)
                    .load(resourceId)
                    .circleCrop()
                    .into(holder.ivMuscleGroupImage);
        }

        // Handle selection state
        if (position == selectedPosition) {
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.purple_200));
            holder.tvMuscleGroupName.setTextColor(Color.WHITE);
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.tvMuscleGroupName.setTextColor(Color.BLACK);
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;

            // Update selection
            if (selectedPosition == position) {
                // If clicking the same item, deselect it
                selectedPosition = -1;
                listener.onMuscleGroupClick(null); // Null means show all
            } else {
                selectedPosition = position;
                listener.onMuscleGroupClick(muscleGroup);
            }

            // Refresh the changed items
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return muscleGroups.size();
    }

    // View Holder
    static class MuscleGroupViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivMuscleGroupImage;
        TextView tvMuscleGroupName;

        public MuscleGroupViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_view);
            ivMuscleGroupImage = itemView.findViewById(R.id.iv_muscle_group_image);
            tvMuscleGroupName = itemView.findViewById(R.id.tv_muscle_group_name);
        }
    }

    // Callback interface
    public interface OnMuscleGroupClickListener {
        void onMuscleGroupClick(MuscleGroup muscleGroup);
    }
}
