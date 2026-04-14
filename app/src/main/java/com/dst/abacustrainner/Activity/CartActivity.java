package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Adapter.CartDetailAdapter;
import com.dst.abacustrainner.Adapter.CourseLevelAdapter;
import com.dst.abacustrainner.Model.CartDetailsResponse;
import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.Model.OnCartDeleteListener;
import com.dst.abacustrainner.Model.OnDeleteCart;
import com.dst.abacustrainner.Model.OrderCreateResponse;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.Model.SubmitDataResponse;
import com.dst.abacustrainner.Model.cartDeleteResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;

import java.util.ArrayList;
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

public class CartActivity extends AppCompatActivity {

    private LinearLayout layoutCartItems, layoutCartBack;
    private Button btnContinueShopping, btnCheckout;
    String workSheetRnm;
    TextView txtCourseType;

    RecyclerView recyclerCartList;
    CartDetailAdapter cartDetalAdapter;
    TextView textAmount;

    Button butCheckOut;
    String studentId;
    int totalAmount;
    ArrayList<String> subscribedIds;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        layoutCartItems = findViewById(R.id.layoutCartItems);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnCheckout = findViewById(R.id.btnCheckout);
        layoutCartBack = findViewById(R.id.layout_cart_back);
        txtCourseType = findViewById(R.id.txt_name);
        recyclerCartList = findViewById(R.id.recycler_cartItems);
        textAmount = findViewById(R.id.txt_amount);
        butCheckOut = findViewById(R.id.btnCheckout);

        workSheetRnm = getIntent().getStringExtra("WorkSheetRnm");
        StudentRegistationResponse.Result result= SharedPrefManager.getInstance(CartActivity.this).getUserData();
        studentId=result.getStudentId();
        totalAmount = getIntent().getIntExtra("TOTAL_AMOUNT", 0);
        subscribedIds = getIntent().getStringArrayListExtra("SUB_IDS");

        Log.d("Anji",workSheetRnm);
        Log.d("Anji",studentId);
        Log.d("Anji", String.valueOf(totalAmount));

        recyclerCartList.setLayoutManager(new LinearLayoutManager(this));
        cartDetalAdapter = new CartDetailAdapter(CartActivity.this, new CartDetailAdapter.OnDeleteCart() {
            @Override
            public void onDeleteClick(String cartId) {
                removeCartItem(cartId);
            }
        });
        recyclerCartList.setAdapter(cartDetalAdapter);

