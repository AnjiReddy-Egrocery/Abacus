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
import com.dst.abacustrainner.Model.CartResponse;
import com.dst.abacustrainner.Model.CartResponseResult;
import com.dst.abacustrainner.Model.CourseLevel;
import com.dst.abacustrainner.Model.CourseLevelCart;
import com.dst.abacustrainner.Model.CourseLevelResponse;
import com.dst.abacustrainner.Model.DurationListResponse;
import com.dst.abacustrainner.Model.LevelPriceResponse;
import com.dst.abacustrainner.Model.SubscribedLevelsResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.google.gson.Gson;

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


    ImageView ivCart;
    TextView tvCartCount;
    int cartCount = 0;

    Button butAddCart, butViewCart;


    RelativeLayout layoutCart;

    String courseId;
    String headerName;
    LinearLayout layoutCourseBack;
    RecyclerView recyclerViewDurationList, recyclerViewLevelList;
    DurationAdapter durationAdapter;
    LevelsAdapter levelsAdapter;
    List<CourseLevel> levelList = new ArrayList<>();
    private String selectedDurationId = null;
    TextView txtAmount, txtLevel,txtHeadername;

    private int selectedCourseLevelId = -1;   // ✅ GLOBAL


    private List<CourseLevel> selectedLevels = new ArrayList<>();
    TextView tvDurationError;
    String studentId;
    private List<String> subscribedLevelIds = new ArrayList<>();



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);



        ivCart = findViewById(R.id.ivCart);
        tvCartCount = findViewById(R.id.tvCartCount);

