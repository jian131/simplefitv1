package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;  // Added this import
import com.google.firebase.firestore.WriteBatch;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.ExerciseLibraryActivity;
import com.jian.simplefitv1.adapters.RoutineExerciseAdapter;
import com.jian.simplefitv1.data.ExerciseData;
import com.jian.simplefitv1.models.Exercise;
import com.jian.simplefitv1.models.Routine;
import com.jian.simplefitv1.models.RoutineExercise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class CreateRoutineFragment extends Fragment {

    private static final String TAG = "CreateRoutineFragment";
    private static final int EXERCISE_SELECTION_REQUEST = 100;

    private EditText etRoutineName, etRoutineDescription;
    private Button btnSaveRoutine, btnCancel;
    private RecyclerView rvExercises;
    private FloatingActionButton fabAddExercise;
    private TextView tvNoExercises;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String editRoutineId;
    private boolean isEditMode = false;
    private Routine currentRoutine;

    private List<RoutineExercise> exerciseList = new ArrayList<>();
    private Map<String, Exercise> exerciseDetailsMap = new HashMap<>();
    private RoutineExerciseAdapter adapter;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Check if we are editing an existing routine
        if (getArguments() != null) {
            editRoutineId = getArguments().getString("ROUTINE_ID");
            isEditMode = (editRoutineId != null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_routine, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mContext = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        etRoutineName = view.findViewById(R.id.et_routine_name);
        etRoutineDescription = view.findViewById(R.id.et_routine_description);
        btnSaveRoutine = view.findViewById(R.id.btn_save_routine);
        btnCancel = view.findViewById(R.id.btn_cancel);
        rvExercises = view.findViewById(R.id.rv_exercises);
        fabAddExercise = view.findViewById(R.id.fab_add_exercise);
        tvNoExercises = view.findViewById(R.id.tv_no_exercises);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup click listeners
        setupClickListeners();

        // Load routine data if editing
        if (isEditMode && editRoutineId != null) {
            loadRoutineForEditing();
        } else {
            showEmptyExercisesMessage();
        }
    }

    private void setupRecyclerView() {
        adapter = new RoutineExerciseAdapter(getContext(), exerciseList, exerciseDetailsMap);
        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExercises.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSaveRoutine.setOnClickListener(v -> saveRoutine());
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            });
        }
        fabAddExercise.setOnClickListener(v -> openExerciseSelectionActivity());
    }

    private void loadRoutineForEditing() {
        if (editRoutineId == null || editRoutineId.isEmpty()) return;

        db.collection("routines").document(editRoutineId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        currentRoutine = documentSnapshot.toObject(Routine.class);
                        currentRoutine.setId(documentSnapshot.getId());

                        // Update UI with routine details
                        if (currentRoutine != null) {
                            etRoutineName.setText(currentRoutine.getName());
                            etRoutineDescription.setText(currentRoutine.getDescription());

                            // Load exercises for this routine
                            loadRoutineExercises();
                        }
                    } else {
                        Toast.makeText(getContext(), "Routine not found", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading routine: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading routine", e);
                });
    }

    private void loadRoutineExercises() {
        if (editRoutineId == null) return;

        db.collection("routines").document(editRoutineId)
                .collection("exercises").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    exerciseList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        RoutineExercise exercise = document.toObject(RoutineExercise.class);
                        exercise.setId(document.getId());
                        exerciseList.add(exercise);

                        // Load exercise details for each exercise
                        loadExerciseDetails(exercise.getExerciseId());
                    }

                    adapter.notifyDataSetChanged();

                    if (exerciseList.isEmpty()) {
                        showEmptyExercisesMessage();
                    } else {
                        hideEmptyExercisesMessage();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading exercises: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading exercises", e);
                });
    }

    private void showEmptyExercisesMessage() {
        if (tvNoExercises != null) {
            tvNoExercises.setVisibility(View.VISIBLE);
        }
        if (rvExercises != null) {
            rvExercises.setVisibility(View.GONE);
        }
    }

    private void hideEmptyExercisesMessage() {
        if (tvNoExercises != null) {
            tvNoExercises.setVisibility(View.GONE);
        }
        if (rvExercises != null) {
            rvExercises.setVisibility(View.VISIBLE);
        }
    }

    private void loadExerciseDetails(String exerciseId) {
        Exercise exercise = ExerciseData.getExerciseById(exerciseId);
        if (exercise != null) {
            exerciseDetailsMap.put(exerciseId, exercise);
            adapter.notifyDataSetChanged();
        }
    }

    private void openExerciseSelectionActivity() {
        Intent intent = new Intent(getActivity(), ExerciseLibraryActivity.class);
        intent.putExtra("SELECTION_MODE", true);
        startActivityForResult(intent, EXERCISE_SELECTION_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXERCISE_SELECTION_REQUEST && resultCode == RESULT_OK && data != null) {
            String exerciseId = data.getStringExtra("EXERCISE_ID");
            if (exerciseId != null) {
                // Tạo RoutineExercise mới với các giá trị mặc định
                RoutineExercise routineExercise = new RoutineExercise();
                routineExercise.setExerciseId(exerciseId);
                routineExercise.setOrder(exerciseList.size());
                routineExercise.setSets(3); // Mặc định 3 hiệp
                routineExercise.setReps(10); // Mặc định 10 lần

                // Thêm vào danh sách và cập nhật adapter
                exerciseList.add(routineExercise);
                loadExerciseDetails(exerciseId);
                adapter.notifyDataSetChanged();

                // Ẩn thông báo "không có bài tập"
                if (tvNoExercises != null) {
                    tvNoExercises.setVisibility(View.GONE);
                }
                if (rvExercises != null) {
                    rvExercises.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void saveRoutine() {
        // Kiểm tra người dùng đã đăng nhập hay chưa
        if (currentUser == null) {
            Toast.makeText(getContext(), "Bạn cần đăng nhập để lưu lịch trình", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra tên lịch trình
        String name = etRoutineName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etRoutineName.setError("Vui lòng nhập tên lịch trình");
            return;
        }

        // Kiểm tra danh sách bài tập
        if (exerciseList.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng thêm ít nhất một bài tập", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Routine mới hoặc cập nhật hiện có
        Routine routine;
        if (isEditMode && currentRoutine != null) {
            routine = currentRoutine;
            routine.setName(name);
            routine.setDescription(etRoutineDescription.getText().toString().trim());
            routine.setExerciseCount(exerciseList.size());
            routine.setUpdatedAt(System.currentTimeMillis());
        } else {
            routine = new Routine();
            routine.setName(name);
            routine.setDescription(etRoutineDescription.getText().toString().trim());
            routine.setUserId(currentUser.getUid());
            routine.setExerciseCount(exerciseList.size());
            routine.setCreatedAt(System.currentTimeMillis());
            routine.setUpdatedAt(System.currentTimeMillis());
        }

        // Ước tính thời gian (5 phút mỗi bài tập)
        routine.setEstimatedMinutes(exerciseList.size() * 5);

        // Lưu lịch trình vào Firestore
        DocumentReference routineRef;
        if (isEditMode && editRoutineId != null) {
            routineRef = db.collection("routines").document(editRoutineId);
        } else {
            routineRef = db.collection("routines").document();
        }

        routineRef.set(routine)
                .addOnSuccessListener(aVoid -> {
                    // Lưu bài tập cho lịch trình
                    saveExercisesToRoutine(routineRef);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi lưu lịch trình: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveExercisesToRoutine(DocumentReference routineRef) {
        // Trước tiên, xóa tất cả bài tập hiện có nếu đang ở chế độ chỉnh sửa
        if (isEditMode) {
            routineRef.collection("exercises")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        // Xóa từng bài tập
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }

                        // Sau đó thêm các bài tập mới
                        addExercisesToRoutine(routineRef);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi cập nhật bài tập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Nếu là lịch trình mới, thêm trực tiếp bài tập
            addExercisesToRoutine(routineRef);
        }
    }

    private void addExercisesToRoutine(DocumentReference routineRef) {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached, cannot add exercises to routine");
            return;
        }

        // Get reference to the exercises collection
        CollectionReference exercisesRef = routineRef.collection("exercises");

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new batch
        WriteBatch batch = db.batch();

        // Add each exercise to the batch
        for (int i = 0; i < exerciseList.size(); i++) {  // Fixed: changed exercises to exerciseList
            RoutineExercise exercise = exerciseList.get(i);  // Fixed: changed exercises to exerciseList
            exercise.setOrder(i); // Ensure order is set correctly
            DocumentReference docRef = exercisesRef.document();
            batch.set(docRef, exercise);
        }

        // Commit the batch
        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Đã lưu lịch trình thành công", Toast.LENGTH_SHORT).show();
                        if (getActivity() != null && !getActivity().isFinishing()) {
                            getActivity().onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, "Error adding exercises to routine", e);
                });
    }

    // Add helper method for showing toasts safely
    private void showToast(String message) {
        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    // Factory method to create a new instance for editing
    public static CreateRoutineFragment newInstance(String routineId) {
        CreateRoutineFragment fragment = new CreateRoutineFragment();
        Bundle args = new Bundle();
        args.putString("ROUTINE_ID", routineId);
        fragment.setArguments(args);
        return fragment;
    }
}