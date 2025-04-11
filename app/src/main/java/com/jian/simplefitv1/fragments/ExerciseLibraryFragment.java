package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.ExerciseDetailActivity;
import com.jian.simplefitv1.adapters.ExerciseAdapter;
import com.jian.simplefitv1.adapters.MuscleGroupAdapter;
import com.jian.simplefitv1.data.ExerciseData;
import com.jian.simplefitv1.data.MuscleGroupData;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.MuscleGroup;

import java.util.ArrayList;
import java.util.List;

public class ExerciseLibraryFragment extends Fragment implements
        ExerciseAdapter.OnExerciseClickListener,
        MuscleGroupAdapter.OnMuscleGroupClickListener {

    private RecyclerView rvExercises;
    private RecyclerView rvMuscleGroups;
    private ExerciseAdapter exerciseAdapter;
    private MuscleGroupAdapter muscleGroupAdapter;
    private EditText etSearch;

    private List<Exercise> allExercises = new ArrayList<>();
    private String selectedMuscleGroup = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvExercises = view.findViewById(R.id.rv_exercises);
        rvMuscleGroups = view.findViewById(R.id.rv_muscle_groups);
        etSearch = view.findViewById(R.id.et_search);

        // Set up exercise RecyclerView
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up muscle group RecyclerView
        rvMuscleGroups.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Load data
        loadExercises();
        loadMuscleGroups();

        // Set up search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterExercises(s.toString());
            }
        });
    }

    private void loadExercises() {
        // In a real app, this would come from Firebase/database
        allExercises = ExerciseData.getAllExercises();

        exerciseAdapter = new ExerciseAdapter(getContext(), allExercises, this);
        rvExercises.setAdapter(exerciseAdapter);
    }

    private void loadMuscleGroups() {
        // In a real app, this would come from Firebase/database
        List<MuscleGroup> muscleGroups = MuscleGroupData.getAllMuscleGroups();

        muscleGroupAdapter = new MuscleGroupAdapter(getContext(), muscleGroups, this);
        rvMuscleGroups.setAdapter(muscleGroupAdapter);
    }

    private void filterExercises(String query) {
        List<Exercise> filteredList = new ArrayList<>();

        for (Exercise exercise : allExercises) {
            // Filter by search query
            boolean matchesQuery = query.isEmpty() ||
                    exercise.getName().toLowerCase().contains(query.toLowerCase());

            // Filter by selected muscle group
            boolean matchesMuscleGroup = selectedMuscleGroup == null ||
                    exercise.getMuscleGroups().contains(selectedMuscleGroup);

            if (matchesQuery && matchesMuscleGroup) {
                filteredList.add(exercise);
            }
        }

        exerciseAdapter.filterList(filteredList);
    }

    @Override
    public void onExerciseClick(Exercise exercise) {
        // Navigate to exercise detail
        Intent intent = new Intent(getActivity(), ExerciseDetailActivity.class);
        intent.putExtra(ExerciseDetailActivity.EXTRA_EXERCISE_ID, exercise.getId());
        startActivity(intent);
    }

    @Override
    public void onMuscleGroupClick(MuscleGroup muscleGroup) {
        // Filter exercises by muscle group
        selectedMuscleGroup = muscleGroup.getId();
        filterExercises(etSearch.getText().toString());
    }
}
