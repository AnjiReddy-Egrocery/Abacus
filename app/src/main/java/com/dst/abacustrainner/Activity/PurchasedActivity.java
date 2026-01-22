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
import com.dst.abacustrainner.Adapter.OrderListAdapter;
import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.Model.OrderListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

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

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased);



        layoutBack = findViewById(R.id.fragment_container);
        recyclerCourses = findViewById(R.id.recycler_course);

        studentId = getIntent().getStringExtra("StudentId");
        Log.e("Reddy",studentId);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerCourses.setLayoutManager(new LinearLayoutManager(this));
        courseLevelAdapter = new CourseLevelAdapter(this);
        recyclerCourses.setAdapter(courseLevelAdapter);


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

                    // ✅ SUCCESS CASE
                    if ("200".equals(res.getErrorCode())) {

                        if (res.getResult() != null &&
                                res.getResult().getCourseType() != null &&
                                !res.getResult().getCourseType().isEmpty()) {

/*
                            List<CourseListResponse.CourseLevels> levels =
                                    res.getResult().getCourseLevels();
*/

                            List<CourseListResponse.CourseLevels> levels = res.getResult().getCourseLevels();

                            courseLevelAdapter.setLevels(levels,studentId);

                        } else {
                            //showEmptyMessage(res.getEmptyAssignmentTopicsessage());
                        }

                    }
                    // ⚠️ NO ACTIVE SUBSCRIPTION
                    else if ("202".equals(res.getErrorCode())) {

                       /* showEmptyMessage(
                                "You do not have any active worksheet subscriptions. " +
                                        "Please contact the administrator for more information."
                        );*/
                    }
                    // ❌ OTHER ERROR
                    else {
                       // showEmptyMessage(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<CourseListResponse> call, Throwable t) {
                //showEmptyMessage("Server error. Please try again.");

            }
        });


    }


}