        btnContinueShopping.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, CoursesActivity.class);
            startActivity(intent);
        });



        butCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String totalAmount = textAmount.getText().toString();
                String cleanAmount = totalAmount.replace("₹", "").replace("/-", "");

                SubmitCartData(workSheetRnm,studentId,cleanAmount);
            }
        });


                loadCartList(workSheetRnm);


    }

    private void SubmitCartData(String workSheetRnm, String studentId, String totalAmount) {
        Log.d("Anji","Amount"+totalAmount);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody WorksheetPart = RequestBody.create(MediaType.parse("text/plain"), workSheetRnm);
        RequestBody StudentPartPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody AmountPart = RequestBody.create(MediaType.parse("text/plain"), totalAmount);
        Call<OrderCreateResponse> call = apiClient.getCheckOutSubmit(WorksheetPart,StudentPartPart,AmountPart);
        call.enqueue(new Callback<OrderCreateResponse>() {
            @Override
            public void onResponse(Call<OrderCreateResponse> call, Response<OrderCreateResponse> response) {
                if (response.body() != null &&
                        "200".equals(response.body().getErrorCode())) {

                    OrderCreateResponse.Result result = response.body().getResult();

                    String merchantRefNo = result.getMerchantRefNo();
                    String amount = result.getPrice();

                    ArrayList<String> levelNames = new ArrayList<>();
                    ArrayList<String> levelPrices = new ArrayList<>();
                    ArrayList<String> cartIds = new ArrayList<>();

                    for (CartDetailsResponse.CourseLevels level : cartDetalAdapter.getLevels()) {
                        levelNames.add(level.getCourseLevel());
                        levelPrices.add(level.getCourseLevelPrice());
                        cartIds.add(level.getCartId());
                    }

                    Log.d("Reddy","OrderId"+merchantRefNo);
                    Log.d("Reddy","amount"+amount);
                    Log.d("Reddy","worksheetRnm"+workSheetRnm);
                    Log.d("Reddy","StudentId"+studentId);

                    Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                    intent.putExtra("StudentId", studentId);
                    intent.putExtra("WorkRNM", workSheetRnm);
                    intent.putExtra("TotalAmount", amount);
                    intent.putExtra("merchantRef",merchantRefNo);
                    intent.putStringArrayListExtra("LevelNames", levelNames);
                    intent.putStringArrayListExtra("LevelPrices", levelPrices);
                    intent.putStringArrayListExtra("CartIds", cartIds);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
            }

            @Override
            public void onFailure(Call<OrderCreateResponse> call, Throwable t) {

            }
        });
    }

    private void removeCartItem(String cartId) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody CartLevelPart = RequestBody.create(MediaType.parse("text/plain"), cartId);
        Call<cartDeleteResponse> call = apiClient.getDeleteCart(CartLevelPart);
        call.enqueue(new Callback<cartDeleteResponse>() {
            @Override
            public void onResponse(Call<cartDeleteResponse> call, Response<cartDeleteResponse> response) {
                if (response.body() != null &&
                        "200".equals(response.body().getErrorCode())) {

                    Toast.makeText(CartActivity.this,
                            "Item removed from cart",
                            Toast.LENGTH_SHORT).show();

                    // ✅ INSTANT UI UPDATE
                    cartDetalAdapter.removeItemByCartId(cartId);

                    // ✅ TOTAL UPDATE
                    recalculateTotal();

                }
            }

            @Override
            public void onFailure(Call<cartDeleteResponse> call, Throwable t) {

            }
        });
    }

    private void loadCartList(String workSheetRnm) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody CartLevelPart = RequestBody.create(MediaType.parse("text/plain"), workSheetRnm);

        Call<CartDetailsResponse> call = apiClient.getCartList(CartLevelPart);
        call.enqueue(new Callback<CartDetailsResponse>() {
            @Override
            public void onResponse(Call<CartDetailsResponse> call, Response<CartDetailsResponse> response) {
                CartDetailsResponse res = response.body();

                if (res != null && "200".equals(res.getErrorCode())) {

                    List<CartDetailsResponse.Result> resultList = res.getResult();

                    if (resultList != null && !resultList.isEmpty()) {

                        // 👉 First course type display
                       /* StringBuilder builder = new StringBuilder();

                        for (int i = 0; i < resultList.size(); i++) {

                            String courseType = resultList.get(i).getCourseType();

                            if (courseType != null && !courseType.isEmpty()) {
                                Log.e("COURSE_TYPE", courseType);

                                builder.append(courseType).append("\n"); // add line by line
                            }
                        }



// 👉 TextView lo set cheyyi
                        txtCourseType.setText(builder.toString());*/


                        List<CartDetailsResponse.CourseLevels> allLevels = new ArrayList<>();

                        for (CartDetailsResponse.Result r : resultList) {

                            String courseType = r.getCourseType();

                            if (r.getCourseLevels() != null) {

                                for (CartDetailsResponse.CourseLevels level : r.getCourseLevels()) {

                                    if (subscribedIds != null &&
                                            subscribedIds.contains(level.getCourseLevelId())) {
                                        continue; // ❌ skip subscribed
                                    }


                                    level.setCourseType(courseType); // 🔥 attach
                                    allLevels.add(level);
                                }
                            }
                        }
                        // 👉 Merge all levels
                        List<CartDetailsResponse.CourseLevels> allLevels1 = new ArrayList<>();

                        for (CartDetailsResponse.Result r : resultList) {
                            if (r.getCourseLevels() != null) {
                                allLevels1.addAll(r.getCourseLevels());
                            }
                        }

                        if (!allLevels1.isEmpty()) {
                            cartDetalAdapter.setLevels(allLevels);
                            updateCartTotal(allLevels1);
                        } else {
                            cartDetalAdapter.setLevels(new ArrayList<>());
                            textAmount.setText("₹0/-");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<CartDetailsResponse> call, Throwable t) {

            }
        });
    }

    private void updateCartTotal(List<CartDetailsResponse.CourseLevels> levels) {
        int totalAmount = 0;

        for (CartDetailsResponse.CourseLevels level : levels) {
            if (level.getCourseLevelPrice() != null && !level.getCourseLevelPrice().isEmpty()) {
                try {
                    totalAmount += Integer.parseInt(level.getCourseLevelPrice());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        textAmount.setText("₹" + totalAmount + "/-");
    }
    private void recalculateTotal() {
        int total = 0;

        List<CartDetailsResponse.CourseLevels> list =
                cartDetalAdapter.getLevels();

        for (CartDetailsResponse.CourseLevels level : list) {
            if (level.getCourseLevelPrice() != null) {
                try {
                    total += Integer.parseInt(level.getCourseLevelPrice());
                } catch (Exception ignored) {}
            }
        }

        textAmount.setText("₹" + total + "/-");

        if (list.isEmpty()) {
            textAmount.setText("₹0/-");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (workSheetRnm != null) {
            loadCartList(workSheetRnm);
        }
    }

}
