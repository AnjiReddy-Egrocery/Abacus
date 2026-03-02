package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Adapter.AssignmentListAdapter;
import com.dst.abacustrainner.Model.AssignmentListResponse;
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

public class AssignmentListActivity extends AppCompatActivity {
    RecyclerView recyclerAssignList;

    String dateid="";
    String studentid="";
    String name="",date="",startTime="",endTime="";
    TextView txtName;

    AssignmentListAdapter assignmentListAdapter;
    ProgressBar progressBar;

    LinearLayout btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_list);

        btnBack = findViewById(R.id.btn_back_to_home);
        txtName=findViewById(R.id.txt_batch_name);
        Bundle bundle=getIntent().getExtras();
        dateid=bundle.getString("dateId");
        studentid=bundle.getString("studentId");
        name=bundle.getString("batchName");
        date=bundle.getString("scheduleDate");
        startTime=bundle.getString("startTime");
        endTime=bundle.getString("endTime");
        progressBar= findViewById(R.id.progress);
        String header = name + "||" + date;

        txtName.setText(header);


        recyclerAssignList=findViewById(R.id.recycler_assignments);

        LinearLayoutManager layoutManager=new LinearLayoutManager(AssignmentListActivity.this);
        recyclerAssignList.setLayoutManager(layoutManager);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(AssignmentListActivity.this, BatchDatesDetailsActivity.class);
                startActivity(intent);
            }
        });

        VerifyMethod(dateid,studentid);

    }

    private void VerifyMethod(String dateid, String studentid) {
        progressBar.setVisibility(View.VISIBLE);

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
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateid);

        Call<AssignmentListResponse> call=apiClient.assignList(idPart,datePart);
        call.enqueue(new Callback<AssignmentListResponse>() {
            @Override
            public void onResponse(Call<AssignmentListResponse> call, Response<AssignmentListResponse> response) {
                progressBar.setVisibility(View.GONE);
                AssignmentListResponse assignmentListResponse=response.body();
                if (assignmentListResponse.getErrorCode().equals("202")) {

                    Toast.makeText(AssignmentListActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();

                } else if (assignmentListResponse.getErrorCode().equals("200")) {
                    AssignmentListResponse.Result result = assignmentListResponse.getResult();
                    List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList = result.getAssignmentTopicsList();

                    assignmentListAdapter = new AssignmentListAdapter(AssignmentListActivity.this, assignmentTopicsList);
                    recyclerAssignList.setAdapter(assignmentListAdapter);


                } else {
                    Toast.makeText(AssignmentListActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AssignmentListResponse> call, Throwable t) {

            }

        });
    }
}