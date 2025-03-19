package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.dst.abacustrainner.Adapter.TableAdapter;
import com.dst.abacustrainner.Fragment.SchedulesFragment;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.TableRowModel;
import com.dst.abacustrainner.Model.TopicListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

public class AllSchedulesActivity extends AppCompatActivity {


    private LinearLayout btnBack;

    String studentId, batchId;
    TableLayout tableLayout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedules);


        btnBack = findViewById(R.id.btn_back_to_home);
        tableLayout = findViewById(R.id.tableLayout);

        Bundle bundle = getIntent().getExtras();
        batchId = bundle.getString("batchId");
        studentId = bundle.getString("studentId");

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of SchedulesFragment
                SchedulesFragment schedulesFragment = new SchedulesFragment();

                // Get the FragmentManager and begin a transaction
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // Replace the current fragment with SchedulesFragment
                fragmentTransaction.replace(R.id.btn_back_to_home, schedulesFragment);
                fragmentTransaction.addToBackStack(null); // Allows going back to the previous fragment

                // Commit the transaction
                fragmentTransaction.commit();
            }
        });

        AllScheduleMethod(studentId, batchId);

    }

    private void AllScheduleMethod(String studentId, String batchId) {
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
                        Toast.makeText(AllSchedulesActivity.this, "No Schedule for the given details", Toast.LENGTH_LONG).show();
                    } else if (details.getErrorCode().equals("200")) {
                        List<DatedetailsResponse.Result> result = details.getResult();

                        tableLayout.removeAllViews(); // Clear previous rows

                        for (int i = 0; i < result.size(); i++) {
                            DatedetailsResponse.Result item = result.get(i);
                            String startTime = item.getStartTime();
                            String endTime = item.getEndTime();
                            String timeText = startTime + " - " + endTime;

                            List<DatedetailsResponse.Result.Date> datesList = item.getDates();
                            Collections.reverse(datesList);

                            for (int j = 0; j < datesList.size(); j++) {
                                DatedetailsResponse.Result.Date date = datesList.get(j);

                                // **Create Parent Layout (Vertical)**
                                LinearLayout parentLayout = new LinearLayout(AllSchedulesActivity.this);
                                parentLayout.setOrientation(LinearLayout.VERTICAL);
                                parentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                // **Schedule Row (Horizontal)**
                                LinearLayout scheduleRow = new LinearLayout(AllSchedulesActivity.this);
                                scheduleRow.setOrientation(LinearLayout.HORIZONTAL);
                                scheduleRow.setBackgroundColor(Color.parseColor("#EEEEEE"));
                                scheduleRow.setPadding(10, 10, 10, 10);
                                scheduleRow.setElevation(2);
                                scheduleRow.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                // **Date TextView**
                                TextView txtDate = new TextView(AllSchedulesActivity.this);
                                txtDate.setText(date.getScheduleDate());
                                txtDate.setTextSize(14);
                                txtDate.setTypeface(Typeface.DEFAULT_BOLD);
                                txtDate.setGravity(Gravity.CENTER);
                                txtDate.setPadding(4, 4, 4, 4);
                                txtDate.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                                ));

                                // **Time TextView**
                                TextView txtTime = new TextView(AllSchedulesActivity.this);
                                txtTime.setText(timeText);
                                txtTime.setTextSize(14);
                                txtTime.setTypeface(Typeface.DEFAULT_BOLD);
                                txtTime.setGravity(Gravity.CENTER);
                                txtTime.setPadding(4, 4, 4, 4);
                                txtTime.setLayoutParams(new LinearLayout.LayoutParams(
                                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1
                                ));

                                // **Status Button**
                                Button btnStatus = new Button(AllSchedulesActivity.this);
                                btnStatus.setText(j == 0 ? "Join Now" : "Up Coming");
                                btnStatus.setTextSize(12);
                                btnStatus.setTypeface(Typeface.DEFAULT_BOLD);
                                btnStatus.setTextColor(Color.WHITE);
                                btnStatus.setPadding(10, 6, 10, 6);
                                btnStatus.setBackgroundResource(R.drawable.rounded_button); // **Set Rounded Background**
                                btnStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF6600"))); // Orange Button
                                btnStatus.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                // **Expandable Details Section (Initially Hidden)**
                                LinearLayout detailsLayout = new LinearLayout(AllSchedulesActivity.this);
                                detailsLayout.setOrientation(LinearLayout.VERTICAL);
                                detailsLayout.setBackgroundColor(Color.parseColor("#DDDDDD"));
                                detailsLayout.setPadding(10, 10, 10, 10);
                                detailsLayout.setVisibility(View.GONE);
                                detailsLayout.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                // **Topic Details**
                                TextView txtTopic = new TextView(AllSchedulesActivity.this);
                                //txtTopic.setText("Topic: " + item.getTopic());
                                txtTopic.setTextSize(14);
                                txtTopic.setTextColor(Color.parseColor("#333333"));
                                txtTopic.setPadding(4, 4, 4, 4);

                                // **Assignment Details**
                                TextView txtAssignment = new TextView(AllSchedulesActivity.this);
                               // txtAssignment.setText("Assignment: " + item.getAssignment());
                                txtAssignment.setTextSize(14);
                                txtAssignment.setTextColor(Color.parseColor("#333333"));
                                txtAssignment.setPadding(4, 4, 4, 4);

                                // **Add Views to Details Layout**
                                detailsLayout.addView(txtTopic);
                                detailsLayout.addView(txtAssignment);

                                // **Add Views to Schedule Row**
                                scheduleRow.addView(txtDate);
                                scheduleRow.addView(txtTime);
                                scheduleRow.addView(btnStatus);

                                // **Add Click Event for Expanding**
                                scheduleRow.setOnClickListener(v -> {
                                    if (detailsLayout.getVisibility() == View.GONE) {
                                        detailsLayout.setVisibility(View.VISIBLE);
                                    } else {
                                        detailsLayout.setVisibility(View.GONE);
                                    }
                                });

                                // **Add Row and Details to Parent Layout**
                                parentLayout.addView(scheduleRow);
                                parentLayout.addView(detailsLayout);

                                // **Add Parent Layout to Main Layout**
                                tableLayout.addView(parentLayout);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DatedetailsResponse> call, Throwable t) {
                Toast.makeText(AllSchedulesActivity.this, "Failed to load schedules", Toast.LENGTH_SHORT).show();
            }
        });
    }

}