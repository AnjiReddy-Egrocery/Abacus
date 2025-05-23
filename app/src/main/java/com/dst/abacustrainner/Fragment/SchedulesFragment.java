package com.dst.abacustrainner.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.AllSchedulesActivity;
import com.dst.abacustrainner.Activity.AssignmentPracticeActivity;
import com.dst.abacustrainner.Activity.TopicPracticeActivity;
import com.dst.abacustrainner.Activity.ViewAssignmentListActivity;
import com.dst.abacustrainner.Activity.ViewPracticeListActivity;
import com.dst.abacustrainner.Adapter.CalendarAdapter;
import com.dst.abacustrainner.Adapter.TopicListAdapter;
import com.dst.abacustrainner.Model.AssignmentListResponse;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.Model.TopicListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.Services.OnDateClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import android.text.TextUtils;


public class SchedulesFragment extends Fragment  implements OnDateClickListener {
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private List<String> daysList;
    private TextView tvMonthYear,txtDate,txtTime;
    private ImageView btnPrevMonth, btnNextMonth;
    private Calendar currentCalendar;
    private TextView txtviewall;

    RecyclerView recyclerTopicList;
    TopicListAdapter topicListAdapter;

    private String studentId, batchId;

    private Map<String, String> scheduledDatesMap = new HashMap<>();
    Map<String, String> dateIdMap = new HashMap<>();

    TableLayout tableLayout,tableLayoutAssignments ;
    String dateId;
    String startTime,endTime,timeText,amPm,startHour,endHour;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1=inflater.inflate(R.layout.fragment_schedules,container,false);

        calendarRecyclerView =view1.findViewById(R.id.calendarRecyclerView);
        tvMonthYear = view1.findViewById(R.id.tvMonthYear);
        btnPrevMonth = view1.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view1.findViewById(R.id.btnNextMonth);
        txtviewall = view1.findViewById(R.id.viewall);
        txtDate= view1.findViewById(R.id.txt_date);
        txtTime = view1.findViewById(R.id.txt_time);


        if (getArguments() != null) {
            studentId = getArguments().getString("studentId");
            batchId = getArguments().getString("batchId");

            Log.d("Reddy","StudentId"+studentId);
            Log.d("Reddy","BatchId"+batchId);
        }

        GridLayoutManager gridlayoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(gridlayoutManager);

        // Initialize Calendar
        currentCalendar = Calendar.getInstance();
        //updateCalendar();

        ScheduledateMethod(studentId,batchId);

