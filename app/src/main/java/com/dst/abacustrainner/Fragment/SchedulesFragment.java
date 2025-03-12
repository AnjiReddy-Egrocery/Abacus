package com.dst.abacustrainner.Fragment;

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dst.abacustrainner.Activity.AllSchedulesActivity;
import com.dst.abacustrainner.Activity.BatchDatesDetailsActivity;
import com.dst.abacustrainner.Adapter.BatchDatesDetailsAdapter;
import com.dst.abacustrainner.Adapter.CalendarAdapter;
import com.dst.abacustrainner.Model.DatedetailsResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.Services.OnDateClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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


public class SchedulesFragment extends Fragment  implements OnDateClickListener {
    private RecyclerView calendarRecyclerView;
    private CalendarAdapter calendarAdapter;
    private List<String> daysList;
    private TextView tvMonthYear;
    private ImageView btnPrevMonth, btnNextMonth;
    private Calendar currentCalendar;
    private TextView txtviewall;

    private String studentId, batchId;
    List<String> scheduledDates = new ArrayList<>();
    private Map<String, String> scheduledDatesMap = new HashMap<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view1=inflater.inflate(R.layout.fragment_schedules,container,false);

        calendarRecyclerView =view1.findViewById(R.id.calendarRecyclerView);
        tvMonthYear = view1.findViewById(R.id.tvMonthYear);
        btnPrevMonth = view1.findViewById(R.id.btnPrevMonth);
        btnNextMonth = view1.findViewById(R.id.btnNextMonth);
        txtviewall = view1.findViewById(R.id.viewall);

        if (getArguments() != null) {
            studentId = getArguments().getString("studentId");
            batchId = getArguments().getString("batchId");

            Log.d("Reddy","StudentId"+studentId);
            Log.d("Reddy","BatchId"+batchId);
        }

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);

        // Initialize Calendar
        currentCalendar = Calendar.getInstance();
        updateCalendar();

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
                startActivity(intent);
            }
        });

        ScheduledateMethod(studentId,batchId);

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

                        scheduledDatesMap.clear();
                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd - MMMM - yyyy", Locale.ENGLISH);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        int classNumber = 1; // Start numbering from Class - 1

                            // ** Create a list to store all schedule dates before processing them **
                        List<String> allDates = new ArrayList<>();

                        // ** Step 1: Extract all dates **
                        for (DatedetailsResponse.Result result : daResult) {
                            if (result.getDates() != null) {
                                for (DatedetailsResponse.Result.Date dateObj : result.getDates()) {
                                    allDates.add(dateObj.getScheduleDate().trim());
                                }
                            }
                        }

                    // ** Step 2: Process dates in reverse order **
                        Collections.reverse(allDates); // Reverse the list

                        for (String scheduleDate : allDates) {
                            try {
                                String formattedDate = outputFormat.format(inputFormat.parse(scheduleDate));
                                scheduledDatesMap.put(formattedDate, "Class - " + classNumber);
                                classNumber++; // Increment for the next date
                            } catch (ParseException e) {
                                Log.e("ScheduleDebug", "Date Parsing Error: " + scheduleDate, e);
                            }
                        }

// Update the calendar with new data
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
        calendarAdapter = new CalendarAdapter(getContext(), daysList, scheduledDates,this);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }


    private List<String> generateCalendarDays() {
        List<String> days = new ArrayList<>();
        Calendar tempCalendar = (Calendar) currentCalendar.clone();
        tempCalendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK) - 1; // Adjust to start from Sunday/Monday
        int daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("d", Locale.getDefault());
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String currentMonth = monthFormat.format(currentCalendar.getTime()); // Get current month (YYYY-MM)

        // Step 1: Add placeholders for alignment
        for (int i = 0; i < firstDayOfWeek; i++) {
            days.add(""); // Empty placeholders
        }

        // Step 2: Add Actual Dates
        for (int i = 1; i <= daysInMonth; i++) {
            tempCalendar.set(Calendar.DAY_OF_MONTH, i);
            String formattedDate = dateFormat.format(tempCalendar.getTime()); // Full date format (yyyy-MM-dd)
            String displayText = String.valueOf(i); // Default text is just the date

            // Check if the date is in the scheduled dates list
            if (scheduledDatesMap.containsKey(formattedDate)) {
                displayText += "\n" + scheduledDatesMap.get(formattedDate); // Append class info
            }

            days.add(displayText);
        }

        return days;
    }

    @Override
    public void onDateClick(String selectedDate) {
        // 🎯 Handle click event in `SchedulesFragment`
        Toast.makeText(getContext(), "Clicked on: " + selectedDate, Toast.LENGTH_SHORT).show();

        // 🔥 Open a new screen if needed

    }
}

