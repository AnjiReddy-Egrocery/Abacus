package com.dst.abacustrainner.Activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dst.abacustrainner.Fragment.HomeFragment;
import com.dst.abacustrainner.R;

public class DashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getIntent().getBooleanExtra("openHome", false)) {
            loadFragment(new HomeFragment());
        }

        // Add bottom navigation setup here if needed
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
