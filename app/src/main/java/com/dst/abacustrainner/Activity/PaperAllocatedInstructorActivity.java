package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.PaperInstructorAdapter;
import com.dst.abacustrainner.Adapter.TopicListAdapter;
import com.dst.abacustrainner.Model.PaperListResponse;
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

public class PaperAllocatedInstructorActivity extends AppCompatActivity {

    private String studentId;
    RecyclerView recyclerPaperList;
    LinearLayout layoutBack;
    TextView txtEmpty;
   PaperInstructorAdapter paperInstructorAdapter;
    ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paper_allocated_instructor);

        layoutBack = findViewById(R.id.btn_back_to_home);
        txtEmpty = findViewById(R.id.txt_empty);

        Bundle bundle=getIntent().getExtras();


        studentId=bundle.getString("studentId");

        Log.e("Reddy",""+studentId);

        recyclerPaperList=findViewById(R.id.recycler_papers);
        LinearLayoutManager layoutManager=new LinearLayoutManager(PaperAllocatedInstructorActivity.this);
        recyclerPaperList.setLayoutManager(layoutManager);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // go back safely


            }
        });

        VerifyMethod(studentId);


    }

    private void VerifyMethod(String studentId) {
        progressDialog = new ProgressDialog(PaperAllocatedInstructorActivity.this);
        progressDialog.setMessage("Loading Please wait ......");
        progressDialog.setCancelable(false);
        progressDialog.show();

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
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);

        Call<PaperListResponse> call=apiClient.paperList(idPart);
        call.enqueue(new Callback<PaperListResponse>() {
            @Override
            public void onResponse(Call<PaperListResponse> call, Response<PaperListResponse> response) {
                if(progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                PaperListResponse paperListResponse = response.body();
                if (paperListResponse.getErrorCode().equals("202")) {

                    recyclerPaperList.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.VISIBLE);
                    txtEmpty.setText("Instructor Not Allocated Topics, Please Contact Instructor For More Details");

                } else if (paperListResponse.getErrorCode().equals("200")) {
                    List<PaperListResponse.Result> paperList = paperListResponse.getResult();

                    if (paperList != null && !paperList.isEmpty()) {
                        recyclerPaperList.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.GONE);

                        paperInstructorAdapter = new PaperInstructorAdapter(PaperAllocatedInstructorActivity.this, paperList);
                        recyclerPaperList.setAdapter(paperInstructorAdapter);


                    } else {
                        recyclerPaperList.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);

                        // backend message use cheyyachu
                        txtEmpty.setText(paperListResponse.getEmptyTopicsessage());
                    }

                }
            }

            @Override
            public void onFailure(Call<PaperListResponse> call, Throwable t) {

            }
        });
    }

}