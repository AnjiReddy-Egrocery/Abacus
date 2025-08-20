package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.dst.abacustrainner.Model.StudentTotalDetails;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.android.material.navigation.NavigationView;

import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailsActivity extends AppCompatActivity {

    String studentId;
    Toolbar toolbar;
    TextView txtName;
    ImageView imageProfile;

    Button butWorkSheetSubscrioption,butVideoTutorials;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        toolbar = findViewById(R.id.detail_toolbar);
        txtName = findViewById(R.id.detail_txt_name);
        imageProfile = findViewById(R.id.detail_imgProfile);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        butWorkSheetSubscrioption = findViewById(R.id.but_worksheet);
        butVideoTutorials = findViewById(R.id.but_videotutorial);

        butVideoTutorials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, VideoCoursesActivity.class);
                startActivity(intent);
            }
        });
        butWorkSheetSubscrioption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, CoursesActivity.class);
                startActivity(intent);
            }
        });
        /*String navigateTo = getIntent().getStringExtra("navigate_to");
        if (navigateTo != null) {
            if (navigateTo.equals("video")) {
                butVideoTutorials.performClick();  // Auto click video
            } else if (navigateTo.equals("live")) {
                butWorkSheetSubscrioption.performClick();  // Auto click live
            }
        }
*/

        StudentTotalDetails.Result studentdetails = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        studentId = studentdetails.getStudentId();
        StudentDetailsMethod(studentId);

    }

    public void StudentDetailsMethod(String studentId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        Call<StudentTotalDetails> call=apiClient.studentData(idPart);
        call.enqueue(new Callback<StudentTotalDetails>() {
            @Override
            public void onResponse(Call<StudentTotalDetails> call, Response<StudentTotalDetails> response) {
                if (response.isSuccessful()) {
                    StudentTotalDetails studentTotalDetails = response.body();
                    String firstName = capitalizeFirstLetter(studentTotalDetails.getResult().getFirstName());
                    String lastName = capitalizeFirstLetter(studentTotalDetails.getResult().getLastName());
                    String fullName = firstName + " " + lastName;
                    txtName.setText(fullName);

                    String imageUrl = studentTotalDetails.getImageUrl() + studentTotalDetails.getResult().getProfilePic();

                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .signature(new ObjectKey(System.currentTimeMillis())) // forces fresh load
                            .circleCrop()
                            .into(imageProfile);


                } else {
                    Toast.makeText(DetailsActivity.this, "Server Error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StudentTotalDetails> call, Throwable t) {
                Log.e("DEBUG", "API call failed", t);
                Toast.makeText(DetailsActivity.this, "API Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String capitalizeFirstLetter(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            return ""; // Return empty string if name is null or empty
        }
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
    }
}