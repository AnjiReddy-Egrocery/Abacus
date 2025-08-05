package com.dst.abacustrainner.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Activity.AllSchedulesActivity;
import com.dst.abacustrainner.Activity.PlayWithNumbersActivity;
import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Activity.PurchasedVideoTutorialsActivity;
import com.dst.abacustrainner.Activity.ViewDetailsActivity;
import com.dst.abacustrainner.Activity.VisualiztionActivity;
import com.dst.abacustrainner.Adapter.BatchDetailsAdapter;
import com.dst.abacustrainner.Model.BachDetailsResponse;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.StudentDetails;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    TextView txtName,   txtPurchases, txtTime,txtTime1,txtClckSchedule,txtNextSchedule,txtNextTime,txtCompleted,txtRemaining;
    ImageView imageCalender;
    String currentDate,textWithBrackets;
    private Calendar calendar;
    private String studentId, batchId;
    String id="",dateId ="",name="",time="",time1="",date="",firsstname="",startedOn="";
    LinearLayout layoutData,layoutPlayWithNumbers,layoutvisualization;
    BatchDetailsAdapter batchDetailsAdapter;
    ProgressBar progressBar;
    String startTime,endTime,timeText;
    private Map<String, String> scheduledDatesMap = new HashMap<>();
    Map<String, String> dateIdMap = new HashMap<>();
    LinearLayout layoutSchedule, layoutScheduleInfo,layoutPurchasedSection,layoutVideoTutorials ;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_home,container,false);

        txtClckSchedule=view.findViewById(R.id.txt_clcik_schedule);
        txtName=view.findViewById(R.id.txt_name);
        txtTime=view.findViewById(R.id.txt_time);
        txtTime1=view.findViewById(R.id.txtdate);
        txtNextSchedule = view.findViewById(R.id.txt_nxtSchedule);
        txtNextTime = view.findViewById(R.id.txt_nextTime);
        txtCompleted = view.findViewById(R.id.txt_Completed);
        txtRemaining = view.findViewById(R.id.txt_upComing);
        imageCalender = view.findViewById(R.id.image_calender);





        layoutSchedule = view.findViewById(R.id.layou_schedule);
        layoutScheduleInfo = view.findViewById(R.id.layout_schedule_information);
        layoutPurchasedSection = view.findViewById(R.id.layout_purchased_section);
        layoutVideoTutorials = view.findViewById(R.id.layout_video_tutorials);

        layoutData=view.findViewById(R.id.layout_data);
        progressBar= view.findViewById(R.id.progress);
        //layoutPlayWithNumbers=view.findViewById(R.id.layout_play_numbers);
        //layoutvisualization=view.findViewById(R.id.layout_visualization);
        calendar = Calendar.getInstance();
        updateDateText();
        //get the user id from sharedpref manager
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(getContext().getApplicationContext()).getUserData();
        id=result.getStudentId();

         Log.e("Reddy","StudentId" + id);
        firsstname=" Hello " +  result.getFirstName() + "";
        imageCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScheduleFragment();
            }
        });
        Bundle args = getArguments();
        if (args != null) {
            String studentId = args.getString("studentId");
            String batchId = args.getString("batchId");

            if (studentId != null && batchId != null) {
                ScheduledateMethod(studentId, batchId);
            }
        }


        VerifyMethod(id,currentDate);
        VerifyBatchDetails(id);

        if (getArguments() != null && getArguments().getString("studentId") != null) {
            studentId = getArguments().getString("studentId");
        } else {
            studentId = id; // fallback to SharedPref ID
        }

        txtClckSchedule.setText(Html.fromHtml("<u>Click Here</u>"));
        txtClckSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (studentId != null && !studentId.isEmpty()) {
                    scheduleMethod(studentId);
                } else {
                    Toast.makeText(getContext(), "Student ID is missing", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*layoutPlayWithNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), PlayWithNumbersActivity.class);
                intent.putExtra("studentId",id);
                intent.putExtra("firstName",firsstname);
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
        });*/

        SharedPreferences prefs = requireContext().getSharedPreferences("purchases", Context.MODE_PRIVATE);

        boolean videoPurchased = prefs.getBoolean("video_purchased", false);
        boolean livePurchased = prefs.getBoolean("live_purchased", false);

        if (videoPurchased) {
            layoutVideoTutorials.setVisibility(View.VISIBLE);
        }

        if (livePurchased) {
            layoutPurchasedSection.setVisibility(View.VISIBLE);
        }


        layoutPurchasedSection.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), PurchasedActivity.class);
            intent.putExtra("cartType", "live");
            startActivity(intent);
        });

        layoutVideoTutorials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), PurchasedVideoTutorialsActivity.class);
                intent.putExtra("cartType", "video");
                startActivity(intent);
            }
        });

        layoutSchedule.setVisibility(View.GONE);
        layoutScheduleInfo.setVisibility(View.GONE);

        return view;
    }
    private void openScheduleFragment() {
        SchedulesFragment scheduleFragment = new SchedulesFragment();
        Bundle args = new Bundle();
        args.putString("studentId", id);
        args.putString("batchId",batchId);// pass studentId if needed
        scheduleFragment.setArguments(args);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, scheduleFragment); // Make sure R.id.fragment_container is the correct container in your activity layout
        transaction.addToBackStack(null);  // So you can navigate back
        transaction.commit();
    }

    private void scheduleMethod(String studentId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient=retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        Call<BachDetailsResponse> call=apiClient.batchData(idPart);
        call.enqueue(new Callback<BachDetailsResponse>() {
            @Override
            public void onResponse(Call<BachDetailsResponse> call, Response<BachDetailsResponse> response) {

                if (response.isSuccessful()){
                    BachDetailsResponse bachDetailsResponse=response.body();
                    if (bachDetailsResponse.getErrorCode().equals("202")){
                        Toast.makeText(getContext(), "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    }else if (bachDetailsResponse.getErrorCode().equals("200")){

                        List<BachDetailsResponse.Result> results=bachDetailsResponse.getResult();

                        if (!results.isEmpty()) {

                            batchId = results.get(0).getBatchId();
                            Log.d("Reddy", "StudentId" + studentId);
                            Log.d("Reddy", "BatchId" + batchId);
                            Log.d("Reddy", "DateId" + dateId);

                            Intent intent = new Intent(getContext(), AllSchedulesActivity.class);
                            intent.putExtra("studentId",studentId);
                            intent.putExtra("batchId",batchId);
                            intent.putExtra("DateId",dateId);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(getContext(), "Data Error", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BachDetailsResponse> call, Throwable t) {

            }
        });
    }


    private void ScheduledateMethod(String studentId, String batchId) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody batchIdPart = RequestBody.create(MediaType.parse("text/plain"), batchId);

        Call<DatedetailsResponse> call = apiClient.batchDateData(idPart, batchIdPart);
        call.enqueue(new Callback<DatedetailsResponse>() {
            @Override
            public void onResponse(Call<DatedetailsResponse> call, Response<DatedetailsResponse> response) {
                if (response.body() != null) {
                    DatedetailsResponse details = response.body();

                    if (details.getErrorCode().equals("202")) {
                        Toast.makeText(getContext(), "No Schedule for the given details", Toast.LENGTH_LONG).show();
                    } else if (details.getErrorCode().equals("200")) {
                        List<DatedetailsResponse.Result> daResult = details.getResult();
                        if (daResult != null && !daResult.isEmpty()) {
                            DatedetailsResponse.Result batchDetails = daResult.get(0);

                            startTime = batchDetails.getStartTime();
                            endTime = batchDetails.getEndTime();
                            timeText = startTime + " - " + endTime; // e.g., "4:00 PM - 5:00 PM"
                        } else {
                            txtTime.setText("No data available");
                        }

                        scheduledDatesMap.clear();
                        dateIdMap.clear();

                        int classNumber = 1;
                        int completedSchedules = 0;
                        int totalSchedules = 0;
                        int remainingSchedules = 0;
                        for (DatedetailsResponse.Result result : daResult) {
                            if (result.getDates() != null) {
                                List<DatedetailsResponse.Result.Date> datesList = result.getDates();

                                SimpleDateFormat inputFormat = new SimpleDateFormat("dd - MMMM - yyyy", Locale.ENGLISH);
                                SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                Date today = new Date();

                                boolean nextScheduleFound = false;

                                for (DatedetailsResponse.Result.Date dateObj : datesList) {
                                    String scheduleDateStr = dateObj.getScheduleDate().trim();
                                    dateId = dateObj.getDateId();

                                    try {
                                        Date scheduleDate = inputFormat.parse(scheduleDateStr);
                                        totalSchedules++;

                                        if (scheduleDate.before(today)) {
                                            completedSchedules++;
                                        } else {
                                            remainingSchedules++;
                                        }

                                        String formattedDate = outputFormat.format(scheduleDate);
                                        scheduledDatesMap.put(formattedDate, "Class - " + classNumber);
                                        dateIdMap.put(formattedDate, dateId);
                                        classNumber++;

                                        if (!nextScheduleFound && scheduleDate.after(today)) {
                                            String displayDate = displayFormat.format(scheduleDate);
                                            String fullSchedule = displayDate + ", [" + timeText + "]";
                                            txtNextSchedule.setText(fullSchedule);
                                            txtNextTime.setText(timeText);
                                            nextScheduleFound = true;
                                        }

                                    } catch (ParseException e) {
                                        Log.e("ScheduleDebug", "Date Parsing Error: " + scheduleDateStr, e);
                                    }
                                }

                                if (!nextScheduleFound) {
                                    txtNextSchedule.setText("No upcoming schedule found");
                                    txtNextTime.setText(""); // clear time
                                }
                            }
                        }

                        // ✅ Now show the totals (initialize these TextViews at the top)

                        txtCompleted.setText("Completed: " + completedSchedules);
                        txtRemaining.setText("Remaining: " + remainingSchedules);
                        if (completedSchedules == 0 && remainingSchedules == 0) {
                            layoutSchedule.setVisibility(View.GONE);
                            layoutScheduleInfo.setVisibility(View.GONE);
                        } else {
                            layoutSchedule.setVisibility(View.VISIBLE);
                            layoutScheduleInfo.setVisibility(View.VISIBLE);
                        }
                            }
                        }
                    }



            @Override
            public void onFailure(Call<DatedetailsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load schedules", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void VerifyBatchDetails(String id) {
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
//                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()){
                    BachDetailsResponse bachDetailsResponse=response.body();
                    if (bachDetailsResponse.getErrorCode().equals("202")){
                        Toast.makeText(getContext(), "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                    }else if (bachDetailsResponse.getErrorCode().equals("200")){

                        List<BachDetailsResponse.Result> results=bachDetailsResponse.getResult();

                        batchDetailsAdapter =new BatchDetailsAdapter(getActivity(),results);

                        if (!results.isEmpty()) {
                            batchId = results.get(0).getBatchId();
                            ScheduledateMethod(id, batchId); // ✅ Call schedule loading here
                        }
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
        Log.e("Reddy","StudentId" + id);
        Log.e("Reddy","Date" + txtSchedule);
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
//                progressBar.setVisibility(View.GONE);
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

        } else if (details.getErrorCode().equals("200")) {
            List<StudentDetails.Result> list = details.getResult();
            if (!list.isEmpty()) {
                StudentDetails.Result result = list.get(0);
                dateId = result.getDateId();
                name = "Batch Name : " + result.getBatchName() + "";
                time1 = result.getEndTime();
                time = result.getStartTime();
                date=result.getScheduleDate();
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
        VerifyMethod(id,currentDate);
    }


}
