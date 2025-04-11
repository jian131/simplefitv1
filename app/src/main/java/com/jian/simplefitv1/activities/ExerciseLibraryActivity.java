package com.jian.simplefitv1.activities;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.jian.simplefitv1.R;
import com.jian.simplefitv1.fragments.ExerciseLibraryFragment;

public class ExerciseLibraryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_library);

        // Thiết lập toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thư viện bài tập");
        }

        // Đọc chế độ lựa chọn từ intent
        boolean isSelectionMode = getIntent().getBooleanExtra("SELECTION_MODE", false);

        // Thêm Fragment Exercise Library
        if (savedInstanceState == null) {
            ExerciseLibraryFragment fragment = new ExerciseLibraryFragment();

            // Truyền chế độ lựa chọn cho fragment
            Bundle args = new Bundle();
            args.putBoolean("SELECTION_MODE", isSelectionMode);
            fragment.setArguments(args);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
