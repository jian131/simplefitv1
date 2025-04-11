package com.jian.simplefitv1.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.jian.simplefitv1.R;
import com.jian.simplefitv1.fragments.HomeFragment;
import com.jian.simplefitv1.fragments.ProfileFragment;
import com.jian.simplefitv1.fragments.RoutineFragment;
import com.jian.simplefitv1.fragments.WorkoutHistoryFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabStartWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if user is logged in, if not, redirect to auth activity
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        // Initialize UI components
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabStartWorkout = findViewById(R.id.fab_start_workout);

        // Set up listeners
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        fabStartWorkout.setOnClickListener(v -> openQuickStartWorkout());

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
            // Show FAB start workout on Home screen
            fabStartWorkout.setVisibility(View.VISIBLE);
        /* Temporarily disabled Routine functionality
        } else if (itemId == R.id.nav_routines) {
            fragment = new RoutineFragment();
            // Hide FAB start workout on Routines screen
            fabStartWorkout.setVisibility(View.GONE);
        */
        } else if (itemId == R.id.nav_history) {
            fragment = new WorkoutHistoryFragment();
            // Hide FAB start workout on History screen
            fabStartWorkout.setVisibility(View.GONE);
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
            // Hide FAB start workout on Profile screen
            fabStartWorkout.setVisibility(View.GONE);
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void openQuickStartWorkout() {
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    /**
     * Phương thức public để điều hướng đến một fragment
     * @param fragment Fragment cần hiển thị
     */
    public void navigateToFragment(Fragment fragment) {
        // Sử dụng phương thức loadFragment private
        loadFragment(fragment);
    }

    /**
     * Chọn một mục trong bottom navigation
     * @param itemId ID của mục cần chọn
     */
    public void selectNavItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    @Override
    public void onBackPressed() {
        // Nếu không phải là Home Fragment, chuyển về Home
        if (bottomNavigationView.getSelectedItemId() != R.id.nav_home) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}
