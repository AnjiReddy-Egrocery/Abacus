package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dst.abacustrainner.Model.CourseType;
import com.dst.abacustrainner.Model.CourseTypeResponse;
import com.dst.abacustrainner.Model.StudentOrdersResponse;
import com.dst.abacustrainner.Model.WorksheetOrder;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;
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

public class OrdersActivity extends AppCompatActivity {

    TableLayout tableLayout;
    private String studentId;
    LinearLayout layoutBack;
    TextView txtNoOrders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        tableLayout = findViewById(R.id.tableLayout);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        layoutBack = findViewById(R.id.layout_back);
        studentId = getIntent().getStringExtra("studentId");
        Log.e("Reddy",studentId);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadOrders(studentId);

    }

    private void loadOrders(String studentId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody CourseLevelPart = RequestBody.create(MediaType.parse("text/plain"), studentId);

        Call<StudentOrdersResponse> call = apiClient.getStudentOrders(CourseLevelPart);
        call.enqueue(new Callback<StudentOrdersResponse>() {
            @Override
            public void onResponse(Call<StudentOrdersResponse> call, Response<StudentOrdersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {


                    List<WorksheetOrder> list =
                            response.body()
                                    .getResult()
                                    .getWorksheetOrders();

                    if (list != null && !list.isEmpty()) {

                        tableLayout.setVisibility(View.VISIBLE);
                        txtNoOrders.setVisibility(View.GONE);

                        setTableData(list);

                    } else {

                        tableLayout.setVisibility(View.GONE);
                        txtNoOrders.setVisibility(View.VISIBLE);
                        txtNoOrders.setText("No Orders");

                    }                }

            }

                    @Override
            public void onFailure(Call<StudentOrdersResponse> call, Throwable t) {
                //showEmptyMessage("Server error. Please try again.");

            }
        });

    }

    private void setTableData(List<WorksheetOrder> list) {
        TableRow header = new TableRow(this);

        header.addView(createText("#"));
        header.addView(createText("OrderedOn"));
        header.addView(createText("Amount"));
        header.addView(createText("Status"));
        header.addView(createText("Action"));

        tableLayout.addView(header);

        int count = 1;

        for (WorksheetOrder order : list) {

            TableRow row = new TableRow(this);

            row.addView(createText(String.valueOf(count)));

            row.addView(createText(
                    formatDate(order.getOrderedOn())
            ));

            row.addView(createText(order.getAmount()));

            String status = "";

            if (order.getState() != null && !order.getState().isEmpty()) {
                status = order.getState();
            }

            row.addView(createText(status));

            ImageButton btn = new ImageButton(this);

            btn.setImageResource(R.drawable.baseline_preview_24); // your eye icon
            btn.setBackgroundResource(R.drawable.table_border);
            btn.setBackgroundColor(Color.TRANSPARENT);

            TableRow.LayoutParams params =
                    new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT
                    );
            btn.setLayoutParams(params);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(OrdersActivity.this, OrderDetailsActivity.class);

                    intent.putExtra("studentId", studentId);          // your studentId variable
                    intent.putExtra("orderId", order.getOrderId());   // from current order object

                    startActivity(intent);
                }
            });


            row.addView(btn);
            tableLayout.addView(row);

            count++;
        }
    }

    private TextView createText(String text) {

        TextView tv = new TextView(this);

        tv.setText(text);
        tv.setPadding(20, 20, 20, 20);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(14);


        return tv;
    }

    private String formatDate(String timestamp) {

        try {

            long time = Long.parseLong(timestamp) * 1000;
            Date date = new Date(time);

            SimpleDateFormat sdf =
                    new SimpleDateFormat(
                            "dd MMMM, yyyy hh:mm a",
                            Locale.getDefault());

            return sdf.format(date);

        } catch (Exception e) {
            return "-";
        }
    }
}