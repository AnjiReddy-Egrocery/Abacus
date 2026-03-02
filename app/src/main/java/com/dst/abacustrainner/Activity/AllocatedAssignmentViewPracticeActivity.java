package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.AllocatedAssignmentViewSubListAdataper;
import com.dst.abacustrainner.Adapter.AllocatedViewSubListAdataper;
import com.dst.abacustrainner.Model.AlloactedViewSubTopicListResponse;
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

public class AllocatedAssignmentViewPracticeActivity extends AppCompatActivity {

    RecyclerView recyclerViewSubTopics;

    String topicid;
    String studentid;


    AllocatedAssignmentViewSubListAdataper viewListTopicAdapter;

    LinearLayout layoutBack;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocated_assignment_view_practice);

        recyclerViewSubTopics=findViewById(R.id.recycler_practices);


        Bundle bundle=getIntent().getExtras();
        topicid=bundle.getString("TopicId");
        studentid=bundle.getString("StudentId");

        layoutBack = findViewById(R.id.layout_payment_back);



        Log.e("Reddy",""+topicid);
        Log.e("Reddy",""+studentid);


        LinearLayoutManager layoutManager=new LinearLayoutManager(AllocatedAssignmentViewPracticeActivity.this);
        recyclerViewSubTopics.setLayoutManager(layoutManager);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        VerifyMethod(studentid,topicid);
    }

    private void VerifyMethod(String studentid, String topicid) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentid);
        RequestBody topicPart = RequestBody.create(MediaType.parse("text/plain"), topicid);

        Call<AlloactedViewSubTopicListResponse> call = apiClient.getAllocatedAssignmentSubViewListPractice(idPart,topicPart);
        call.enqueue(new Callback<AlloactedViewSubTopicListResponse>() {
            @Override
            public void onResponse(Call<AlloactedViewSubTopicListResponse> call, Response<AlloactedViewSubTopicListResponse> response) {
                AlloactedViewSubTopicListResponse viewTopicListResponse = response.body();

                if (viewTopicListResponse.getErrorCode().equals("202")) {
                    Toast.makeText(AllocatedAssignmentViewPracticeActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                } else if (viewTopicListResponse.getErrorCode().equals("200")) {
                    AlloactedViewSubTopicListResponse.Result result = viewTopicListResponse.getResult();
                    List<AlloactedViewSubTopicListResponse.Result.PracticesList> topicsList = result.getPracticesList();

                    if (topicsList.isEmpty()) {
                        recyclerViewSubTopics.setVisibility(View.GONE); // Hide RecyclerView
                    } else {

                        viewListTopicAdapter = new AllocatedAssignmentViewSubListAdataper(AllocatedAssignmentViewPracticeActivity.this, topicsList);
                        recyclerViewSubTopics.setAdapter(viewListTopicAdapter);
                    }
                } else {
                    Toast.makeText(AllocatedAssignmentViewPracticeActivity.this, "Data Error", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<AlloactedViewSubTopicListResponse> call, Throwable t) {

            }
        });

    }
}