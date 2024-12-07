package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.StudentScheduleInfo;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewDetailsActivity extends AppCompatActivity {

    LinearLayout layoutTopics,layoutAssignments;
    String dateid="",studentid="";
    String name="",date="",startTime="",endTime="";
    String topic="";
    String assignments="";
    String topicMessage="";
    String assignmentMessage="";
    TextView txtTopic,txtAssignments,txtName,txtDate,txtStartTime,txtEndTime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_details);

        layoutTopics=findViewById(R.id.layout_topic);
        layoutAssignments=findViewById(R.id.layout_assignments);
        txtTopic=findViewById(R.id.txt_topics);
        txtAssignments=findViewById(R.id.txt_assignments);

        txtName=findViewById(R.id.txt_batch_name);
        txtDate=findViewById(R.id.txt_date);
        txtStartTime=findViewById(R.id.txt_time);
        txtEndTime=findViewById(R.id.txtdate);


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

        VerifyMethod(dateid,studentid);
    }

    private void VerifyMethod(String dateid, String studentid) {

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
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateid);

        Call<StudentScheduleInfo> call=apiClient.scheduleInfo(idPart,datePart);
        call.enqueue(new Callback<StudentScheduleInfo>() {
            @Override
            public void onResponse(Call<StudentScheduleInfo> call, Response<StudentScheduleInfo> response) {
                if (response.isSuccessful()){

                    StudentScheduleInfo scheduleInfo=response.body();

                    if (scheduleInfo.getErrorCode().equals("202")){

                        Toast.makeText(ViewDetailsActivity.this, "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();

                    } else if (scheduleInfo.getErrorCode().equals("200")) {
                        StudentScheduleInfo.Result result=scheduleInfo.getResult();
                        topic="Topics : (" + result.getTopicsCount() + ")";
                        assignments="Assignment Topics : (" + result.getAssignmentTopicsCount() + ")";

                        // Topics Condition
                        if (result.getTopicsCount() == 0){
                            topicMessage=scheduleInfo.getEmptyTopicsessage();
                            txtTopic.setText(topicMessage);

                        }else {
                            txtTopic.setText(topic);
                            setTopicClickListner();
                        }

                        // Assignments
                        if (result.getAssignmentTopicsCount ()==0){
                            layoutAssignments.setVisibility(View.GONE);
                        }else {
                            layoutAssignments.setVisibility(View.VISIBLE);
                            txtAssignments.setText(assignments);
                            setAssignmentClickListner();
                        }
                    }

                }else {
                    Toast.makeText(ViewDetailsActivity.this,"Data Error",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<StudentScheduleInfo> call, Throwable t) {

            }
        });

    }

    private void setAssignmentClickListner() {
        layoutAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewDetailsActivity.this,AssignmentListActivity.class);
                intent.putExtra("dateId",dateid);
                intent.putExtra("studentId",studentid);
                intent.putExtra("batchName",name);
                intent.putExtra("startTime",startTime);
                intent.putExtra("endTime",endTime);
                intent.putExtra("scheduleDate",date);
                startActivity(intent);
            }
        });
    }

    private void setTopicClickListner() {
        layoutTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ViewDetailsActivity.this,TopicListActivity.class);
                intent.putExtra("dateId",dateid);
                intent.putExtra("studentId",studentid);
                intent.putExtra("batchName",name);
                intent.putExtra("startTime",startTime);
                intent.putExtra("endTime",endTime);
                intent.putExtra("scheduleDate",date);
                startActivity(intent);
            }
        });
    }
}