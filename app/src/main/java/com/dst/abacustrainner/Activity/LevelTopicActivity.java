package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.TopicsAdapter;

import com.dst.abacustrainner.Model.CourseLevelTopicResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.util.ArrayList;
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

public class LevelTopicActivity extends AppCompatActivity {


    private RecyclerView recyclerTopics;
    private TopicsAdapter adapter;

    LinearLayout layoutBack;
    private String studentId,courseLevelId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_topic);


        recyclerTopics = findViewById(R.id.recycler_topic);
        layoutBack = findViewById(R.id.fragment_container);


        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        studentId = getIntent().getStringExtra("StudentId");
        courseLevelId = getIntent().getStringExtra("LevelId");
        Log.e("Reddy",studentId);
        Log.e("Reddy",courseLevelId);
        // Load static topics based on level
        //loadTopicsForLevel(levelName);

        adapter = new TopicsAdapter(this);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerTopics.setAdapter(adapter);

        loadTopics(studentId,courseLevelId);
    }

    private void loadTopics(String studentId, String courseLevelId) {
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
        RequestBody CourseTopicLevelPart = RequestBody.create(MediaType.parse("text/plain"),courseLevelId);

        Call<CourseLevelTopicResponse> call = apiClient.getCourseLevelTopic(CourseLevelPart,CourseTopicLevelPart);
        call.enqueue(new Callback<CourseLevelTopicResponse>() {
            @Override
            public void onResponse(Call<CourseLevelTopicResponse> call, Response<CourseLevelTopicResponse> response) {

                            if (response.isSuccessful() && response.body() != null) {

                                CourseLevelTopicResponse res = response.body();

                                if ("200".equals(res.getErrorCode())) {

                                    if (res.getResult() != null &&
                                            res.getResult().getCourseLevelTopics() != null &&
                                            !res.getResult().getCourseLevelTopics().isEmpty()) {

                                        List<CourseLevelTopicResponse.courseLevelTopics> levels =
                                                res.getResult().getCourseLevelTopics();
                            adapter.setLevels(levels,studentId,courseLevelId);

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
            public void onFailure(Call<CourseLevelTopicResponse> call, Throwable t) {

            }
        });
    }


}