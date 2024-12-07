package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dst.abacustrainner.Fragment.ClassFragment;
import com.dst.abacustrainner.Fragment.CompitationFragment;
import com.dst.abacustrainner.Fragment.HomeFragment;
import com.dst.abacustrainner.Fragment.PracticeFragment;
import com.dst.abacustrainner.Fragment.ProfileFragment;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeActivity extends AppCompatActivity{

    String studentId;
    String displayName;
    String email;
    String idToken;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView= findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setItemIconSize(getResources().getDimensionPixelSize(R.dimen.bottom_nav_icon_size));
        bottomNavigationView.setItemTextAppearanceInactive(R.style.BottomNavTextAppearance);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(getApplicationContext()).getUserData();
        studentId = result.getStudentId();

        if (studentId != null) {
            // Load the HomeFragment with the retrieved studentId
            loadHomeFragmentWithStudentId(studentId);
        } else {
            Toast.makeText(HomeActivity.this, "Student ID not available. Please log in again.", Toast.LENGTH_LONG).show();
        }




    }

    private void loadHomeFragmentWithStudentId(String studentId) {
        Bundle bundle = new Bundle();
        bundle.putString("studentId", studentId);
        bundle.getString("displayName");
        bundle.getString("email");
        bundle.getString("idToken");

        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, homeFragment)
                .commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;
            int itemView= item.getItemId();

            if (itemView == R.id.navigation_home){
                selectedFragment =new HomeFragment();
            }else if(itemView == R.id.navigation_cla){
                selectedFragment =new ClassFragment();
            }else if(itemView == R.id.navigation_practice){
                selectedFragment =new PracticeFragment();
            }else if(itemView == R.id.navigation_compitation){
                selectedFragment =new CompitationFragment();
            }else if(itemView == R.id.navigation_profile){
                selectedFragment =new ProfileFragment();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, selectedFragment)
                    .commit();
            return true;
        }
    };
}