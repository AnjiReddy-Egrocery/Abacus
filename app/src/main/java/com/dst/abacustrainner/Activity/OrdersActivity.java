package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dst.abacustrainner.Adapter.OrdersAdapter;
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

    private RecyclerView recyclerOrders;
    private OrdersAdapter adapter;

    private LinearLayout layoutBack;
    private TextView txtNoOrders;

    private ProgressDialog progressDialog;

    private String studentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        recyclerOrders = findViewById(R.id.recyclerOrders);
        txtNoOrders = findViewById(R.id.txtNoOrders);
        layoutBack = findViewById(R.id.layout_back);

        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        studentId = getIntent().getStringExtra("studentId");

        Log.e("StudentId", "" + studentId);

        layoutBack.setOnClickListener(v -> finish());

        loadOrders(studentId);
    }

    private void loadOrders(String studentId) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);

        RequestBody studentBody =
                RequestBody.create(MediaType.parse("text/plain"), studentId);

        Call<StudentOrdersResponse> call =
                apiClient.getStudentOrders(studentBody);

        call.enqueue(new Callback<StudentOrdersResponse>() {

            @Override
            public void onResponse(Call<StudentOrdersResponse> call,
                                   Response<StudentOrdersResponse> response) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().getResult() != null) {

                    List<WorksheetOrder> list =
                            response.body()
                                    .getResult()
                                    .getWorksheetOrders();

                    if (list != null && !list.isEmpty()) {

                        recyclerOrders.setVisibility(View.VISIBLE);
                        txtNoOrders.setVisibility(View.GONE);

                        adapter = new OrdersAdapter(
                                OrdersActivity.this,
                                list,
                                studentId);

                        recyclerOrders.setAdapter(adapter);

                    } else {

                        recyclerOrders.setVisibility(View.GONE);
                        txtNoOrders.setVisibility(View.VISIBLE);
                        txtNoOrders.setText("No Orders Found");
                    }

                } else {

                    recyclerOrders.setVisibility(View.GONE);
                    txtNoOrders.setVisibility(View.VISIBLE);
                    txtNoOrders.setText("No Orders Found");
                }
            }

            @Override
            public void onFailure(Call<StudentOrdersResponse> call,
                                  Throwable t) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                recyclerOrders.setVisibility(View.GONE);
                txtNoOrders.setVisibility(View.VISIBLE);
                txtNoOrders.setText("Server Error");

                Log.e("Orders", t.getMessage());
            }
        });
    }
}