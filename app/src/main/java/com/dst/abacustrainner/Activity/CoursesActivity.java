package com.dst.abacustrainner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Adapter.CoursesAdapter;
import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.CoursesListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CoursesActivity extends AppCompatActivity {


    LinearLayout layoutCourseBack;
    String studentId, batchId;
    RecyclerView recyclerView;
    CoursesAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);




        // Layouts
        layoutCourseBack = findViewById(R.id.layout_course_back);
        recyclerView = findViewById(R.id.recycler_courses_list);

        // Intent data
        studentId = getIntent().getStringExtra("studentId");
        batchId = getIntent().getStringExtra("batchId");

        Log.d("Reddy", "studentId=" + studentId + " batchId=" + batchId);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CoursesAdapter(this);
        recyclerView.setAdapter(adapter);


        //setupAccordion();
        //setupSubscribeButtons();
        setupBack();

        loadCourses();



    }

    private void loadCourses() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        Call<CoursesListResponse> call = apiClient.getCoursesList();
        call.enqueue(new Callback<CoursesListResponse>() {
            @Override
            public void onResponse(Call<CoursesListResponse> call, Response<CoursesListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setData(response.body().getResult());
                }
            }

            @Override
            public void onFailure(Call<CoursesListResponse> call, Throwable t) {

            }
        });

    }

    private void setupBack() {
        layoutCourseBack.setOnClickListener(v -> goHome());
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("batchId", batchId);
        startActivity(intent);
        finish();
    }


}
