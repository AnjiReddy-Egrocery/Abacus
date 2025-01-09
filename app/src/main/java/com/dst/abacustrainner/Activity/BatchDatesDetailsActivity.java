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


import com.dst.abacustrainner.Adapter.BatchDatesDetailsAdapter;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.SharedPrefManager;

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

public class BatchDatesDetailsActivity extends AppCompatActivity {
    RecyclerView recyclerBatchDates;

    String bactchId="";

    String name="";

    String id="";

    TextView txtName;
    BatchDatesDetailsAdapter batchDatesDetailsAdapter;

    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_dates_details);

        txtName=findViewById(R.id.txt_batch_name);
        Bundle bundle=getIntent().getExtras();
        bactchId=bundle.getString("batchId");
        name=bundle.getString("batchName");
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(getApplicationContext()).getUserData();
        id=result.getStudentId();

        txtName.setText(name);

        progressBar = findViewById(R.id.progressBar);

        Log.e("Reddy","Id"+id);
        Log.e("Reddy","BatchId"+bactchId);

        recyclerBatchDates=findViewById(R.id.recycler_dates);
        LinearLayoutManager layoutManager=new LinearLayoutManager(BatchDatesDetailsActivity.this);
        recyclerBatchDates.setLayoutManager(layoutManager);


        VerifyMethod(id,bactchId);

    }

    private void VerifyMethod(String id, String bactchId) {
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
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody batchIdPart = RequestBody.create(MediaType.parse("text/plain"), bactchId);

        Call<DatedetailsResponse> call=apiClient.batchDateData(idPart,batchIdPart);
        call.enqueue(new Callback<DatedetailsResponse>() {
            @Override
            public void onResponse(Call<DatedetailsResponse> call, Response<DatedetailsResponse> response) {
                progressBar.setVisibility(View.GONE);
                DatedetailsResponse details=response.body();
                if (details.getErrorCode().equals("202")){
                    Toast.makeText(BatchDatesDetailsActivity.this,"No Schedule, for the given details",Toast.LENGTH_LONG).show();
                }else if (details.getErrorCode().equals("200")){
                 List<DatedetailsResponse.Result> daResult=details.getResult();

                 String name="";
                 String startDate="";
                 String startTime="";
                 String endTime="";

                 for (int i=0; i<daResult.size(); i++){
                     name=daResult.get(i).getBatchName();
                     startDate=daResult.get(i).getStartDate();
                     startTime=daResult.get(i).getEndTime();
                     endTime=daResult.get(i).getEndTime();
                     List<DatedetailsResponse.Result.Date> dateList=daResult.get(i).getDates();
                     Log.e("Reddy","Dates"+dateList);
                     batchDatesDetailsAdapter =new BatchDatesDetailsAdapter(BatchDatesDetailsActivity.this,dateList,name,startTime,endTime);
                     recyclerBatchDates.setAdapter(batchDatesDetailsAdapter);
                 }

                }
            }

            @Override
            public void onFailure(Call<DatedetailsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}