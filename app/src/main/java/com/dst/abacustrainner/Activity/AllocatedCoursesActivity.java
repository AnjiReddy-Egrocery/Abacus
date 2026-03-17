package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.Adapter.CourseLevelAdapter;
import com.dst.abacustrainner.Adapter.CourseTypeAdapter;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.Model.CourseType;
import com.dst.abacustrainner.Model.CourseTypeResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;

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

public class AllocatedCoursesActivity extends AppCompatActivity {

    LinearLayout layoutBack;
    RecyclerView recyclerCourseType;
    private String studentId;
    CourseTypeAdapter coursetypeAdapter;
    TextView txtNoCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocated_courses);

        layoutBack = findViewById(R.id.layout_back);
        recyclerCourseType = findViewById(R.id.recycler_course_type);
        txtNoCourses = findViewById(R.id.txtNoCourses);

        studentId = getIntent().getStringExtra("studentId");
        Log.e("Reddy",studentId);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerCourseType.setLayoutManager(new LinearLayoutManager(this));
        coursetypeAdapter = new CourseTypeAdapter(this);
        recyclerCourseType.setAdapter(coursetypeAdapter);


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

        Call<CourseTypeResponse> call = apiClient.getCourseTypeList(CourseLevelPart);
        call.enqueue(new Callback<CourseTypeResponse>() {
            @Override
            public void onResponse(Call<CourseTypeResponse> call, Response<CourseTypeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    CourseTypeResponse res = response.body();

                    // ✅ SUCCESS CASE
                    if ("200".equals(res.getErrorCode())) {

                        List<CourseType> courseTypes = res.getResult();

                        if (courseTypes != null && !courseTypes.isEmpty()) {

                            // ✅ send full list to adapter
                            coursetypeAdapter.setCourseTypes(courseTypes, studentId);

                        } else {
                            recyclerCourseType.setVisibility(View.GONE);
                            txtNoCourses.setVisibility(View.VISIBLE);
                            txtNoCourses.setText("No Courses Available");
                        }
                    } // ⚠️ NO ACTIVE SUBSCRIPTION
                    else if ("202".equals(res.getErrorCode())) {

                        recyclerCourseType.setVisibility(View.GONE);
                        txtNoCourses.setVisibility(View.VISIBLE);

                        txtNoCourses.setText(res.getErrorMessage());
                    }
                    // ❌ OTHER ERROR
                    else {
                        // showEmptyMessage(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseTypeResponse> call, Throwable t) {
                //showEmptyMessage("Server error. Please try again.");

            }
        });


    }
}