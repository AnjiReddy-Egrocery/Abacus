package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.dst.abacustrainner.Fragment.AIGenrationFragment;
import com.dst.abacustrainner.Fragment.ClassFragment;

import com.dst.abacustrainner.Fragment.CompetitionFragment;
import com.dst.abacustrainner.Fragment.HomeFragment;
import com.dst.abacustrainner.Fragment.PracticeFragment;
import com.dst.abacustrainner.Fragment.ProfileFragment;
import com.dst.abacustrainner.Fragment.SchedulesFragment;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;


public class HomeActivity extends AppCompatActivity{

    String studentId;
    String displayName;
    String email;
    String idToken;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    NavigationView mNavigationView;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ActionBarDrawerToggle toggle;

    LinearLayout layoutAI;
    private NavigationView navigationView;
    private boolean isInboxSubmenuVisible = false;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);



        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        BottomNavigationView bottomNavigationView= findViewById(R.id.bottom_navigation_main);
        //layoutAI = findViewById(R.id.ll_center_option);

        bottomNavigationView.setItemIconSize(getResources().getDimensionPixelSize(R.dimen.bottom_nav_icon_size));
        bottomNavigationView.setItemTextAppearanceInactive(R.style.BottomNavTextAppearance);
        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setPadding(0, 0, 0, 0);  // Padding around the view
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(getApplicationContext()).getUserData();
        studentId = result.getStudentId();

        if (studentId != null) {
            // Load the HomeFragment with the retrieved studentId
            loadHomeFragmentWithStudentId(studentId);
        } else {
            Toast.makeText(HomeActivity.this, "Student ID not available. Please log in again.", Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setItemIconSize(80);

        View headerView = navigationView.getHeaderView(0);
        ImageView closeIcon = headerView.findViewById(R.id.close_icon);


        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START); // NavigationView మూసివేయండి
                }
            }
        });


// Enable the "hamburger" icon to open the drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Set up NavigationView listener for the Drawer
        navigationView.setNavigationItemSelectedListener(navDrawerListener);


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
            }else if(itemView == R.id.ll_center_option){
                selectedFragment =new AIGenrationFragment();
            }else if(itemView == R.id.navigation_schedules){
                selectedFragment =new SchedulesFragment();
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


    private NavigationView.OnNavigationItemSelectedListener navDrawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;
            int itemView = item.getItemId();

            /*if (itemView == R.id.nav_dashboard) {
                selectedFragment = new HomeFragment();
            }else if (itemView == R.id.nav_logout) {
                SharedPrefManager.getInstance(getApplicationContext().getApplicationContext()).isLoggedOut();
            }else if (itemView == R.id.nav_events) {
                selectedFragment = new CompetitionFragment();
            }else if (itemView == R.id.nav_schedules) {
                selectedFragment = new SchedulesFragment();
            }else if (itemView == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }


            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, selectedFragment)
                        .commit();
            }*/

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };
}