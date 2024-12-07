package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Adapter.TopicListAdapter;
import com.dst.abacustrainner.Model.TopicListResponse;
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

public class TopicListActivity extends AppCompatActivity {

    RecyclerView recyclerTopicList;
    String dateid="";
    String studentid="";
    String name="",date="",startTime="",endTime="";
    TopicListAdapter topicListAdapter;
    TextView txtName,txtDate,txtStartTime,txtEndTime;
    ProgressBar progressBar;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_list);

        txtName=findViewById(R.id.txt_batch_name);
        txtDate=findViewById(R.id.txt_date);
        txtStartTime=findViewById(R.id.txt_time);
        txtEndTime=findViewById(R.id.txtdate);
        progressBar=findViewById(R.id.progress);

        Bundle bundle=getIntent().getExtras();


        dateid=bundle.getString("dateId");
        studentid=bundle.getString("studentId");
        name=bundle.getString("batchName");
        date=bundle.getString("scheduleDate");
        startTime=bundle.getString("startTime");
        endTime=bundle.getString("endTime");

        txtStartTime.setText(startTime);
        txtEndTime.setText(endTime);
        txtName.setText(name);
        txtDate.setText(date);


        Log.e("Reddy",""+dateid);
        Log.e("Reddy",""+studentid);

        recyclerTopicList=findViewById(R.id.recycler_topic);
        LinearLayoutManager layoutManager=new LinearLayoutManager(TopicListActivity.this);
        recyclerTopicList.setLayoutManager(layoutManager);

        VerifyMethod(dateid,studentid);
    }
    private void VerifyMethod(String dateid, String studentid) {
        progressBar.setVisibility(View.VISIBLE);
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentid);
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateid);

        Call<TopicListResponse> call=apiClient.topicList(idPart,datePart);
        call.enqueue(new Callback<TopicListResponse>() {
            @Override
            public void onResponse(Call<TopicListResponse> call, Response<TopicListResponse> response) {
                progressBar.setVisibility(View.GONE);
                TopicListResponse topicListResponse = response.body();
                if (topicListResponse.getErrorCode().equals("202")) {

                    Toast.makeText(TopicListActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();

                } else if (topicListResponse.getErrorCode().equals("200")) {
                    TopicListResponse.Result result = topicListResponse.getResult();
                    List<TopicListResponse.Result.Topics> topicsList = result.getTopicsList();

                    topicListAdapter = new TopicListAdapter(TopicListActivity.this, topicsList);
                    recyclerTopicList.setAdapter(topicListAdapter);


                } else {
                    Toast.makeText(TopicListActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TopicListResponse> call, Throwable t) {

            }
        });
    }

}