package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.DurationAdapter;
import com.dst.abacustrainner.Adapter.LevelsAdapter;
import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.CourseLevel;
import com.dst.abacustrainner.Model.CourseLevelResponse;
import com.dst.abacustrainner.Model.DurationListResponse;
import com.dst.abacustrainner.Model.LevelPriceResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class CourseDetailActivity extends AppCompatActivity {


    private ImageView ivCart;


    RelativeLayout layoutCart;

    String courseId;

    RecyclerView recyclerViewDurationList, recyclerViewLevelList;
    DurationAdapter durationAdapter;
    LevelsAdapter levelsAdapter;
    List<CourseLevel> levelList = new ArrayList<>();
    private String selectedDurationId = null;
    TextView txtAmount, txtLevel;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);



        ivCart = findViewById(R.id.ivCart);

        layoutCart = findViewById(R.id.layout_cart);



        courseId = getIntent().getStringExtra("CoursesTypeId");

        Log.e("Reddy", courseId);

        recyclerViewDurationList = findViewById(R.id.recycler_course_duration);
        recyclerViewDurationList.setLayoutManager(new GridLayoutManager(this,3));

        durationAdapter = new DurationAdapter(this, durationId -> {
            selectedDurationId = durationId;
            fetchPricesForLevels(durationId);
        });
        recyclerViewDurationList.setAdapter(durationAdapter);


        recyclerViewLevelList = findViewById(R.id.recycler_course_levels);
        recyclerViewLevelList.setLayoutManager(new LinearLayoutManager(this));
        txtAmount = findViewById(R.id.txt_amount);
        txtLevel = findViewById(R.id.txt_level);

        levelsAdapter = new LevelsAdapter(this, new LevelsAdapter.OnLevelSelectListener() {
            @Override
            public boolean isDurationSelected() {
                return selectedDurationId != null;
            }

            @Override
            public void onLevelSelected(CourseLevel level) {
                // 👉 Cart add logic ikada
                /*CartManager.getInstance(getApplicationContext())
                        .addLevel(level, selectedDurationId);*/
                updateSummary();
            }
        });
        recyclerViewLevelList.setAdapter(levelsAdapter);



        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CartManager.getInstance(getApplicationContext()).getAllSelectedLevels("live").isEmpty()) {
                    Toast.makeText(CourseDetailActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CourseDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });


        loadDurations();
        loadLevels(courseId);

    }

    private void updateSummary() {
        int totalAmount = 0;
        int selectedCount = 0;

        for (CourseLevel level : levelList) {
            if (level.isSelected() && level.getPrice() != null) {
                selectedCount++;
                totalAmount += Integer.parseInt(level.getPrice());
            }
        }

        txtAmount.setText("₹" + totalAmount + "/-");
        txtLevel.setText(selectedCount + " levels selected");
    }

    private void fetchPricesForLevels(String  durationId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiClient apiClient = retrofit.create(ApiClient.class);

        for (CourseLevel level : levelList) {

            RequestBody levelIdBody =
                    RequestBody.create(MediaType.parse("text/plain"),
                            level.getCourseLevelId());

            RequestBody durationIdBody =
                    RequestBody.create(MediaType.parse("text/plain"),
                            durationId);

            apiClient.getLevelPrice(levelIdBody, durationIdBody)
                    .enqueue(new Callback<LevelPriceResponse>() {
                        @Override
                        public void onResponse(Call<LevelPriceResponse> call,
                                               Response<LevelPriceResponse> response) {

                            if (response.isSuccessful()
                                    && response.body() != null
                                    && response.body().getResult() != null) {

                                String price =
                                        response.body().getResult().getPrice();

                                level.setPrice(price);
                                levelsAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<LevelPriceResponse> call, Throwable t) {
                            Log.e("PRICE_ERROR", t.getMessage());
                        }
                    });
        }
    }



    private void loadLevels(String courseId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody levelPart = RequestBody.create(MediaType.parse("text/plain"), courseId);

        Call<CourseLevelResponse> call = apiClient.updateLevelList(levelPart);
        call.enqueue(new Callback<CourseLevelResponse>() {
            @Override
            public void onResponse(Call<CourseLevelResponse> call, Response<CourseLevelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    CourseLevelResponse courseLevelResponse = response.body();

                    if (courseLevelResponse.getResult() != null) {

                        List<CourseLevel> levels =
                                courseLevelResponse.getResult().getCourseLevels();
                        levelList = levels;

                        // 🔥 RecyclerView ki data set
                        levelsAdapter.setLevels(levels);
                    }
                }
            }


            @Override
            public void onFailure(Call<CourseLevelResponse> call, Throwable t) {

            }
        });

    }

    private void loadDurations() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);

        Call<DurationListResponse> call= apiClient.getDurationList();
        call.enqueue(new Callback<DurationListResponse>() {
            @Override
            public void onResponse(Call<DurationListResponse> call, Response<DurationListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    durationAdapter.setData(response.body().getResult());
                }

            }

            @Override
            public void onFailure(Call<DurationListResponse> call, Throwable t) {

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}