        // Left Arrow Click (Previous Month)
        btnPrevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCalendar.add(Calendar.MONTH, -1);
                updateCalendar();
            }
        });

        // Right Arrow Click (Next Month)
        btnNextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentCalendar.add(Calendar.MONTH, 1);
                updateCalendar();
            }
        });

        txtviewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AllSchedulesActivity.class);
                intent.putExtra("studentId",studentId);
                intent.putExtra("batchId",batchId);
                intent.putExtra("DateId",dateId);
                startActivity(intent);
            }
        });

        tableLayout = view1.findViewById(R.id.tableLayout);
        tableLayoutAssignments = view1.findViewById(R.id.tableLayoutassignment);



        return view1;
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
                            // Extract first item from the list (assuming single batch details)
                            DatedetailsResponse.Result batchDetails = daResult.get(0);

                            startTime = batchDetails.getStartTime(); // Extract Start Time
                            endTime = batchDetails.getEndTime(); // Extract End Time


                            // Formatting the time range
                            timeText = startTime + "-" + endTime; // "06 - 07 AM"
                        } else {
                            txtTime.setText("No data available"); // Handle empty result
                        }

                        scheduledDatesMap.clear();
                        dateIdMap.clear(); // ✅ Clear dateIdMap before updating

                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd - MMMM - yyyy", Locale.ENGLISH);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        int classNumber = 1;

                        for (DatedetailsResponse.Result result : daResult) {
                            if (result.getDates() != null) {
                                List<DatedetailsResponse.Result.Date> datesList = result.getDates();
                                Collections.reverse(datesList); // ✅ Reverse order

                                for (DatedetailsResponse.Result.Date dateObj : datesList) {
                                    String scheduleDate = dateObj.getScheduleDate().trim();
                                    dateId    = dateObj.getDateId();  // ✅ Get Date ID

                                    try {
                                        String formattedDate = outputFormat.format(inputFormat.parse(scheduleDate));
                                        scheduledDatesMap.put(formattedDate, "Class - " + classNumber);
                                        dateIdMap.put(formattedDate, dateId); // ✅ Store Date ID in Map
                                        Log.d("DebugDateID", "Date: " + formattedDate + " | DateID: " + dateId);
                                        classNumber++;
                                    } catch (ParseException e) {
                                        Log.e("ScheduleDebug", "Date Parsing Error: " + scheduleDate, e);
                                    }
                                }
                            }
                        }

                        // ✅  Update calendar with new data
                        updateCalendar();
                    }
                }
            }

            @Override
            public void onFailure(Call<DatedetailsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load schedules", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM - yyyy", Locale.getDefault());
        tvMonthYear.setText(sdf.format(currentCalendar.getTime()));

        daysList = generateCalendarDays();

        //ScheduledateMethod(studentId, batchId);

        calendarAdapter = new CalendarAdapter(getContext(), daysList, dateIdMap,currentCalendar,this);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


    private List<String> generateCalendarDays() {
        List<String> days = new ArrayList<>();
        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1; // Adjust for Sunday/Monday start
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = monthFormat.format(currentCalendar.getTime()); // Get current month (YYYY-MM)

        // Step 1: Add placeholders for alignment
        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add(""); // Empty placeholders
        }

        // Step 2: Add Actual Dates
        for (int i = 1; i <= daysInMonth; i++) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, i);
            String formattedDate = dateFormat.format(tempCalendar.getTime());
            String displayText = String.valueOf(i);

            if (scheduledDatesMap.containsKey(formattedDate)) {
                String classInfo = scheduledDatesMap.get(formattedDate);
                displayText += "\n" + classInfo; // ✅ Show class info

                // ✅ Ensure correct Date ID is stored for each date
                dateIdMap.put(formattedDate, dateIdMap.get(formattedDate));
            }

            days.add(displayText);
        }

        return days;
    }

    @Override
    public void onDateClick(String selectedDate, String dateId) {

        TopicsMethod(studentId,dateId);
        Assignment(studentId,dateId);

        String formattedDate = formatDate(selectedDate);

        txtDate.setText(formattedDate);
        txtTime.setText(timeText);

            // 🔥 Open a new screen if needed

    }

    private String formatDate(String selectedDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = inputFormat.parse(selectedDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return selectedDate; // Return original if parsing fails
        }
    }

    private void Assignment(String studentId, String dateId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateId);

        Call<AssignmentListResponse> call = apiClient.assignList(idPart, datePart);
        call.enqueue(new Callback<AssignmentListResponse>() {
            @Override
            public void onResponse(Call<AssignmentListResponse> call, Response<AssignmentListResponse> response) {
                AssignmentListResponse assignmentListResponse = response.body();

                if (assignmentListResponse == null || assignmentListResponse.getErrorCode() == null) {
                    Toast.makeText(getContext(), "Invalid Response", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (assignmentListResponse.getErrorCode().equals("202")) {
                    Toast.makeText(getContext(), "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                } else if (assignmentListResponse.getErrorCode().equals("200")) {
                    AssignmentListResponse.Result result = assignmentListResponse.getResult();
                    List<AssignmentListResponse.Result.AssignmentTopics> assignmentTopicsList = result.getAssignmentTopicsList();

                    tableLayoutAssignments.removeAllViews(); // Clear previous data

                    if (assignmentTopicsList == null || assignmentTopicsList.isEmpty()) {

                        TableRow headerRow = new TableRow(getContext());
                        headerRow.setBackgroundColor(Color.LTGRAY);

                        String[] headers = {"No", "Topics", "Conducted"};
                        for (String header : headers) {
                            TextView headerText = new TextView(getContext());
                            headerText.setText(header);
                            headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                            headerText.setTextColor(Color.BLACK);
                            headerText.setGravity(Gravity.CENTER);
                            headerText.setPadding(16, 20, 16, 20);
                            headerText.setBackgroundResource(R.drawable.border);
                            headerRow.addView(headerText);
                        }

                        tableLayoutAssignments.addView(headerRow);
                        // Show message if topics list is empty
                        TextView noTopicTextView = new TextView(getContext());
                        noTopicTextView.setText("Instructor Not Allocated Assignments");
                        noTopicTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        noTopicTextView.setTypeface(Typeface.DEFAULT_BOLD);
                        noTopicTextView.setTextColor(Color.BLACK);
                        noTopicTextView.setGravity(Gravity.CENTER);
                        noTopicTextView.setPadding(16, 16, 16, 16);

                        tableLayoutAssignments.addView(noTopicTextView);
                    }else{

                    TableRow headerRow = new TableRow(getContext());
                    headerRow.setBackgroundColor(Color.LTGRAY);

                    String[] headers = {"No", "Topics", "Conducted"};
                    for (String header : headers) {
                        TextView headerText = new TextView(getContext());
                        headerText.setText(header);
                        headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        headerText.setTextColor(Color.BLACK);
                        headerText.setGravity(Gravity.CENTER);
                        headerText.setPadding(16, 20, 16, 20);
                        headerText.setBackgroundResource(R.drawable.border);
                        headerRow.addView(headerText);
                    }

                    tableLayoutAssignments.addView(headerRow);

// Loop through Assignments
                    for (int i = 0; i < assignmentTopicsList.size(); i++) {
                        AssignmentListResponse.Result.AssignmentTopics topic = assignmentTopicsList.get(i);
                        TableRow row = new TableRow(getContext());

                        LinearLayout txtNumberLayout = new LinearLayout(getContext());
                        txtNumberLayout.setOrientation(LinearLayout.VERTICAL);
                        txtNumberLayout.setGravity(Gravity.CENTER);
                        txtNumberLayout.setPadding(16, 69, 16, 69);
                        txtNumberLayout.setBackgroundResource(R.drawable.border);

                        // Number Column
                        TextView txtNumber = new TextView(getContext());
                        txtNumber.setText(String.valueOf(i + 1));
                        txtNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                        txtNumber.setPadding(16, 10, 16, 10);
                        txtNumber.setTextColor(Color.BLACK);
                        txtNumber.setGravity(Gravity.CENTER);

                        View spacetxtnumber = new View(getContext());
                        spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0));

                        txtNumberLayout.addView(txtNumber);
                        txtNumberLayout.addView(spacetxtnumber);

                        LinearLayout topicLayout = new LinearLayout(getContext());
                        topicLayout.setOrientation(LinearLayout.VERTICAL);
                        topicLayout.setGravity(Gravity.CENTER);
                        topicLayout.setBackgroundResource(R.drawable.border);
                        topicLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f)); // Ensure it expands properly

// Topic Column
                        TextView txtTopic = new TextView(getContext());
                        txtTopic.setText(topic.getTopicName());
                        txtTopic.setMaxLines(2);
                        txtTopic.setEllipsize(TextUtils.TruncateAt.END);
                        txtTopic.setGravity(Gravity.CENTER); // Align text to start
                        txtTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                        txtTopic.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        txtTopic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                // Remove the listener after execution to avoid multiple calls
                                txtTopic.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                if (txtTopic.getLineCount() == 1) {
                                    topicLayout.setPadding(16, 79, 10, 79); // Apply for single-line text
                                } else {
                                    topicLayout.setPadding(16, 55 ,16, 55); // Apply for two-line text
                                }
                            }
                        });

                        View spacetopic = new View(getContext());
                        spacetopic.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0));

                        topicLayout.addView(txtTopic);
                        topicLayout.addView(spacetopic);
                        // Practice Now Button
                        LinearLayout practiceLayout = new LinearLayout(getContext());
                        practiceLayout.setOrientation(LinearLayout.VERTICAL);
                        practiceLayout.setGravity(Gravity.CENTER);
                        practiceLayout.setPadding(20, 20, 16, 20);
                        practiceLayout.setBackgroundResource(R.drawable.border);

                        TextView txtPracticeNow = new TextView(getContext());
                        txtPracticeNow.setText("Practice Now");
                        txtPracticeNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                        txtPracticeNow.setPadding(16, 20, 16, 20);
                        txtPracticeNow.setGravity(Gravity.CENTER);
                        txtPracticeNow.setBackgroundResource(R.drawable.rounded_button); // Correct
                        txtPracticeNow.setTextColor(Color.WHITE);
                        txtPracticeNow.setTypeface(null, Typeface.BOLD);
                        txtPracticeNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getContext(), AssignmentPracticeActivity.class);
                                intent.putExtra("topicId", topic.getTopicId());
                                intent.putExtra("studentId", studentId);
                                intent.putExtra("topicName", topic.getTopicName());
                                startActivity(intent);
                            }
                        });

                        // Space between buttons
                        View space = new View(getContext());
                        space.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 10));

                        // View Result Button
                        TextView txtViewResult = new TextView(getContext());
                        txtViewResult.setText("View Result");
                        txtViewResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                        txtViewResult.setPadding(16, 20, 16, 20);
                        txtViewResult.setGravity(Gravity.CENTER);
                        txtViewResult.setBackgroundResource(R.drawable.button_viewresult); // Correctly applied to txtViewResult
                        txtViewResult.setTextColor(Color.parseColor("#000000"));
                        txtViewResult.setTypeface(null, Typeface.BOLD);
                        txtViewResult.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent1=new Intent(getContext(), ViewAssignmentListActivity.class);
                                intent1.putExtra("topicId", topic.getTopicId());
                                intent1.putExtra("studentId", studentId);
                                intent1.putExtra("topicName", topic.getTopicName());
                                startActivity(intent1);
                            }
                        });

                        // Add buttons to layout
                        practiceLayout.addView(txtPracticeNow);
                        practiceLayout.addView(space);
                        practiceLayout.addView(txtViewResult);
                        // Add Columns to Row
                        row.addView(txtNumberLayout);
                        row.addView(topicLayout);
                        row.addView(practiceLayout);

                        // Add Row to Table
                        tableLayoutAssignments.addView(row);
                    }
                }
                }
            }

            @Override
            public void onFailure(Call<AssignmentListResponse> call, Throwable t) {
                Toast.makeText(getContext(), "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void TopicsMethod(String studentId, String dateId) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody datePart = RequestBody.create(MediaType.parse("text/plain"), dateId);

        Call<TopicListResponse> call = apiClient.topicList(idPart, datePart);
        call.enqueue(new Callback<TopicListResponse>() {
            @Override
            public void onResponse(Call<TopicListResponse> call, Response<TopicListResponse> response) {
                TopicListResponse topicListResponse = response.body();
                if (topicListResponse == null || topicListResponse.getErrorCode() == null) {
                    Toast.makeText(getContext(), "Invalid Response", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (topicListResponse.getErrorCode().equals("202")) {
                    Toast.makeText(getContext(), "Invalid Request, no data found for your request", Toast.LENGTH_SHORT).show();
                } else if (topicListResponse.getErrorCode().equals("200")) {
                    TopicListResponse.Result result = topicListResponse.getResult();
                    List<TopicListResponse.Result.Topics> topicsList = result.getTopicsList();


                    tableLayout.removeAllViews(); // Clear previous data

                    if (topicsList == null || topicsList.isEmpty()) {

                        TableRow headerRow = new TableRow(getContext());
                        headerRow.setBackgroundColor(Color.LTGRAY);

                        String[] headers = {"No", "Topics", "Conducted"};
                        for (String header : headers) {
                            TextView headerText = new TextView(getContext());
                            headerText.setText(header);
                            headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                            headerText.setTextColor(Color.BLACK);
                            headerText.setGravity(Gravity.CENTER);
                            headerText.setPadding(16, 20, 16, 20);
                            headerText.setBackgroundResource(R.drawable.border);
                            headerRow.addView(headerText);
                        }

                        tableLayout.addView(headerRow);
                        // Show message if topics list is empty
                        TextView noTopicTextView = new TextView(getContext());
                        noTopicTextView.setText("Instructor Not Allocated Topics");
                        noTopicTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                        noTopicTextView.setTypeface(Typeface.DEFAULT_BOLD);
                        noTopicTextView.setTextColor(Color.BLACK);
                        noTopicTextView.setGravity(Gravity.CENTER);
                        noTopicTextView.setPadding(16, 16, 16, 16);

                        tableLayout.addView(noTopicTextView);
                    }else{
                        TableRow headerRow = new TableRow(getContext());
                        headerRow.setBackgroundColor(Color.LTGRAY);

                        String[] headers = {"No", "Topics", "Conducted"};
                        for (String header : headers) {
                            TextView headerText = new TextView(getContext());
                            headerText.setText(header);
                            headerText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                            headerText.setTextColor(Color.BLACK);
                            headerText.setGravity(Gravity.CENTER);
                            headerText.setPadding(16, 20, 16, 20);
                            headerText.setBackgroundResource(R.drawable.border);
                            headerRow.addView(headerText);
                        }

                        tableLayout.addView(headerRow);

                        // Populate the table
                        for (int i = 0; i < topicsList.size(); i++) {
                            TopicListResponse.Result.Topics topic = topicsList.get(i);
                            TableRow row = new TableRow(getContext());

                            LinearLayout txtNumberLayout = new LinearLayout(getContext());
                            txtNumberLayout.setOrientation(LinearLayout.VERTICAL);
                            txtNumberLayout.setGravity(Gravity.CENTER);
                            txtNumberLayout.setPadding(16, 76, 16, 76);
                            txtNumberLayout.setBackgroundResource(R.drawable.border);

                            // Number Column
                            TextView txtNumber = new TextView(getContext());
                            txtNumber.setText(String.valueOf(i + 1));
                            txtNumber.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                            txtNumber.setPadding(16, 10, 16, 10);
                            txtNumber.setTextColor(Color.BLACK);
                            txtNumber.setGravity(Gravity.CENTER);

                            View spacetxtnumber = new View(getContext());
                            spacetxtnumber.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 0));

                            txtNumberLayout.addView(txtNumber);
                            txtNumberLayout.addView(spacetxtnumber);

                            LinearLayout topicLayout = new LinearLayout(getContext());
                            topicLayout.setOrientation(LinearLayout.VERTICAL);
                            topicLayout.setGravity(Gravity.CENTER);
                            topicLayout.setBackgroundResource(R.drawable.border);
                            topicLayout.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.1f)); // Ensure it expands properly

// Topic Column
                            TextView txtTopic = new TextView(getContext());
                            txtTopic.setText(topic.getTopicName());
                            txtTopic.setMaxLines(2);
                            txtTopic.setEllipsize(TextUtils.TruncateAt.END);
                            txtTopic.setGravity(Gravity.CENTER); // Align text to start
                            txtTopic.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

                            txtTopic.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            txtTopic.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                @Override
                                public void onGlobalLayout() {
                                    // Remove the listener after execution to avoid multiple calls
                                    txtTopic.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                                    if (txtTopic.getLineCount() == 1) {
                                        topicLayout.setPadding(16, 79, 10, 79); // Apply for single-line text
                                    } else {
                                        topicLayout.setPadding(16, 55 ,16, 55); // Apply for two-line text
                                    }
                                }
                            });

                            View spacetopic = new View(getContext());
                            spacetopic.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 0));

                            topicLayout.addView(txtTopic);
                            topicLayout.addView(spacetopic);




                            // Practice Now Button
                            LinearLayout practiceLayout = new LinearLayout(getContext());
                            practiceLayout.setOrientation(LinearLayout.VERTICAL);
                            practiceLayout.setGravity(Gravity.CENTER);
                            practiceLayout.setPadding(20, 20, 16, 20);
                            practiceLayout.setBackgroundResource(R.drawable.border);

                            TextView txtPracticeNow = new TextView(getContext());
                            txtPracticeNow.setText("Practice Now");
                            txtPracticeNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                            txtPracticeNow.setPadding(16, 20, 16, 20);
                            txtPracticeNow.setGravity(Gravity.CENTER);
                            txtPracticeNow.setBackgroundResource(R.drawable.rounded_button); // Correct
                            txtPracticeNow.setTextColor(Color.WHITE);
                            txtPracticeNow.setTypeface(null, Typeface.BOLD);
                            txtPracticeNow.setOnClickListener(v -> {
                                Intent intent = new Intent(getContext(), TopicPracticeActivity.class);
                                intent.putExtra("topicId", topic.getTopicId());
                                intent.putExtra("studentId", studentId);
                                intent.putExtra("topicName", topic.getTopicName());
                                startActivity(intent);
                            });

                            // Space between buttons
                            View space = new View(getContext());
                            space.setLayoutParams(new LinearLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT, 10));

                            // View Result Button
                            TextView txtViewResult = new TextView(getContext());
                            txtViewResult.setText("View Result");
                            txtViewResult.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                            txtViewResult.setPadding(16, 20, 16, 20);
                            txtViewResult.setGravity(Gravity.CENTER);
                            txtViewResult.setBackgroundResource(R.drawable.button_viewresult); // Correctly applied to txtViewResult
                            txtViewResult.setTextColor(Color.parseColor("#000000"));
                            txtViewResult.setTypeface(null, Typeface.BOLD);
                            txtViewResult.setOnClickListener(v -> {
                                Intent intent = new Intent(getContext(), ViewPracticeListActivity.class);
                                intent.putExtra("topicId", topic.getTopicId());
                                intent.putExtra("studentId", studentId);
                                intent.putExtra("topicName", topic.getTopicName());
                                startActivity(intent);
                            });

                            // Add buttons to layout
                            practiceLayout.addView(txtPracticeNow);
                            practiceLayout.addView(space);
                            practiceLayout.addView(txtViewResult);
                            // Add views to row
                            row.addView(txtNumberLayout);
                            row.addView(topicLayout);
                            row.addView(practiceLayout);

                            // Add row to table
                            tableLayout.addView(row);
                    }

                    }

                }
            }

            @Override
            public void onFailure(Call<TopicListResponse> call, Throwable t) {
                Toast.makeText(getContext(), "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}

