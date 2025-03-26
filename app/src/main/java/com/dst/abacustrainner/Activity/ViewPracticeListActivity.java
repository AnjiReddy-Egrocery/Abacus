package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Adapter.ViewListTopicAdapter;
import com.dst.abacustrainner.Model.ViewTopicListResponse;
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

public class ViewPracticeListActivity extends AppCompatActivity {

   RecyclerView recyclerViewTopic;

    String topicid="";
    String studentid="";
    String name="";

    String topicname="";
    TextView txtName,txtTopicName, txtNodata;
    ViewListTopicAdapter viewListTopicAdapter;
    ProgressBar progressBar;
    LinearLayout layoutBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_practice_list);
        txtName=findViewById(R.id.txt_name);
        txtTopicName=findViewById(R.id.txt_topic_name);
        recyclerViewTopic=findViewById(R.id.recycler_view_topic);
        progressBar= findViewById(R.id.progress_bar);

        Bundle bundle=getIntent().getExtras();
        topicid=bundle.getString("topicId");
        studentid=bundle.getString("studentId");
        name=bundle.getString("firstName");
        topicname=bundle.getString("topicName");
        txtNodata = findViewById(R.id.txtNoData);
        layoutBack = findViewById(R.id.layout_back);

       // txtName.setText(name);
        txtTopicName.setText(topicname);

        Log.e("Reddy",""+topicid);
        Log.e("Reddy",""+studentid);


        LinearLayoutManager layoutManager=new LinearLayoutManager(ViewPracticeListActivity.this);
        recyclerViewTopic.setLayoutManager(layoutManager);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        VerifyMethod(studentid,topicid);
    }
    private void VerifyMethod(String studentid, String topicid) {
        progressBar.setVisibility(View.VISIBLE);
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
        RequestBody topicPart = RequestBody.create(MediaType.parse("text/plain"), topicid);

        Call<ViewTopicListResponse> call=apiClient.viewTopicList(idPart,topicPart);
        call.enqueue(new Callback<ViewTopicListResponse>() {
            @Override
            public void onResponse(Call<ViewTopicListResponse> call, Response<ViewTopicListResponse> response) {
                progressBar.setVisibility(View.GONE);
                ViewTopicListResponse viewTopicListResponse = response.body();

                if (viewTopicListResponse.getErrorCode().equals("202")) {
                    Toast.makeText(ViewPracticeListActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                    recyclerViewTopic.setVisibility(View.GONE); // Hide RecyclerView
                } else if (viewTopicListResponse.getErrorCode().equals("200")) {
                    ViewTopicListResponse.Result result = viewTopicListResponse.getResult();
                    List<ViewTopicListResponse.Result.Practices> topicsList = result.getPracticesList();

                    if (topicsList.isEmpty()) {
                        txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                        recyclerViewTopic.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        txtNodata.setVisibility(View.GONE); // Hide No Data Found TextView
                        recyclerViewTopic.setVisibility(View.VISIBLE); // Show RecyclerView

                        viewListTopicAdapter = new ViewListTopicAdapter(ViewPracticeListActivity.this, topicsList);
                        recyclerViewTopic.setAdapter(viewListTopicAdapter);
                    }
                } else {
                    Toast.makeText(ViewPracticeListActivity.this, "Data Error", Toast.LENGTH_LONG).show();
                    txtNodata.setVisibility(View.VISIBLE); // Show No Data Found TextView
                    recyclerViewTopic.setVisibility(View.GONE); // Hide RecyclerView
                }
            }

            @Override
            public void onFailure(Call<ViewTopicListResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}