package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.CourseLevelAdapter;
import com.dst.abacustrainner.Adapter.CourseLevelTypeAdaper;
import com.dst.abacustrainner.Adapter.OrderListAdapter;
import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.Model.OrderListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PurchasedActivity extends AppCompatActivity {

    private LinearLayout  layoutBack;
    private CartManager cartManager;
    private String studentId;
    RecyclerView recyclerCourses;
    CourseLevelAdapter courseLevelAdapter;
    TextView tvEmptyMessage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased);



        layoutBack = findViewById(R.id.fragment_container);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);
        recyclerCourses = findViewById(R.id.recycler_course);
        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));

        studentId = getIntent().getStringExtra("studentId");

        Log.d("Anji","Student Details"+ studentId);

        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));

        courseLevelAdapter = new CourseLevelAdapter(this);
        recyclerCourses.setAdapter(courseLevelAdapter);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


            loadCourses(studentId);




    }

    private void loadCourses(String studentId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody CourseLevelPart = RequestBody.create(MediaType.parse("text/plain"), studentId);

        Call<CourseListResponse> call = apiClient.getCourseList(CourseLevelPart);
        call.enqueue(new Callback<CourseListResponse>() {
            @Override
            public void onResponse(Call<CourseListResponse> call, Response<CourseListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    CourseListResponse res = response.body();

                    if ("200".equals(res.getErrorCode())) {

                        // ✅ Correct data mapping
                        if (res.getResult() != null && !res.getResult().isEmpty()) {

                            // 👉 result = Course Types
                            List<CourseListResponse.Result> courseTypes = res.getResult();

                            // 👉 Adapter ki set cheyyali
                            courseLevelAdapter.setCourseTypes(courseTypes, studentId);

                        } else {
                            showEmptyMessage(res.getEmptyAssignmentTopicsessage());
                        }

                    } else if ("202".equals(res.getErrorCode())) {

                        showEmptyMessage(
                                "You do not have any active worksheet subscriptions. " +
                                        "Please contact administrator."
                        );

                    } else {
                        showEmptyMessage(res.getMessage());
                    }
                } else {
                    showEmptyMessage("Response error");
                }
            }

            @Override
            public void onFailure(Call<CourseListResponse> call, Throwable t) {
                showEmptyMessage("Server error. Please try again.");
            }
        });
    }

    private void showEmptyMessage(String message) {
        recyclerCourses.setVisibility(View.GONE);
        tvEmptyMessage.setVisibility(View.VISIBLE);
        tvEmptyMessage.setText(message);
    }
}