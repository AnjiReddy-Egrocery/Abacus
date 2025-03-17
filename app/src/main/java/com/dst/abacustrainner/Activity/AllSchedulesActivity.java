package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
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

// Create Header Row
                        TableRow headerRow = new TableRow(AllSchedulesActivity.this);
                        headerRow.setBackgroundColor(Color.LTGRAY);

                        String[] headers = {"Date", "Time", "Conducted"};
                        for (String header : headers) {
                            TextView headerText = new TextView(AllSchedulesActivity.this);
                            headerText.setText(header);
                            headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Bigger Text
                            headerText.setTypeface(Typeface.DEFAULT_BOLD);
                            headerText.setTextColor(Color.BLACK);
                            headerText.setGravity(Gravity.CENTER);
                            headerText.setPadding(8, 14, 8, 14);
                            headerText.setBackgroundResource(R.drawable.border); // **BORDER ADDED**
                            headerRow.addView(headerText);
                        }
                        tableLayout.addView(headerRow); // Add header to TableLayout

// Add Data Rows
                        for (int i = 0; i < result.size(); i++) {
                            DatedetailsResponse.Result item = result.get(i);
                            String startTime = item.getStartTime();
                            String endTime = item.getEndTime();
                            String timeText = startTime + " - " + endTime;

                            List<DatedetailsResponse.Result.Date> datesList = item.getDates();
                            Collections.reverse(datesList);

                            for (int j = 0; j < datesList.size(); j++) {
                                DatedetailsResponse.Result.Date date = datesList.get(j);
                                TableRow row = new TableRow(AllSchedulesActivity.this);

                                // **Date Column**
                                TextView txtDate = new TextView(AllSchedulesActivity.this);
                                txtDate.setText(date.getScheduleDate());
                                txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                txtDate.setPadding(8, 14, 8, 14);
                                txtDate.setGravity(Gravity.CENTER);
                                txtDate.setBackgroundResource(R.drawable.border); // **BORDER ADDED**

                                // **Time Column**
                                TextView txtTime = new TextView(AllSchedulesActivity.this);
                                txtTime.setText(timeText);
                                txtTime.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                                txtTime.setPadding(8, 14, 8, 14); // **Reduce left & right padding**
                                txtTime.setGravity(Gravity.CENTER);
                                txtTime.setBackgroundResource(R.drawable.border); // **BORDER ADDED**

                                // **Conducted Column (Button Style)**
                                TextView txtConducted = new TextView(AllSchedulesActivity.this);
                                txtConducted.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                                txtConducted.setGravity(Gravity.CENTER);
                                txtConducted.setPadding(30, 10, 30, 10); // **Left/Right gap added**
                                txtConducted.setTypeface(Typeface.DEFAULT_BOLD);
                                txtConducted.setTextColor(Color.WHITE);
                                txtConducted.setBackgroundResource(R.drawable.button_background); // **Rounded button style**

                                // **Set Button Text**
                                if (j == 0) {
                                    txtConducted.setText("Join Now");
                                } else {
                                    txtConducted.setText("Up Coming");
                                }

                                // **Add Views to Row**
                                row.addView(txtDate);
                                row.addView(txtTime);
                                row.addView(txtConducted);

                                // **Add Row to TableLayout**
                                tableLayout.addView(row);
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