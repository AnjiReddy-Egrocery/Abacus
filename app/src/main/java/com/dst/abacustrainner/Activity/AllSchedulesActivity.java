package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.TopicListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.Services.AssignmentCallback;
import com.dst.abacustrainner.Services.TopicsCallback;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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

    String studentId, batchId ,dateId;
    TableLayout tableLayout;
    private LinearLayout currentlyOpenLayout = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_schedules);


        btnBack = findViewById(R.id.fragment_container);
        tableLayout = findViewById(R.id.tableLayout);

        Bundle bundle = getIntent().getExtras();
        batchId = bundle.getString("batchId");
        studentId = bundle.getString("studentId");
        dateId = bundle.getString("DateId");
        Log.d("Reddy",""+dateId);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of SchedulesFragment
                finish();
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
                        TableRow headerRow = new TableRow(AllSchedulesActivity.this);
                        headerRow.setBackgroundColor(Color.LTGRAY);
                        headerRow.setLayoutParams(new TableLayout.LayoutParams(
                                TableLayout.LayoutParams.MATCH_PARENT,
                                TableLayout.LayoutParams.WRAP_CONTENT
                        ));

                        // **Header - Date Layout**
                        LinearLayout headerDateLayout = new LinearLayout(getApplicationContext());
                        headerDateLayout.setOrientation(LinearLayout.VERTICAL);
                        headerDateLayout.setGravity(Gravity.CENTER);
                        headerDateLayout.setPadding(16, 20, 16, 20);
                        headerDateLayout.setBackgroundResource(R.drawable.border);
                        headerDateLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                        TextView txtHeaderDate = new TextView(getApplicationContext());
                        txtHeaderDate.setText("Date");
                        txtHeaderDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        txtHeaderDate.setTextColor(Color.BLACK);
                        txtHeaderDate.setGravity(Gravity.CENTER);

                        headerDateLayout.addView(txtHeaderDate);
                        headerRow.addView(headerDateLayout);

                        // **Header - Time Layout**
                        LinearLayout headerTimeLayout = new LinearLayout(getApplicationContext());
                        headerTimeLayout.setOrientation(LinearLayout.VERTICAL);
                        headerTimeLayout.setGravity(Gravity.CENTER);
                        headerTimeLayout.setPadding(16, 20, 16, 20);
                        headerTimeLayout.setBackgroundResource(R.drawable.border);
                        headerTimeLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                        TextView txtHeaderTime = new TextView(getApplicationContext());
                        txtHeaderTime.setText("Time");
                        txtHeaderTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        txtHeaderTime.setTextColor(Color.BLACK);
                        txtHeaderTime.setGravity(Gravity.CENTER);

                        headerTimeLayout.addView(txtHeaderTime);
                        headerRow.addView(headerTimeLayout);

                        // **Header - Conducted Layout**
                        LinearLayout headerStatusLayout = new LinearLayout(getApplicationContext());
                        headerStatusLayout.setOrientation(LinearLayout.VERTICAL);
                        headerStatusLayout.setGravity(Gravity.CENTER);
                        headerStatusLayout.setPadding(16, 20, 16, 20);
                        headerStatusLayout.setBackgroundResource(R.drawable.border);
                        headerStatusLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));

                        TextView txtHeaderStatus = new TextView(getApplicationContext());
                        txtHeaderStatus.setText("Conducted");
                        txtHeaderStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        txtHeaderStatus.setTextColor(Color.BLACK);
                        txtHeaderStatus.setGravity(Gravity.CENTER);

                        headerStatusLayout.addView(txtHeaderStatus);
                        headerRow.addView(headerStatusLayout);

                        // Add Header Row to TableLayout
                        tableLayout.addView(headerRow);


                        for (int i = 0; i < result.size(); i++) {
                            DatedetailsResponse.Result item = result.get(i);
                            String startTime = item.getStartTime(); // Example: "06:00 AM"
                            String endTime = item.getEndTime();     // Example: "07:00 AM"

                            // Extracting only the hour part
                            String startHour = startTime.split(":")[0]; // "06"
                            String endHour = endTime.split(":")[0];     // "07"

                            // Getting AM/PM from endTime
                            String amPm = endTime.substring(endTime.length() - 2); // "AM" or "PM"

                            // Formatting the time range
                            String timeText = startHour + " - " + endHour + " " + amPm; // "06 - 07 AM"

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
                                scheduleRow.setPadding(0, 0, 0, 0);
                                scheduleRow.setElevation(2);
                                scheduleRow.setLayoutParams(new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                ));

                                LinearLayout txtDateLayout = new LinearLayout(getApplicationContext());
                                txtDateLayout.setOrientation(LinearLayout.VERTICAL);
                                txtDateLayout.setGravity(Gravity.CENTER);
                                txtDateLayout.setPadding(16, 20, 16, 20);
                                txtDateLayout.setBackgroundResource(R.drawable.border);
                                txtDateLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1


                                // **Date TextView**
                                TextView txtDate = new TextView(AllSchedulesActivity.this);

                                // Convert and set formatted date
                                String formattedDate = formatDate(date.getScheduleDate());
                                txtDate.setText(formattedDate);
                                txtDate.setPadding(16, 20, 16, 20);
                                txtDate.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                txtDate.setTextColor(Color.BLACK);
                                txtDate.setGravity(Gravity.CENTER);

                                View spacetxtnumber = new View(getApplicationContext());
                                spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, 0));

                                txtDateLayout.addView(txtDate);
                                txtDateLayout.addView(spacetxtnumber);

                                LinearLayout txtTimeLayout = new LinearLayout(getApplicationContext());
                                txtTimeLayout.setOrientation(LinearLayout.VERTICAL);
                                txtTimeLayout.setGravity(Gravity.CENTER);
                                txtTimeLayout.setPadding(16, 20, 16, 20);
                                txtTimeLayout.setBackgroundResource(R.drawable.border);
                                txtTimeLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1

                                // **Time TextView**
                                TextView txtTime = new TextView(AllSchedulesActivity.this);
                                txtTime.setText(timeText);
                                txtTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                txtTime.setTextColor(Color.BLACK);
                                txtTime.setGravity(Gravity.CENTER);
                                txtTime.setPadding(16, 20, 16, 20);

                                View spacetxtTime = new View(getApplicationContext());
                                spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, 0));

                                txtTimeLayout.addView(txtTime);
                                txtTimeLayout.addView(spacetxtTime);

                                LinearLayout txtStatusLayout = new LinearLayout(getApplicationContext());
                                txtStatusLayout.setOrientation(LinearLayout.VERTICAL);
                                txtStatusLayout.setGravity(Gravity.CENTER);
                                txtStatusLayout.setPadding(16, 20, 16, 20);
                                txtStatusLayout.setBackgroundResource(R.drawable.border);
                                txtStatusLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1)); // Weight 1

                                // **Status Button**
                                TextView txtStatus = new TextView(getApplicationContext());
                                txtStatus.setText(j == 0 ? "Join Now" : "Up Coming");
                                txtStatus.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                txtStatus.setPadding(16, 20, 16, 20);
                                txtStatus.setGravity(Gravity.CENTER);
                                txtStatus.setBackgroundResource(R.drawable.rounded_button); // Correct
                                txtStatus.setTextColor(Color.WHITE);
                                txtStatus.setTypeface(null, Typeface.BOLD);


                                View spacetxtStatus = new View(getApplicationContext());
                                spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT, 0));


                                txtStatusLayout.addView(txtStatus);
                                txtStatusLayout.addView(spacetxtStatus);

                                // **Add Views to Schedule Row**
                                scheduleRow.addView(txtDateLayout);
                                scheduleRow.addView(txtTimeLayout);
                                scheduleRow.addView(txtStatusLayout);


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
                                txtTopic.setText("Topic: " + "");
                                txtTopic.setTextSize(14);
                                txtTopic.setTextColor(Color.parseColor("#333333"));
                                txtTopic.setPadding(4, 4, 4, 4);

                                // **Assignment Details**
                                TextView txtAssignment = new TextView(AllSchedulesActivity.this);
                                txtAssignment.setText("Assignment: " + "Raghu");
                                txtAssignment.setTextSize(14);
                                txtAssignment.setTextColor(Color.parseColor("#333333"));
                                txtAssignment.setPadding(4, 4, 4, 4);

                                // **Add Views to Details Layout**
                                detailsLayout.addView(txtTopic);
                                detailsLayout.addView(txtAssignment);


                                // **Add Click Event for Expanding**
                                txtStatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (txtStatusLayout.getChildCount() > 1) {
                                            // Assuming index 0 is your TableLayout, index 1 will be dynamic loadingText
                                            txtStatusLayout.removeViewAt(1);
                                        }

                                        // Step 1: Create Loading TextView
                                        TextView loadingText = new TextView(v.getContext());
                                        loadingText.setText("Data Loading...");
                                        loadingText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                        loadingText.setTextColor(Color.GRAY);
                                        loadingText.setPadding(0, 10, 0, 0);

                                        // Step 2: Add loadingText to Accordion (txtStatusLayout)
                                        txtStatusLayout.addView(loadingText);
