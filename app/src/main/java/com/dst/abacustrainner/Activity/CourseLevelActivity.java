package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.dst.abacustrainner.Adapter.LevelTopicAdapter;
import com.dst.abacustrainner.Model.CourseType;
import com.dst.abacustrainner.Model.CourseTypeLevel;
import com.dst.abacustrainner.Model.CourseTypeResponse;
import com.dst.abacustrainner.Model.CoursesResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

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

public class CourseLevelActivity extends AppCompatActivity {

    private String  studentId, courseLevelId ;
    LinearLayout layoutBack;
    RecyclerView recyclerCourseType;
    LevelTopicAdapter courseTypeLevelTopicAdapter;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_level);

        layoutBack = findViewById(R.id.layout_back);
        recyclerCourseType = findViewById(R.id.recycler_course_list);


        studentId = getIntent().getStringExtra("StudentId");
        courseLevelId = getIntent().getStringExtra("CourseLevelId");

        Log.e("Reddy","studentId"+studentId);
        Log.e("Reddy","courseLevelId"+courseLevelId);

        recyclerCourseType.setLayoutManager(new LinearLayoutManager(this));
        courseTypeLevelTopicAdapter = new LevelTopicAdapter(this);
        recyclerCourseType.setAdapter(courseTypeLevelTopicAdapter);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadCoursesList(studentId,courseLevelId);



    }

    private void loadCoursesList(String studentId, String courseLevelId) {
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
        RequestBody CourseLevelIdPart = RequestBody.create(MediaType.parse("text/plain"), courseLevelId);

        Call<CoursesResponse> call = apiClient.getCourseTypeListTopics(CourseLevelPart,CourseLevelIdPart);
        call.enqueue(new Callback<CoursesResponse>() {
            @Override
            public void onResponse(Call<CoursesResponse> call, Response<CoursesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    CoursesResponse res = response.body();

                    if ("200".equals(res.getErrorCode())) {

                        List<CourseTypeLevel> levels = res.getResult();

                        if (levels != null && !levels.isEmpty()) {

                            // ✅ send topics data
                            courseTypeLevelTopicAdapter.setData(levels, studentId);

                        } else {
                            Log.e("API", "No topics found");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CoursesResponse> call, Throwable t) {
                Log.e("API", "Error: " + t.getMessage());
            }
        });
    }


 }