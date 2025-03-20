package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Adapter.ViewAssignmentAdapter;
import com.dst.abacustrainner.Model.ViewAssignmentListResponse;
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

public class ViewAssignmentListActivity extends AppCompatActivity {
    RecyclerView recyclerViewTopic;

    String topicid="";
    String studentid="";
    String name="";

    String topicname="";
    TextView txtTopicName;
    ViewAssignmentAdapter viewAssignmentAdapter;

    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignment_list);

        //txtName=findViewById(R.id.txt_name);
        txtTopicName=findViewById(R.id.txt_topic_name);
        recyclerViewTopic=findViewById(R.id.recycler_view_topic);

        Bundle bundle=getIntent().getExtras();
        topicid=bundle.getString("topicId");
        studentid=bundle.getString("studentId");
        name=bundle.getString("firstName");
        topicname=bundle.getString("topicName");
        progressBar= findViewById(R.id.progress_bar);
        //txtName.setText(name);
        txtTopicName.setText(topicname);


        LinearLayoutManager layoutManager=new LinearLayoutManager(ViewAssignmentListActivity.this);
        recyclerViewTopic.setLayoutManager(layoutManager);

        VerifyMethod(studentid,topicid);
    }

    private void VerifyMethod(String studentid, String topicid) {
        progressBar.setVisibility(View.VISIBLE);
        /*HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);*/
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

        Call<ViewAssignmentListResponse> call=apiClient.viewAssignmentList(idPart,topicPart);
        call.enqueue(new Callback<ViewAssignmentListResponse>() {
            @Override
            public void onResponse(Call<ViewAssignmentListResponse> call, Response<ViewAssignmentListResponse> response) {
                progressBar.setVisibility(View.GONE);
                ViewAssignmentListResponse assignmentListResponse=response.body();
                if (assignmentListResponse.getErrorCode().equals("202")){
                    Toast.makeText(ViewAssignmentListActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                }else if (assignmentListResponse.getErrorCode().equals("200")){
                    ViewAssignmentListResponse.Result result=assignmentListResponse.getResult();
                    List<ViewAssignmentListResponse.Result.Practices> practicesList=result.getPracticesList();

                    viewAssignmentAdapter =new ViewAssignmentAdapter(ViewAssignmentListActivity.this,practicesList);
                    recyclerViewTopic.setAdapter(viewAssignmentAdapter);

                }else {
                    Toast.makeText(ViewAssignmentListActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ViewAssignmentListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}