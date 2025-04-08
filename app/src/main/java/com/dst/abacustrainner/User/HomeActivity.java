package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dst.abacustrainner.Activity.PlayWithNumbersActivity;
import com.dst.abacustrainner.Activity.VisualQuizActivity;
import com.dst.abacustrainner.Activity.VisualiztionActivity;
import com.dst.abacustrainner.Adapter.BatchDetailsAdapter;
import com.dst.abacustrainner.Fragment.AIGenrationFragment;
import com.dst.abacustrainner.Fragment.ClassFragment;

import com.dst.abacustrainner.Fragment.CompetitionFragment;
import com.dst.abacustrainner.Fragment.HomeFragment;
import com.dst.abacustrainner.Fragment.PracticeFragment;
import com.dst.abacustrainner.Fragment.ProfileFragment;
import com.dst.abacustrainner.Fragment.SchedulesFragment;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HomeActivity extends AppCompatActivity{

    String studentId,firstName,LastName;
    Object profilePic ;
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

    TextView txtName;
    String fullName;
    ImageView imageProfile;
    String batchId;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        txtName = findViewById(R.id.txt_name);
        imageProfile = findViewById(R.id.imgProfile);





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
        firstName = result.getFirstName();
        LastName = result.getLastName();
        profilePic = result.getProfilePic();

        Log.e("Reddy","image"+profilePic);

        String imageUrl = "https://www.abacustrainer.com/assets/student_images/"+profilePic;

        Log.e("Reddy","image"+imageUrl);

        String formattedFirstName = capitalizeFirstLetter(firstName);
        String formattedLastName = capitalizeFirstLetter(LastName);

        fullName= formattedFirstName +formattedLastName;

        txtName.setText(fullName);

        Glide.with(this)
                .load(R.drawable.headerprofile) // You can use URL also
                .circleCrop()
                .into(imageProfile);

        Log.e("Reddy","Name"+fullName);

        if (studentId != null) {
            batchId = SharedPrefManager.getInstance(getApplicationContext()).getBatchId();

            if (batchId != null && !batchId.isEmpty()) {
                // ✅ If batchId already available, load directly
                loadHomeFragmentWithStudentId(studentId, batchId);
            } else {
                // ❗ If not, call API to get batchId, then load fragment
                scheduleMethod(studentId);
            }
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
        TextView txtUserName = headerView.findViewById(R.id.user_name);

        txtUserName.setText(fullName);

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

    private String capitalizeFirstLetter(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return ""; // Return empty string if name is null or empty
        }
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }

    private void loadHomeFragmentWithStudentId(String studentId, String batchId) {

        Log.d("Reddy", "BatchId used in loadHomeFragment: " + batchId);

        Bundle bundle = new Bundle();
        bundle.putString("studentId", studentId);
        bundle.putString("batchId", batchId);


        Log.d("HomeFragment", "StudentId: " + studentId + ", BatchId: " + batchId);

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

                scheduleMethod(studentId);  // Call method (no fragment return needed)
                return true;

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

    private void scheduleMethod(String studentId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        Call<BachDetailsResponse> call=apiClient.batchData(idPart);
        call.enqueue(new Callback<BachDetailsResponse>() {
            @Override
            public void onResponse(Call<BachDetailsResponse> call, Response<BachDetailsResponse> response) {

                if (response.isSuccessful()){
                    BachDetailsResponse bachDetailsResponse=response.body();
                    if (bachDetailsResponse.getErrorCode().equals("202")){
                        Toast.makeText(HomeActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    }else if (bachDetailsResponse.getErrorCode().equals("200")){

                        List<BachDetailsResponse.Result> results=bachDetailsResponse.getResult();

                        if (!results.isEmpty()) {

                            batchId = results.get(0).getBatchId();
                            Log.d("Reddy", "StudentId" + studentId);
                            Log.d("Reddy", "BatchId" + batchId);
                            SharedPrefManager.getInstance(getApplicationContext()).saveBatchId(batchId);
                            loadHomeFragmentWithStudentId(studentId, batchId); // ✅ Place this here
                            openSchedulesFragment(studentId, batchId);
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                    }



                }
            }

            @Override
            public void onFailure(Call<BachDetailsResponse> call, Throwable t) {

            }
        });
    }

    private void openSchedulesFragment(String studentId, String batchId) {

        Bundle bundle = new Bundle();
        bundle.putString("studentId", studentId);
        bundle.putString("batchId",batchId);

        SchedulesFragment schedulesFragment = new SchedulesFragment();
        schedulesFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, schedulesFragment)
                .commit();
    }


    private NavigationView.OnNavigationItemSelectedListener navDrawerListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;
            int itemView = item.getItemId();

            if (itemView == R.id.nav_dashboard) {
                selectedFragment = new HomeFragment();
            }else if (itemView == R.id.nav_logout) {
                SharedPrefManager.getInstance(getApplicationContext().getApplicationContext()).isLoggedOut();
            }else if (itemView == R.id.nav_events) {
                selectedFragment = new CompetitionFragment();
            }else if (itemView == R.id.nav_schedules) {
                scheduleMethod(studentId);  // Call method (no fragment return needed)
                return true;
            }else if (itemView == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }else if (itemView == R.id.nav_play_with_numbers ) {
                Intent intent = new Intent(HomeActivity.this, PlayWithNumbersActivity.class);
                intent.putExtra("studentId",studentId);
                //intent.putExtra("firstName",firsstname);
                startActivity(intent);
            }else if (itemView == R.id.nav_visualization) {
                Intent intent = new Intent(HomeActivity.this, VisualiztionActivity.class);
                intent.putExtra("studentId",studentId);
                startActivity(intent);
            }else if (itemView == R.id.nav_exam) {
                selectedFragment = new AIGenrationFragment();
            }


            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, selectedFragment)
                        .commit();
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    };
}