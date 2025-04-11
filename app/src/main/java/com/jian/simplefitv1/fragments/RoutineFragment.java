package com.jian.simplefitv1.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.activities.RoutineActivity;
import com.jian.simplefitv1.adapters.RoutineAdapter;
import com.jian.simplefitv1.models.Routine;

import java.util.ArrayList;
import java.util.List;

public class RoutineFragment extends Fragment implements RoutineAdapter.OnRoutineClickListener {

    private static final String TAG = "RoutineFragment";

    private RecyclerView rvRoutines;
    private TextView tvNoRoutines;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fabAddRoutine;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private List<Routine> routines = new ArrayList<>();
    private RoutineAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        rvRoutines = view.findViewById(R.id.rv_routines);
        tvNoRoutines = view.findViewById(R.id.tv_no_routines);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        fabAddRoutine = view.findViewById(R.id.fab_add_routine);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadRoutines);

        // Setup FloatingActionButton
        fabAddRoutine.setOnClickListener(v -> openCreateRoutineFragment());

        // Load data
        loadRoutines();
    }

    private void setupRecyclerView() {
        rvRoutines.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RoutineAdapter(getContext(), routines, this);
        rvRoutines.setAdapter(adapter);
    }

    private void loadRoutines() {
        if (currentUser == null || !isAdded()) {
            // Nếu fragment không được gắn vào activity hoặc người dùng không tồn tại, không thực hiện truy vấn
            return;
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        // Truy vấn routines của người dùng hiện tại
        db.collection("users").document(currentUser.getUid())
                .collection("routines")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (isAdded()) { // Kiểm tra Fragment có được gắn vào không
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        routines.clear();

                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Routine routine = documentSnapshot.toObject(Routine.class);
                            routine.setId(documentSnapshot.getId());
                            routines.add(routine);
                        }

                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }

                        if (routines.isEmpty()) {
                            showEmptyState();
                        } else if (tvNoRoutines != null) {
                            tvNoRoutines.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) { // Kiểm tra Fragment có được gắn vào không trước khi hiển thị Toast
                        if (swipeRefreshLayout != null) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        // Toast an toàn
                        try {
                            Context context = getContext();
                            if (context != null) {
                                Toast.makeText(context, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, "Error showing toast", ex);
                        }
                        showEmptyState();
                    }
                });
    }

    private void showEmptyState() {
        tvNoRoutines.setVisibility(View.VISIBLE);
        rvRoutines.setVisibility(View.GONE);
    }

    private void openCreateRoutineFragment() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new CreateRoutineFragment())
                .addToBackStack(null)
                .commit();
        }
    }

    @Override
    public void onRoutineClick(Routine routine) {
        Intent intent = new Intent(getActivity(), RoutineActivity.class);
        intent.putExtra("ROUTINE_ID", routine.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadRoutines();
    }
}
