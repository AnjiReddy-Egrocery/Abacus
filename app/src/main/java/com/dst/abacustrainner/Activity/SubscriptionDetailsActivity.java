package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.Adapter.OrderListAdapter;
import com.dst.abacustrainner.Model.OrderListResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SubscriptionDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    OrderListAdapter orderListAdapter;

    TextView txtEmpty;
    private String studentId;
    private LinearLayout layoutBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_details);

        recyclerView = findViewById(R.id.recycler_order_list);
        txtEmpty = findViewById(R.id.tvEmptyMessage);
        layoutBack = findViewById(R.id.fragment_container);

        studentId = getIntent().getStringExtra("studentId");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderListAdapter = new OrderListAdapter(this);
        recyclerView.setAdapter(orderListAdapter);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        loadOrdersList(studentId);

    }

    private void loadOrdersList(String studentId) {

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody OrderLevelPart = RequestBody.create(MediaType.parse("text/plain"), studentId);

        Call<OrderListResponse> call = apiClient.getOrderList(OrderLevelPart);
        call.enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    OrderListResponse res = response.body();

                    // ✅ SUCCESS CASE
                    if ("200".equals(res.getErrorCode())) {

                        if (res.getResult() != null &&
                                res.getResult().getCourseTypes() != null &&
                                !res.getResult().getCourseTypes().isEmpty()) {

                            List<OrderListResponse.CourseType> levels =
                                    res.getResult().getCourseTypes();

                            orderListAdapter.setLevels(levels,studentId);

                        } else {
                            showEmptyMessage(res.getEmptyAssignmentTopicsessage());
                        }

                    }
                    // ⚠️ NO ACTIVE SUBSCRIPTION
                    else if ("202".equals(res.getErrorCode())) {

                        showEmptyMessage(
                                "You do not have any active worksheet subscriptions. " +
                                        "Please contact the administrator for more information."
                        );
                    }
                    // ❌ OTHER ERROR
                    else {
                        showEmptyMessage(res.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                showEmptyMessage("Server error. Please try again.");
            }
        });
    }

    private void showEmptyMessage(String emptyAssignmentTopicsessage) {
        recyclerView.setVisibility(View.GONE);
        txtEmpty.setVisibility(View.VISIBLE);
        txtEmpty.setText(emptyAssignmentTopicsessage);
    }
}