// default ga hide
        tvCartCount.setVisibility(View.GONE);

        layoutCart = findViewById(R.id.layout_cart);
        butAddCart = findViewById(R.id.but_addCart);
        butViewCart = findViewById(R.id.but_view_cart);
        tvDurationError = findViewById(R.id.tvDurationError);
        txtHeadername = findViewById(R.id.txt_header_name);

        layoutCourseBack = findViewById(R.id.layout_course_back);

        courseId = getIntent().getStringExtra("CoursesTypeId");
        headerName = getIntent().getStringExtra("HeaderName");
        studentId = getIntent().getStringExtra("studentId");

        txtHeadername.setText(headerName);

        if (courseId != null) {
            Log.d("Reddy", "courseId: " + courseId);
        } else {
            Log.d("Reddy", "courseId is NULL ❌");
        }

        if (studentId != null) {
            Log.d("Reddy", "studentId: " + studentId);
        } else {
            Log.d("Reddy", "studentId is NULL ❌");
        }





        recyclerViewDurationList = findViewById(R.id.recycler_course_duration);
        recyclerViewDurationList.setLayoutManager(new GridLayoutManager(this,3));

        durationAdapter = new DurationAdapter(this, durationId -> {
            selectedDurationId = durationId;

            tvDurationError.setVisibility(View.GONE); // 🔥 hide error
            String worksheetRnm =
                    CartManager.getInstance(CourseDetailActivity.this)
                            .getWorksheetRnm();

            // 🔥 SAVE selected duration
            CartManager.getInstance(CourseDetailActivity.this)
                    .saveSelectedDuration(worksheetRnm, courseId, durationId);
            fetchPricesForLevels(durationId);
        });

        layoutCourseBack.setOnClickListener(v -> {
            finish();   // 🔥 DON'T create new intent
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
                // 🔥 Duration not selected
                if (selectedDurationId == null) {

                    tvDurationError.setVisibility(View.VISIBLE);

                    Toast.makeText(CourseDetailActivity.this,
                            "Please Select Subscription Duration",
                            Toast.LENGTH_SHORT).show();

                    return; // ❌ STOP selection
                }
                tvDurationError.setVisibility(View.GONE);
                if (level == null) return;
                if (level.isSelected()) {
                boolean alreadyExists = false;

                for (CourseLevel l : selectedLevels) {
                    if (l.getCourseLevelId().equals(level.getCourseLevelId())) {
                        alreadyExists = true;
                        break;
                    }
                }

                if (!alreadyExists) {
                    selectedLevels.add(level);
                }

            } else {

                // 🔥 FIX HERE
                for (int i = 0; i < selectedLevels.size(); i++) {
                    if (selectedLevels.get(i).getCourseLevelId()
                            .equals(level.getCourseLevelId())) {

                        selectedLevels.remove(i);
                        break;
                    }
                }
            }
                Log.e("Reddy",
                        "Selected courseTypeId = " + courseId +
                                " | Selected levels count = " + selectedLevels.size());
                String worksheetRnm =
                        CartManager.getInstance(CourseDetailActivity.this)
                                .getWorksheetRnm();

                // 🔥 SAVE selected levels
                CartManager.getInstance(CourseDetailActivity.this)
                        .saveSelectedLevels(worksheetRnm, courseId, selectedLevels);

                updateSummary();
                // updateAddCartButton();
            }
        });
        recyclerViewLevelList.setAdapter(levelsAdapter);

        butAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Anji", "Button clicked");


                if (selectedDurationId == null) {
                    Toast.makeText(CourseDetailActivity.this, "Please select duration", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedLevels == null || selectedLevels.isEmpty()) {
                    Toast.makeText(CourseDetailActivity.this, "Please select level", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔴 STEP 1: Block if ANY subscribed level selected
                List<CourseLevel> validLevels = new ArrayList<>();

                for (CourseLevel level : selectedLevels) {
                    if (!level.isSubscribed()) {
                        validLevels.add(level);
                    }
                }

                if (validLevels.isEmpty()) {
                    Toast.makeText(CourseDetailActivity.this,
                            "All selected levels already subscribed",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String worksheetRnm =
                        CartManager.getInstance(CourseDetailActivity.this).getWorksheetRnm();


                Log.e("Anji", "worksheetRnm = " + worksheetRnm);
                Log.e("Anji", "courseTypeId = " + courseId);

                Log.e("Anji", "durationId = " + selectedDurationId);

                int totalCalls = validLevels.size();
                int[] completedCalls = {0};


                // ✅ STEP 3: Call API
                for (CourseLevel level : validLevels) {

                    Log.d("Anji", "Adding CourseLevelId = " + level.getCourseLevelId());

                    addToCartApi(
                            worksheetRnm,
                            courseId,
                            level.getCourseLevelId(),
                            selectedDurationId,
                            new Runnable() {
                                @Override
                                public void run() {
                                    completedCalls[0]++;

                                    if (completedCalls[0] == totalCalls) {
                                        // ✅ Navigate after all calls

                                        Intent intent = new Intent(
                                                CourseDetailActivity.this,
                                                CartActivity.class
                                        );

                                        intent.putExtra("WorkSheetRnm", worksheetRnm);
                                        intent.putStringArrayListExtra(
                                                "SUB_IDS",
                                                new ArrayList<>(subscribedLevelIds)
                                        );

                                        startActivity(intent);
                                    }
                                }
                            }
                    );
                }
            }
        });



   layoutCart.setOnClickListener(new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           Log.e("Anji", "Button clicked");


           if (selectedDurationId == null) {
               Toast.makeText(CourseDetailActivity.this, "Please select duration", Toast.LENGTH_SHORT).show();
               return;
           }

           if (selectedLevels == null || selectedLevels.isEmpty()) {
               Toast.makeText(CourseDetailActivity.this, "Please select level", Toast.LENGTH_SHORT).show();
               return;
           }

           // 🔴 STEP 1: Block if ANY subscribed level selected
           List<CourseLevel> validLevels = new ArrayList<>();

           for (CourseLevel level : selectedLevels) {
               if (!level.isSubscribed()) {
                   validLevels.add(level);
               }
           }

           if (validLevels.isEmpty()) {
               Toast.makeText(CourseDetailActivity.this,
                       "All selected levels already subscribed",
                       Toast.LENGTH_SHORT).show();
               return;
           }

           String worksheetRnm =
                   CartManager.getInstance(CourseDetailActivity.this).getWorksheetRnm();


           Log.e("Anji", "worksheetRnm = " + worksheetRnm);
           Log.e("Anji", "courseTypeId = " + courseId);

           Log.e("Anji", "durationId = " + selectedDurationId);

           int totalCalls = validLevels.size();
           int[] completedCalls = {0};


           // ✅ STEP 3: Call API
           for (CourseLevel level : validLevels) {

               Log.d("Anji", "Adding CourseLevelId = " + level.getCourseLevelId());

               addToCartApi(
                       worksheetRnm,
                       courseId,
                       level.getCourseLevelId(),
                       selectedDurationId,
                       new Runnable() {
                           @Override
                           public void run() {
                               completedCalls[0]++;

                               if (completedCalls[0] == totalCalls) {
                                   // ✅ Navigate after all calls

                                   Intent intent = new Intent(
                                           CourseDetailActivity.this,
                                           CartActivity.class
                                   );

                                   intent.putExtra("WorkSheetRnm", worksheetRnm);
                                   intent.putStringArrayListExtra(
                                           "SUB_IDS",
                                           new ArrayList<>(subscribedLevelIds)
                                   );

                                   startActivity(intent);
                               }
                           }
                       }
               );
           }
       }
   });
   butViewCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Anji", "Button clicked");


                if (selectedDurationId == null) {
                    Toast.makeText(CourseDetailActivity.this, "Please select duration", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedLevels == null || selectedLevels.isEmpty()) {
                    Toast.makeText(CourseDetailActivity.this, "Please select level", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 🔴 STEP 1: Block if ANY subscribed level selected
                List<CourseLevel> validLevels = new ArrayList<>();

                for (CourseLevel level : selectedLevels) {
                    if (!level.isSubscribed()) {
                        validLevels.add(level);
                    }
                }

                if (validLevels.isEmpty()) {
                    Toast.makeText(CourseDetailActivity.this,
                            "All selected levels already subscribed",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String worksheetRnm =
                        CartManager.getInstance(CourseDetailActivity.this).getWorksheetRnm();


                Log.e("Anji", "worksheetRnm = " + worksheetRnm);
                Log.e("Anji", "courseTypeId = " + courseId);

                Log.e("Anji", "durationId = " + selectedDurationId);

                int totalCalls = validLevels.size();
                int[] completedCalls = {0};


                // ✅ STEP 3: Call API
                for (CourseLevel level : validLevels) {

                    Log.d("Anji", "Adding CourseLevelId = " + level.getCourseLevelId());

                    addToCartApi(
                            worksheetRnm,
                            courseId,
                            level.getCourseLevelId(),
                            selectedDurationId,
                            new Runnable() {
                                @Override
                                public void run() {
                                    completedCalls[0]++;

                                    if (completedCalls[0] == totalCalls) {
                                        // ✅ Navigate after all calls

                                        Intent intent = new Intent(
                                                CourseDetailActivity.this,
                                                CartActivity.class
                                        );

                                        intent.putExtra("WorkSheetRnm", worksheetRnm);
                                        intent.putStringArrayListExtra(
                                                "SUB_IDS",
                                                new ArrayList<>(subscribedLevelIds)
                                        );

                                        startActivity(intent);
                                    }
                                }
                            }
                    );
                }
            }
        });


        restoreSelections();
        loadDurations();
        loadLevels(courseId);

    }

    private void restoreSelections() {
        String worksheetRnm =
                CartManager.getInstance(this).getWorksheetRnm();

        // 🔥 Restore Duration
        selectedDurationId =
                CartManager.getInstance(this)
                        .getSelectedDuration(worksheetRnm, courseId);

        // 🔥 Restore Levels
        List<String> savedLevelIds =
                CartManager.getInstance(this)
                        .getSelectedLevelIds(worksheetRnm, courseId);

        if (savedLevelIds != null) {

            for (CourseLevel level : levelList) {

                if (savedLevelIds.contains(level.getCourseLevelId())) {
                    level.setSelected(true);
                    selectedLevels.add(level);
                }
            }

            levelsAdapter.notifyDataSetChanged();
            updateSummary();
        }
    }


    private void addToCartApi(String worksheetRnm, String courseId, String courseLevelId, String selectedDurationId,Runnable onComplete) {

        Log.d("Anji", "API CALL STARTED");

        Log.d("Anji", "worksheetRnm=" + worksheetRnm);
        Log.d("Anji", "courseTypeId=" + courseId);
        Log.d("Anji", "courseLevelId=" + courseLevelId);
        Log.d("Anji", "durationId=" + selectedDurationId);

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

        RequestBody rnmBody =
                RequestBody.create(MediaType.parse("text/plain"), worksheetRnm);
        RequestBody courseTypeBody =
                RequestBody.create(MediaType.parse("text/plain"), courseId);
        RequestBody levelBody =
                RequestBody.create(MediaType.parse("text/plain"), courseLevelId);
        RequestBody durationBody =
                RequestBody.create(MediaType.parse("text/plain"), selectedDurationId);

        Call<CartResponse> call= apiClient.worksheetAddToCart(rnmBody, courseTypeBody, levelBody, durationBody);

        call.enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                Log.d("Anji", "Response code = " + response.code());
                Log.d("Anji", "Full Response = " + new Gson().toJson(response.body()));


                if (response.isSuccessful() && response.body() != null) {

                    CartResponse cartResponse = response.body();

                    List<CartResponseResult> results = cartResponse.getResult();

                    if (results != null && !results.isEmpty()) {

                        CartResponseResult first = results.get(0);

                        if (first.getCourseLevels() != null && !first.getCourseLevels().isEmpty()) {

                            CourseLevelCart level = first.getCourseLevels().get(0);

                            Log.e("Reddy", "CartId = " + level.getCartId());
                            updateCartUI();
                        }
                    }
                }

                // 🔥 ADD THIS LINE
                if (onComplete != null) onComplete.run();
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {

                Log.e("Reddy", "API FAILED ❌ " + t.getMessage());

                Toast.makeText(
                        CourseDetailActivity.this,
                        "Failed to add cart",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void updateCartUI() {

        cartCount++;

        if (cartCount > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(cartCount));
        } else {
            tvCartCount.setVisibility(View.GONE);
        }
    }
    private void updateSummary() {
        int totalAmount = 0;
        int selectedCount = 0;

        for (CourseLevel level : levelList) {

            // 🔥 Skip subscribed levels
            if (level.isSubscribed()) continue;

            // 🔥 Only selected levels
            if (level.isSelected()) {

                selectedCount++;

                if (level.getPrice() != null) {
                    try {
                        totalAmount += Integer.parseInt(level.getPrice());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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

        final int totalLevels = levelList.size();
        final int[] loadedCount = {0}; // 🔥 track API calls

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
                            }

                            loadedCount[0]++;

                            // 🔥 When all prices loaded → update UI
                            if (loadedCount[0] == totalLevels) {
                                levelsAdapter.notifyDataSetChanged();
                                updateSummary(); // ✅ FIX HERE
                            }
                        }

                        @Override
                        public void onFailure(Call<LevelPriceResponse> call, Throwable t) {
                            loadedCount[0]++;

                            if (loadedCount[0] == totalLevels) {
                                updateSummary(); // still update
                            }

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
                        restoreSelections();
                        // 🔥 VERY IMPORTANT FIX
                        if (selectedDurationId != null) {
                            fetchPricesForLevels(selectedDurationId);
                        }

                        fetchSubscribedLevels(studentId);
                    }
                }
            }


            @Override
            public void onFailure(Call<CourseLevelResponse> call, Throwable t) {

            }
        });

    }

    private void fetchSubscribedLevels(String studentId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);

        RequestBody studentIdPart = RequestBody.create(MediaType.parse("text/plain"), studentId);

        Call<SubscribedLevelsResponse> call= apiClient.getSubscribed(studentIdPart);
        call.enqueue(new Callback<SubscribedLevelsResponse>() {
            @Override
            public void onResponse(Call<SubscribedLevelsResponse> call, Response<SubscribedLevelsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    subscribedLevelIds = response.body().getResult();

                    Log.e("SUB_IDS", subscribedLevelIds.toString());

                    // ✅ IMPORTANT: clear old selections
                    selectedLevels.clear();

                    String worksheetRnm =
                            CartManager.getInstance(CourseDetailActivity.this)
                                    .getWorksheetRnm();

                    List<String> savedLevelIds =
                            CartManager.getInstance(CourseDetailActivity.this)
                                    .getSelectedLevelIds(worksheetRnm, courseId);

                    // ✅ LOOP all levels
                    for (CourseLevel level : levelList) {

                        boolean isSubscribed = false;

                        // 🔥 SAFE CHECK (avoid crash)
                        if (subscribedLevelIds != null) {

                            for (String subId : subscribedLevelIds) {

                                if (subId.equals(level.getCourseLevelId())) {
                                    isSubscribed = true;
                                    break;
                                }
                            }
                        }

                        if (isSubscribed) {

                            // 🔴 Subscribed level
                            level.setSubscribed(true);
                            level.setSelected(false);

                        } else {

                            // 🟢 Not subscribed
                            level.setSubscribed(false);

                            // ✅ restore previous selection
                            if (savedLevelIds != null &&
                                    savedLevelIds.contains(level.getCourseLevelId())) {

                                level.setSelected(true);
                                selectedLevels.add(level);

                            } else {
                                level.setSelected(false);
                            }
                        }
                    }

                    // ✅ refresh UI
                    levelsAdapter.notifyDataSetChanged();
                    updateSummary();
                }
            }

            @Override
            public void onFailure(Call<SubscribedLevelsResponse> call, Throwable t) {
                Log.e("SUB_ERROR", t.getMessage());
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
                    if (selectedDurationId != null) {
                        durationAdapter.setSelectedDuration(selectedDurationId);
                    }
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


        if (cartCount > 0) {
            tvCartCount.setVisibility(View.VISIBLE);
            tvCartCount.setText(String.valueOf(cartCount));
        }

        String worksheetRnm =
                CartManager.getInstance(this).getWorksheetRnm();

        // 🔥 Restore Duration
        selectedDurationId =
                CartManager.getInstance(this)
                        .getSelectedDuration(worksheetRnm, courseId);



        // 🔥 Restore Levels
        List<String> savedLevelIds =
                CartManager.getInstance(this)
                        .getSelectedLevelIds(worksheetRnm, courseId);

        if (savedLevelIds != null) {

            selectedLevels.clear();

            for (CourseLevel level : levelList) {

                if (savedLevelIds.contains(level.getCourseLevelId())) {
                    level.setSelected(true);
                    selectedLevels.add(level);
                } else {
                    level.setSelected(false);
                }
            }

            levelsAdapter.notifyDataSetChanged();
            updateSummary();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // ✅ Only navigate back
    }
}