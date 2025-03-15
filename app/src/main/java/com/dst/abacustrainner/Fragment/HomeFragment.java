package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Activity.PlayWithNumbersActivity;
import com.dst.abacustrainner.Activity.ViewDetailsActivity;
import com.dst.abacustrainner.Activity.VisualiztionActivity;
import com.dst.abacustrainner.Adapter.BatchDetailsAdapter;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.StudentDetails;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {
    Button butViewDetrails;
    TextView txtSchedule,txtName,txtTime,txtTime1,txtData,txtTimeText;

    ImageView imgCalender;
    String currentDate,textWithBrackets;
    private Calendar calendar;
    String id="",dateId ="",name="",time="",time1="",date="",firsstname="",startedOn="";
    LinearLayout layoutData,layoutPlayWithNumbers,layoutvisualization;
    RecyclerView recyclerViewBatches;

    BatchDetailsAdapter batchDetailsAdapter;
    ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);

        butViewDetrails=view.findViewById(R.id.but_view);
        txtSchedule=view.findViewById(R.id.txt_schedule_date);
        imgCalender=view.findViewById(R.id.image_calender);
        txtName=view.findViewById(R.id.txt_name);
        txtTime=view.findViewById(R.id.txt_time);
        txtTime1=view.findViewById(R.id.txtdate);
        txtData=view.findViewById(R.id.txt_data);
        txtTimeText=view.findViewById(R.id.time_txt);

        layoutData=view.findViewById(R.id.layout_data);
        progressBar= view.findViewById(R.id.progress);
        layoutPlayWithNumbers=view.findViewById(R.id.layout_play_numbers);
        layoutvisualization=view.findViewById(R.id.layout_visualization);
        calendar = Calendar.getInstance();
        updateDateText();
        //get the user id from sharedpref manager
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(getContext().getApplicationContext()).getUserData();
        id=result.getStudentId();
        firsstname=" Hello " +  result.getFirstName() + "";

        recyclerViewBatches = view.findViewById(R.id.recycler_baches);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerViewBatches.setLayoutManager(layoutManager);
        VerifyMethod(id,currentDate);
        VerifyBatchDetails(id);


       imgCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
        butViewDetrails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewDetailsActivity.class);
                intent.putExtra("dateId", dateId);
                intent.putExtra("studentId",id);
                intent.putExtra("batchName",name);
                intent.putExtra("startTime",time);
                intent.putExtra("endTime",time1);
                intent.putExtra("scheduleDate",date);
                startActivity(intent);
            }
        });


        layoutvisualization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), VisualiztionActivity.class);
                intent.putExtra("studentId",id);
                intent.putExtra("firstName",firsstname);
                startActivity(intent);
            }
        });



        return view;
    }

    private void VerifyBatchDetails(String id) {

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
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), id);
        Call<BachDetailsResponse> call=apiClient.batchData(idPart);
        call.enqueue(new Callback<BachDetailsResponse>() {
            @Override
            public void onResponse(Call<BachDetailsResponse> call, Response<BachDetailsResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    BachDetailsResponse bachDetailsResponse=response.body();
                    if (bachDetailsResponse.getErrorCode().equals("202")){
                        Toast.makeText(getContext(), "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    }else if (bachDetailsResponse.getErrorCode().equals("200")){

                        List<BachDetailsResponse.Result> results=bachDetailsResponse.getResult();

                        batchDetailsAdapter =new BatchDetailsAdapter(getActivity(),results);
                        recyclerViewBatches.setAdapter(batchDetailsAdapter);

                    }else {
                        Toast.makeText(getContext(), "Data Error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BachDetailsResponse> call, Throwable t) {

            }
        });
    }

    private void VerifyMethod(String id, String txtSchedule) {

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
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), txtSchedule);

        Call<StudentDetails> call=apiClient.detailsPost(idPart,datePart);
        call.enqueue(new Callback<StudentDetails>() {
            @Override
            public void onResponse(Call<StudentDetails> call, Response<StudentDetails> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    StudentDetails details=response.body();
                    handleApiResult(details);
                }else {
                    Toast.makeText(getContext(),"Data Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StudentDetails> call, Throwable t) {

            }
        });
    }
    private void handleApiResult(StudentDetails details) {
        if (details.getErrorCode().equals("202")) {
            txtName.setText("No Data Found");
            txtTimeText.setVisibility(View.GONE);
            txtTime.setVisibility(View.GONE);
            txtTime1.setVisibility(View.GONE);
            butViewDetrails.setVisibility(View.GONE);
        } else if (details.getErrorCode().equals("200")) {
            List<StudentDetails.Result> list = details.getResult();
            if (!list.isEmpty()) {
                StudentDetails.Result result = list.get(0);
                dateId = result.getDateId();
                name = "Batch Name : " + result.getBatchName() + "";
                time1 = result.getEndTime();
                time = result.getStartTime();
                date=result.getScheduleDate();
                txtTimeText.setVisibility(View.VISIBLE);
                txtTime.setVisibility(View.VISIBLE);
                txtTime1.setVisibility(View.VISIBLE);
                butViewDetrails.setVisibility(View.VISIBLE);
                txtName.setText(name);
                txtTime.setText(time);
                txtTime1.setText(time1);
            }
        }
    }
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            calendar.set(year, month, day);
            updateDateText();
        }
    };
    private void updateDateText() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());
        currentDate = dateFormat.format(calendar.getTime());
        textWithBrackets = "(" + currentDate + ")";
        txtSchedule.setText(textWithBrackets);
        progressBar.setVisibility(View.VISIBLE);
        VerifyMethod(id,currentDate);
    }
}