/*

                                        TextView loadingText = new TextView(getApplicationContext());
                                        loadingText.setText("Data Loading...");
                                        loadingText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                        loadingText.setTextColor(Color.GRAY);
                                        loadingText.setPadding(0, 10, 0, 0);

                                        txtStatusLayout.addView(loadingText);
*/


                                        // Close previously opened detailsLayout (if any)
                                        if (currentlyOpenLayout != null && currentlyOpenLayout != detailsLayout) {
                                            currentlyOpenLayout.setVisibility(View.GONE);
                                        }

                                        // Set default loading text while fetching topics
                                        txtTopic.setText("Topic: Loading...");
                                        detailsLayout.removeAllViews(); // Clear previous topic buttons

                                        // Fetch Topics Dynamically
                                        TopicsMethod(studentId, date.getDateId(), new TopicsCallback() {
                                            @Override
                                            public void onTopicsReceived(List<TopicListResponse.Result.Topics> topicsList) {

                                               // txtStatuLayout.removeView(loadingText);
                                                Log.d("API_RESPONSE", "Topics List Size: " + topicsList.size()); // Debugging

                                                if (!topicsList.isEmpty()) {

                                                    TextView txtTopicsTitle = new TextView(getApplicationContext());
                                                    txtTopicsTitle.setText("Topics");
                                                    txtTopicsTitle.setTextColor(Color.parseColor("#4A148C"));
                                                    txtTopicsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                    txtTopicsTitle.setTypeface(null, Typeface.BOLD);

                                                    // Add title to detailsLayout
                                                    detailsLayout.addView(txtTopicsTitle);

                                                    TableRow headerRow = new TableRow(getApplicationContext());

                                                    TextView headerNo = new TextView(getApplicationContext());
                                                    headerNo.setText("No");
                                                    headerNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerNo.setTypeface(null, Typeface.BOLD);
                                                    headerNo.setGravity(Gravity.CENTER);
                                                    headerNo.setPadding(16, 20, 16, 20);
                                                    headerNo.setBackgroundResource(R.drawable.border);

                                                    TextView headerTopic = new TextView(getApplicationContext());
                                                    headerTopic.setText("Topic");
                                                    headerTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerTopic.setTypeface(null, Typeface.BOLD);
                                                    headerTopic.setGravity(Gravity.CENTER);
                                                    headerTopic.setPadding(16, 20, 16, 20);
                                                    headerTopic.setBackgroundResource(R.drawable.border);

                                                    TextView headerActions = new TextView(getApplicationContext());
                                                    headerActions.setText("Conducted");
                                                    headerActions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerActions.setTypeface(null, Typeface.BOLD);
                                                    headerActions.setGravity(Gravity.CENTER);
                                                    headerActions.setPadding(16, 20, 16, 20);
                                                    headerActions.setBackgroundResource(R.drawable.border);

                                                    // Set LayoutParams to ensure equal width
                                                    TableRow.LayoutParams headerParamsNo = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                                                    TableRow.LayoutParams headerParamsTopic = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
                                                    TableRow.LayoutParams headerParamsActions = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);

                                                    headerNo.setLayoutParams(headerParamsNo);
                                                    headerTopic.setLayoutParams(headerParamsTopic);
                                                    headerActions.setLayoutParams(headerParamsActions);

                                                    // Add headers to the row
                                                    headerRow.addView(headerNo);
                                                    headerRow.addView(headerTopic);
                                                    headerRow.addView(headerActions);

                                                    // Add header row to detailsLayout
                                                    detailsLayout.addView(headerRow);

                                                    for (int i = 0; i < topicsList.size(); i++) {

                                                        TopicListResponse.Result.Topics topic = topicsList.get(i);

                                                        TableRow row = new TableRow(getApplicationContext());

                                                        LinearLayout txtNumberLayout = new LinearLayout(getApplicationContext());
                                                        txtNumberLayout.setOrientation(LinearLayout.VERTICAL);
                                                        txtNumberLayout.setGravity(Gravity.CENTER);
                                                        txtNumberLayout.setPadding(16, 76, 16, 76);
                                                        txtNumberLayout.setBackgroundResource(R.drawable.border);

                                                        // Number Column
                                                        TextView txtNumber = new TextView(getApplicationContext());
                                                        txtNumber.setText(String.valueOf(i + 1));
                                                        txtNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        txtNumber.setPadding(16, 10, 16, 10);
                                                        txtNumber.setTextColor(Color.BLACK);
                                                        txtNumber.setGravity(Gravity.CENTER);

                                                        View spacetxtnumber = new View(getApplicationContext());
                                                        spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, 0));

                                                        txtNumberLayout.addView(txtNumber);
                                                        txtNumberLayout.addView(spacetxtnumber);

                                                        LinearLayout topicLayout = new LinearLayout(getApplicationContext());
                                                        topicLayout.setOrientation(LinearLayout.VERTICAL);
                                                        topicLayout.setGravity(Gravity.CENTER);
                                                        topicLayout.setBackgroundResource(R.drawable.border);

// Topic Column
                                                        TextView txtTopic = new TextView(getApplicationContext());
                                                        txtTopic.setText(topic.getTopicName());
                                                        txtTopic.setMaxLines(2);
                                                        txtTopic.setEllipsize(TextUtils.TruncateAt.END);
                                                        txtTopic.setGravity(Gravity.CENTER); // Align text to start
                                                        txtTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                                                        txtTopic.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                                        txtTopic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                            @Override
                                                            public void onGlobalLayout() {
                                                                // Remove the listener after execution to avoid multiple calls
                                                                txtTopic.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                                                if (txtTopic.getLineCount() == 1) {
                                                                    topicLayout.setPadding(16, 86, 10, 86); // Apply for single-line text
                                                                } else {
                                                                    topicLayout.setPadding(16, 68 ,16, 68); // Apply for two-line text
                                                                }
                                                            }
                                                        });

                                                        View spacetopic = new View(getApplicationContext());
                                                        spacetopic.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, 0));

                                                        topicLayout.addView(txtTopic);
                                                        topicLayout.addView(spacetopic);




                                                        // Practice Now Button
                                                        LinearLayout practiceLayout = new LinearLayout(getApplicationContext());
                                                        practiceLayout.setOrientation(LinearLayout.VERTICAL);
                                                        practiceLayout.setGravity(Gravity.CENTER);
                                                        practiceLayout.setPadding(20, 20, 16, 20);
                                                        practiceLayout.setBackgroundResource(R.drawable.border);

                                                        TextView txtPracticeNow = new TextView(getApplicationContext());
                                                        txtPracticeNow.setText("Practice Now");
                                                        txtPracticeNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        txtPracticeNow.setPadding(16, 20, 16, 20);
                                                        txtPracticeNow.setGravity(Gravity.CENTER);

                                                        txtPracticeNow.setBackgroundResource(R.drawable.practicenow_drawable); // Correct
                                                         txtPracticeNow.setTextColor(Color.WHITE);
                                                        txtPracticeNow.setTypeface(null, Typeface.BOLD);
                                                        txtPracticeNow.setOnClickListener(v -> {
                                                            Intent intent = new Intent(getApplicationContext(), TopicPracticeActivity.class);
                                                            intent.putExtra("topicId", topic.getTopicId());
                                                            intent.putExtra("studentId", studentId);
                                                            intent.putExtra("topicName", topic.getTopicName());
                                                            startActivity(intent);
                                                        });

                                                        // Space between buttons
                                                        View space = new View(getApplicationContext());
                                                        space.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, 10));

                                                        // View Result Button
                                                        TextView txtViewResult = new TextView(getApplicationContext());
                                                        txtViewResult.setText("View Result");
                                                        txtViewResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        txtViewResult.setPadding(16, 20, 16, 20);
                                                        txtViewResult.setGravity(Gravity.CENTER);
                                                        txtViewResult.setBackgroundResource(R.drawable.button_viewresult); // Correctly applied to txtViewResult
                                                        txtViewResult.setTextColor(Color.parseColor("#000000"));
                                                        txtViewResult.setTypeface(null, Typeface.BOLD);
                                                        txtViewResult.setOnClickListener(v -> {
                                                            Intent intent = new Intent(getApplicationContext(), ViewPracticeListActivity.class);
                                                            intent.putExtra("topicId", topic.getTopicId());
                                                            intent.putExtra("studentId", studentId);
                                                            intent.putExtra("topicName", topic.getTopicName());
                                                            startActivity(intent);
                                                        });

                                                        // Add buttons to layout
                                                        practiceLayout.addView(txtPracticeNow);
                                                        practiceLayout.addView(space);
                                                        practiceLayout.addView(txtViewResult);
                                                        // Create Horizontal Layout for Topic & Button


                                                        TableRow.LayoutParams numberParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                                                        TableRow.LayoutParams topicParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
                                                        TableRow.LayoutParams practiceParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);

                                                        txtNumberLayout.setLayoutParams(numberParams);
                                                        topicLayout.setLayoutParams(topicParams);
                                                        practiceLayout.setLayoutParams(practiceParams);


                                                        // Add Views to Topic Layout
                                                        row.addView(txtNumberLayout);
                                                        row.addView(topicLayout);
                                                        row.addView(practiceLayout);

                                                        // Add Topic Layout to detailsLayout
                                                        detailsLayout.addView(row);
                                                    }
                                                } else {
                                                    TextView txtTopicsTitle = new TextView(getApplicationContext());
                                                    txtTopicsTitle.setText("Topics");
                                                    txtTopicsTitle.setTextColor(Color.parseColor("#4A148C"));
                                                    txtTopicsTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                    txtTopicsTitle.setTypeface(null, Typeface.BOLD);

                                                    // Add title to detailsLayout
                                                    detailsLayout.addView(txtTopicsTitle);

                                                    TableRow headerRow = new TableRow(getApplicationContext());

                                                    TextView headerNo = new TextView(getApplicationContext());
                                                    headerNo.setText("No");
                                                    headerNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerNo.setTypeface(null, Typeface.BOLD);
                                                    headerNo.setGravity(Gravity.CENTER);
                                                    headerNo.setPadding(16, 20, 16, 20);
                                                    headerNo.setBackgroundResource(R.drawable.border);

                                                    TextView headerTopic = new TextView(getApplicationContext());
                                                    headerTopic.setText("Assignments");
                                                    headerTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerTopic.setTypeface(null, Typeface.BOLD);
                                                    headerTopic.setGravity(Gravity.CENTER);
                                                    headerTopic.setPadding(16, 20, 16, 20);
                                                    headerTopic.setBackgroundResource(R.drawable.border);

                                                    TextView headerActions = new TextView(getApplicationContext());
                                                    headerActions.setText("Conducted");
                                                    headerActions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerActions.setTypeface(null, Typeface.BOLD);
                                                    headerActions.setGravity(Gravity.CENTER);
                                                    headerActions.setPadding(16, 20, 16, 20);
                                                    headerActions.setBackgroundResource(R.drawable.border);

                                                    // Set LayoutParams to ensure equal width
                                                    TableRow.LayoutParams headerParamsNo = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                                                    TableRow.LayoutParams headerParamsTopic = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
                                                    TableRow.LayoutParams headerParamsActions = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);

                                                    headerNo.setLayoutParams(headerParamsNo);
                                                    headerTopic.setLayoutParams(headerParamsTopic);
                                                    headerActions.setLayoutParams(headerParamsActions);

                                                    // Add headers to the row
                                                    headerRow.addView(headerNo);
                                                    headerRow.addView(headerTopic);
                                                    headerRow.addView(headerActions);

                                                    // Add header row to detailsLayout
                                                    detailsLayout.addView(headerRow);

                                                    // No Topics Available
                                                    TextView noTopicTextView = new TextView(AllSchedulesActivity.this);
                                                    noTopicTextView.setText("Instructor Not Allocated Topics");
                                                    noTopicTextView.setTextSize(14);
                                                    noTopicTextView.setTypeface(Typeface.DEFAULT_BOLD);
                                                    noTopicTextView.setPadding(4, 4, 4, 4);
                                                    detailsLayout.addView(noTopicTextView);
                                                }
                                            }


                                            public void onError(String errorMessage) {
                                                txtTopic.setText("Failed to load topics");
                                                Log.e("API_ERROR", errorMessage);
                                            }
                                        });

                                        AssignmentMethod(studentId, date.getDateId(), new AssignmentCallback(){

                                            @Override
                                            public void onAssignmentReceived(List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList) {
                                                txtStatusLayout.removeView(loadingText);
                                                //txtStatusLayout.removeView(loadingText);
                                                if (!assignmentTopicsList.isEmpty()) {
                                                    TextView txtassignmentTitle = new TextView(getApplicationContext());
                                                    txtassignmentTitle.setText("AssignMents");
                                                    txtassignmentTitle.setTextColor(Color.parseColor("#4A148C"));
                                                    txtassignmentTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                    txtassignmentTitle.setTypeface(null, Typeface.BOLD);

                                                    // Add title to detailsLayout
                                                    detailsLayout.addView(txtassignmentTitle);
                                                    TableRow headerRow = new TableRow(getApplicationContext());

                                                    TextView headerNo = new TextView(getApplicationContext());
                                                    headerNo.setText("No");
                                                    headerNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerNo.setTypeface(null, Typeface.BOLD);
                                                    headerNo.setGravity(Gravity.CENTER);
                                                    headerNo.setPadding(16, 20, 16, 20);
                                                    headerNo.setBackgroundResource(R.drawable.border);

                                                    TextView headerTopic = new TextView(getApplicationContext());
                                                    headerTopic.setText("Assignments");
                                                    headerTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerTopic.setTypeface(null, Typeface.BOLD);
                                                    headerTopic.setGravity(Gravity.CENTER);
                                                    headerTopic.setPadding(16, 20, 16, 20);
                                                    headerTopic.setBackgroundResource(R.drawable.border);

                                                    TextView headerActions = new TextView(getApplicationContext());
                                                    headerActions.setText("Conducted");
                                                    headerActions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerActions.setTypeface(null, Typeface.BOLD);
                                                    headerActions.setGravity(Gravity.CENTER);
                                                    headerActions.setPadding(16, 20, 16, 20);
                                                    headerActions.setBackgroundResource(R.drawable.border);

                                                    // Set LayoutParams to ensure equal width
                                                    TableRow.LayoutParams headerParamsNo = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                                                    TableRow.LayoutParams headerParamsTopic = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
                                                    TableRow.LayoutParams headerParamsActions = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);

                                                    headerNo.setLayoutParams(headerParamsNo);
                                                    headerTopic.setLayoutParams(headerParamsTopic);
                                                    headerActions.setLayoutParams(headerParamsActions);

                                                    // Add headers to the row
                                                    headerRow.addView(headerNo);
                                                    headerRow.addView(headerTopic);
                                                    headerRow.addView(headerActions);

                                                    // Add header row to detailsLayout
                                                    detailsLayout.addView(headerRow);

                                                    for (int i = 0; i < assignmentTopicsList.size(); i++) {

                                                        AssignmentListResponse.Result.AssignmentTopics assignmentTopics = assignmentTopicsList.get(i);

                                                        TableRow row = new TableRow(getApplicationContext());

                                                        LinearLayout txtNumberLayout = new LinearLayout(getApplicationContext());
                                                        txtNumberLayout.setOrientation(LinearLayout.VERTICAL);
                                                        txtNumberLayout.setGravity(Gravity.CENTER);
                                                        txtNumberLayout.setPadding(16, 76, 16, 76);
                                                        txtNumberLayout.setBackgroundResource(R.drawable.border);

                                                        // Number Column
                                                        TextView txtNumber = new TextView(getApplicationContext());
                                                        txtNumber.setText(String.valueOf(i + 1));
                                                        txtNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        txtNumber.setPadding(16, 10, 16, 10);
                                                        txtNumber.setTextColor(Color.BLACK);
                                                        txtNumber.setGravity(Gravity.CENTER);

                                                        View spacetxtnumber = new View(getApplicationContext());
                                                        spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, 0));

                                                        txtNumberLayout.addView(txtNumber);
                                                        txtNumberLayout.addView(spacetxtnumber);

                                                        LinearLayout topicLayout = new LinearLayout(getApplicationContext());
                                                        topicLayout.setOrientation(LinearLayout.VERTICAL);
                                                        topicLayout.setGravity(Gravity.CENTER);
                                                        topicLayout.setBackgroundResource(R.drawable.border);

// Topic Column
                                                        TextView txtTopic = new TextView(getApplicationContext());
                                                        txtTopic.setText(assignmentTopics.getTopicName());
                                                        txtTopic.setMaxLines(2);
                                                        txtTopic.setEllipsize(TextUtils.TruncateAt.END);
                                                        txtTopic.setGravity(Gravity.CENTER); // Align text to start
                                                        txtTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);

                                                        txtTopic.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                                        txtTopic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                            @Override
                                                            public void onGlobalLayout() {
                                                                // Remove the listener after execution to avoid multiple calls
                                                                txtTopic.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                                                if (txtTopic.getLineCount() == 1) {
                                                                    topicLayout.setPadding(16, 86, 10, 86); // Apply for single-line text
                                                                } else {
                                                                    topicLayout.setPadding(16, 68 ,16, 68); // Apply for two-line text
                                                                }
                                                            }
                                                        });

                                                        View spacetopic = new View(getApplicationContext());
                                                        spacetopic.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, 0));

                                                        topicLayout.addView(txtTopic);
                                                        topicLayout.addView(spacetopic);




                                                        // Practice Now Button
                                                        LinearLayout practiceLayout = new LinearLayout(getApplicationContext());
                                                        practiceLayout.setOrientation(LinearLayout.VERTICAL);
                                                        practiceLayout.setGravity(Gravity.CENTER);
                                                        practiceLayout.setPadding(20, 20, 16, 20);
                                                        practiceLayout.setBackgroundResource(R.drawable.border);

                                                        TextView txtPracticeNow = new TextView(getApplicationContext());
                                                        txtPracticeNow.setText("Practice Now");
                                                        txtPracticeNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        txtPracticeNow.setPadding(16, 20, 16, 20);
                                                        txtPracticeNow.setGravity(Gravity.CENTER);
                                                        txtPracticeNow.setBackgroundResource(R.drawable.practicenow_drawable); // Correct
                                                        txtPracticeNow.setTextColor(Color.WHITE);
                                                        txtPracticeNow.setTypeface(null, Typeface.BOLD);
                                                        txtPracticeNow.setOnClickListener(v -> {
                                                            Intent intent = new Intent(getApplicationContext(), TopicPracticeActivity.class);
                                                            intent.putExtra("topicId", assignmentTopics.getTopicId());
                                                            intent.putExtra("studentId", studentId);
                                                            intent.putExtra("topicName", assignmentTopics.getTopicName());
                                                            startActivity(intent);
                                                        });

                                                        // Space between buttons
                                                        View space = new View(getApplicationContext());
                                                        space.setLayoutParams(new LinearLayout.LayoutParams(
                                                                ViewGroup.LayoutParams.MATCH_PARENT, 10));

                                                        // View Result Button
                                                        TextView txtViewResult = new TextView(getApplicationContext());
                                                        txtViewResult.setText("View Result");
                                                        txtViewResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                                                        txtViewResult.setPadding(16, 20, 16, 20);
                                                        txtViewResult.setGravity(Gravity.CENTER);
                                                        txtViewResult.setBackgroundResource(R.drawable.button_viewresult); // Correctly applied to txtViewResult
                                                        txtViewResult.setTextColor(Color.parseColor("#000000"));
                                                        txtViewResult.setTypeface(null, Typeface.BOLD);
                                                        txtViewResult.setOnClickListener(v -> {
                                                            Intent intent = new Intent(getApplicationContext(), ViewPracticeListActivity.class);
                                                            intent.putExtra("topicId", assignmentTopics.getTopicId());
                                                            intent.putExtra("studentId", studentId);
                                                            intent.putExtra("topicName", assignmentTopics.getTopicName());
                                                            startActivity(intent);
                                                        });

                                                        // Add buttons to layout
                                                        practiceLayout.addView(txtPracticeNow);
                                                        practiceLayout.addView(space);
                                                        practiceLayout.addView(txtViewResult);
                                                        // Create Horizontal Layout for Topic & Button


                                                        TableRow.LayoutParams numberParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                                                        TableRow.LayoutParams topicParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
                                                        TableRow.LayoutParams practiceParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);

                                                        txtNumberLayout.setLayoutParams(numberParams);
                                                        topicLayout.setLayoutParams(topicParams);
                                                        practiceLayout.setLayoutParams(practiceParams);


                                                        // Add Views to Topic Layout
                                                        row.addView(txtNumberLayout);
                                                        row.addView(topicLayout);
                                                        row.addView(practiceLayout);

                                                        // Add Topic Layout to detailsLayout
                                                        detailsLayout.addView(row);
                                                    }
                                                } else {
                                                    TextView txtassignmentTitle = new TextView(getApplicationContext());
                                                    txtassignmentTitle.setText("Assignments");
                                                    txtassignmentTitle.setTextColor(Color.parseColor("#4A148C"));
                                                    txtassignmentTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                                                    txtassignmentTitle.setTypeface(null, Typeface.BOLD);

                                                    // Add title to detailsLayout
                                                    detailsLayout.addView(txtassignmentTitle);

                                                    TableRow headerRow = new TableRow(getApplicationContext());

                                                    TextView headerNo = new TextView(getApplicationContext());
                                                    headerNo.setText("No");
                                                    headerNo.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerNo.setTypeface(null, Typeface.BOLD);
                                                    headerNo.setGravity(Gravity.CENTER);
                                                    headerNo.setPadding(16, 20, 16, 20);
                                                    headerNo.setBackgroundResource(R.drawable.border);

                                                    TextView headerTopic = new TextView(getApplicationContext());
                                                    headerTopic.setText("Assignments");
                                                    headerTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerTopic.setTypeface(null, Typeface.BOLD);
                                                    headerTopic.setGravity(Gravity.CENTER);
                                                    headerTopic.setPadding(16, 20, 16, 20);
                                                    headerTopic.setBackgroundResource(R.drawable.border);

                                                    TextView headerActions = new TextView(getApplicationContext());
                                                    headerActions.setText("Conducted");
                                                    headerActions.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                                    headerActions.setTypeface(null, Typeface.BOLD);
                                                    headerActions.setGravity(Gravity.CENTER);
                                                    headerActions.setPadding(16, 20, 16, 20);
                                                    headerActions.setBackgroundResource(R.drawable.border);

                                                    // Set LayoutParams to ensure equal width
                                                    TableRow.LayoutParams headerParamsNo = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
                                                    TableRow.LayoutParams headerParamsTopic = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
                                                    TableRow.LayoutParams headerParamsActions = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);

                                                    headerNo.setLayoutParams(headerParamsNo);
                                                    headerTopic.setLayoutParams(headerParamsTopic);
                                                    headerActions.setLayoutParams(headerParamsActions);

                                                    // Add headers to the row
                                                    headerRow.addView(headerNo);
                                                    headerRow.addView(headerTopic);
                                                    headerRow.addView(headerActions);

                                                    // Add header row to detailsLayout
                                                    detailsLayout.addView(headerRow);

                                                    // No Topics Available
                                                    TextView noTopicTextView = new TextView(AllSchedulesActivity.this);
                                                    noTopicTextView.setText("Instructor Not Allocated Assignment Topics");
                                                    noTopicTextView.setTextSize(14);
                                                    noTopicTextView.setTypeface(Typeface.DEFAULT_BOLD);
                                                    noTopicTextView.setPadding(4, 4, 4, 4);
                                                    detailsLayout.addView(noTopicTextView);
                                                }
                                            }


                                            public void onError(String errorMessage) {
                                                txtTopic.setText("Failed to load topics ");
                                                txtStatusLayout.removeView(loadingText);
                                                Log.e("API_ERROR", errorMessage);
                                            }
                                        });






                                        // Toggle the detailsLayout visibility
                                        if (detailsLayout.getVisibility() == View.GONE) {
                                            detailsLayout.setVisibility(View.VISIBLE);
                                            currentlyOpenLayout = detailsLayout;
                                        } else {
                                            detailsLayout.setVisibility(View.GONE);
                                            currentlyOpenLayout = null;
                                        }
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




    @SuppressLint("NewApi")
    private String formatDate(String inputDate) {
        if (inputDate == null || inputDate.isEmpty()) {
            return ""; // Handle empty values
        }

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd - MMMM - yyyy", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);

        try {
            // Parse and format the date
            LocalDate parsedDate = LocalDate.parse(inputDate.trim(), inputFormatter);
            return parsedDate.format(outputFormatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace(); // Debugging
            return inputDate; // Return original if parsing fails
        }
    }

    private void TopicsMethod(String studentId, String dateId, TopicsCallback noTopicsFound) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateId);

        Call<TopicListResponse> call = apiClient.topicList(idPart, datePart);
        call.enqueue(new Callback<TopicListResponse>() {
            @Override
            public void onResponse(Call<TopicListResponse> call, Response<TopicListResponse> response) {
                if (response.body() != null) {
                    Log.d("API_ERROR", "Response: " + new Gson().toJson(response.body())); // ✅ Debugging API response

                    if (response.body().getErrorCode() != null && response.body().getErrorCode().equals("200")) {
                        List<TopicListResponse.Result.Topics> topicsList = response.body().getResult().getTopicsList();

                        if (topicsList != null && !topicsList.isEmpty()) {
                            noTopicsFound.onTopicsReceived(topicsList); // ✅ Send data
                        } else {
                            Log.d("API_ERROR", "Topics List is Empty");
                            noTopicsFound.onTopicsReceived(new ArrayList<>()); // Empty list
                        }
                    } else {
                        Log.d("API_ERROR", "Error Code: " + response.body().getErrorCode());
                        noTopicsFound.onTopicsReceived(new ArrayList<>());
                    }
                } else {
                    Log.d("API_ERROR", "Response is NULL");
                    noTopicsFound.onTopicsReceived(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<TopicListResponse> call, Throwable t) {
                Log.d("API_FAILURE", "Error: " + t.getMessage());
                noTopicsFound.onTopicsReceived(new ArrayList<>()); // Handle failure
            }
        });

    }

    private void AssignmentMethod(String studentId, String dateId, AssignmentCallback assignmentCallback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateId);

        Call<AssignmentListResponse> call = apiClient.assignList(idPart, datePart);
        call.enqueue(new Callback<AssignmentListResponse>() {
            @Override
            public void onResponse(Call<AssignmentListResponse> call, Response<AssignmentListResponse> response) {
                if (response.body() != null) {
                    Log.d("API_ERROR", "Response: " + new Gson().toJson(response.body())); // ✅ Debugging API response

                    if (response.body().getErrorCode() != null && response.body().getErrorCode().equals("200")) {
                        List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList = response.body().getResult().getAssignmentTopicsList();

                        if (assignmentTopicsList != null && !assignmentTopicsList.isEmpty()) {
                            assignmentCallback.onAssignmentReceived(assignmentTopicsList); // ✅ Send data
                        } else {
                            Log.d("API_ERROR", "Topics List is Empty");
                            assignmentCallback.onAssignmentReceived(new ArrayList<>()); // Empty list
                        }
                    } else {
                        Log.d("API_ERROR", "Error Code: " + response.body().getErrorCode());
                        assignmentCallback.onAssignmentReceived(new ArrayList<>());
                    }
                } else {
                    Log.d("API_ERROR", "Response is NULL");
                    assignmentCallback.onAssignmentReceived(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<AssignmentListResponse> call, Throwable t) {
                Log.d("API_FAILURE", "Error: " + t.getMessage());
                assignmentCallback.onAssignmentReceived(new ArrayList<>()); // Handle failure
            }
        });
    }

}