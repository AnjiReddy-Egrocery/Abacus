package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.ViewListInstructorAdapter;
import com.dst.abacustrainner.Adapter.ViewListTopicAdapter;
import com.dst.abacustrainner.Model.ViewInstructorListResponse;
import com.dst.abacustrainner.Model.ViewTopicListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.util.Collections;
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

public class InstructorViewPracticeActivity extends AppCompatActivity {

    RecyclerView recyclerViewTopic;

    String paperId="";
    String studentid="";

    String topicname="";
    TextView txtName,txtTopicName, txtNodata;
    ViewListInstructorAdapter viewListInstructorAdapter;
    ProgressDialog progressDialog;
    LinearLayout layoutBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructor_view_practice);


        txtTopicName=findViewById(R.id.txt_topic_name);
        recyclerViewTopic=findViewById(R.id.recycler_view_topic);


        Bundle bundle=getIntent().getExtras();
        paperId=bundle.getString("PaperId");
        studentid=bundle.getString("StudentId");
        topicname=bundle.getString("PaperName");
        txtNodata = findViewById(R.id.txtNoData);
        layoutBack = findViewById(R.id.layout_back);

        // txtName.setText(name);
        txtTopicName.setText(topicname);

        Log.e("Reddy",""+paperId);
        Log.e("Reddy",""+studentid);


        LinearLayoutManager layoutManager=new LinearLayoutManager(InstructorViewPracticeActivity.this);
        recyclerViewTopic.setLayoutManager(layoutManager);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        VerifyMethod(studentid,paperId);
    }
    private void VerifyMethod(String studentid, String paperId) {
        progressDialog = new ProgressDialog(InstructorViewPracticeActivity.this);
        progressDialog.setMessage("Loading Please wait ......");
        progressDialog.setCancelable(false);
        progressDialog.show();
       /* HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
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
        RequestBody paperPart = RequestBody.create(MediaType.parse("text/plain"), paperId);

        Call<ViewInstructorListResponse> call=apiClient.viewInstructorPracticeList(idPart,paperPart);
        call.enqueue(new Callback<ViewInstructorListResponse>() {
            @Override
            public void onResponse(Call<ViewInstructorListResponse> call, Response<ViewInstructorListResponse> response) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ViewInstructorListResponse viewTopicListResponse = response.body();

                if (viewTopicListResponse.getErrorCode().equals("202")) {
                    Toast.makeText(InstructorViewPracticeActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                    recyclerViewTopic.setVisibility(View.GONE); // Hide RecyclerView
                } else if (viewTopicListResponse.getErrorCode().equals("200")) {
                    List<ViewInstructorListResponse.Result> result = viewTopicListResponse.getResult();
                    List<ViewInstructorListResponse.Result.PracticeModel> topicsList = result.get(0).getPracticesList();
                    Collections.reverse(topicsList);
                    String paperName = result.get(0).getPaperTitle();

                    if (topicsList.isEmpty()) {
                        txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                        recyclerViewTopic.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        // 👈 Latest data first
                        txtNodata.setVisibility(View.GONE); // Hide No Data Found TextView
                        recyclerViewTopic.setVisibility(View.VISIBLE); // Show RecyclerView

                        viewListInstructorAdapter = new ViewListInstructorAdapter(InstructorViewPracticeActivity.this, topicsList, paperName);
                        recyclerViewTopic.setAdapter(viewListInstructorAdapter);
                    }
                } else {
                    Toast.makeText(InstructorViewPracticeActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                    txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                    recyclerViewTopic.setVisibility(View.GONE); // Hide RecyclerView
                }
            }

            @Override
            public void onFailure(Call<ViewInstructorListResponse> call, Throwable t) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
        });

    }
}