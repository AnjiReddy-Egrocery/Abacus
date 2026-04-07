package com.dst.abacustrainner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.Model.CartDetailsResponse;
import com.dst.abacustrainner.Model.MetaInfo;
import com.dst.abacustrainner.Model.OrderCreateResponse;
import com.dst.abacustrainner.Model.OrderRequest;
import com.dst.abacustrainner.Model.OrderResponse;
import com.dst.abacustrainner.Model.OrderStatusResponse;
import com.dst.abacustrainner.Model.PaymentFlow;
import com.dst.abacustrainner.Model.PaymentRefrence;
import com.dst.abacustrainner.Model.TokenResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.ApiClient;
import com.dst.abacustrainner.Services.PhonePeApi;
import com.dst.abacustrainner.Services.RetrofitClient;
import com.phonepe.intent.sdk.api.PhonePeKt;
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment;


import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout layoutSelectedLevel;

    ImageView  layoutBack;
    private TextView tvTotal;
    private Button btnPayNow;

    private String studentId, workRnm, totalAmount, merchantref;
    private ArrayList<String> levelNames, levelPrices, cartIds;

    private String accessTokenGlobal, orderIdGlobal, orderTokenGlobal;
    private String merchantOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        receiveIntentData();
        setLevelsUI();
        setTotalAmount();
        clickListeners();

        boolean initResult = PhonePeKt.init(this, "M23EB6GY8RWOK", "flowId", PhonePeEnvironment.RELEASE, false, null);
        Log.d("Anji", initResult ? "✅ PhonePe SDK initialized successfully" : "❌ SDK initialization failed");
    }

    private void initViews() {
        layoutSelectedLevel = findViewById(R.id.layoutSelectedLevels);
        tvTotal = findViewById(R.id.tvTotal);
        btnPayNow = findViewById(R.id.btnPayNow);
        layoutBack = findViewById(R.id.layout_payment_back);
    }

    private void receiveIntentData() {
        studentId = getIntent().getStringExtra("StudentId");
        workRnm = getIntent().getStringExtra("WorkRNM");
        totalAmount = getIntent().getStringExtra("TotalAmount");
        levelNames = getIntent().getStringArrayListExtra("LevelNames");
        levelPrices = getIntent().getStringArrayListExtra("LevelPrices");
        cartIds = getIntent().getStringArrayListExtra("CartIds");
        merchantref = getIntent().getStringExtra("merchantRef");

        Log.d("Anji", "Intent Data -> studentId: " + studentId + ", workRnm: " + workRnm + ", totalAmount: " + totalAmount + ", MerchantRef: " + merchantref);
        Log.d("Anji", "LevelNames: " + levelNames);
        Log.d("Anji", "LevelPrices: " + levelPrices);
    }

    private void setLevelsUI() {
        if (levelNames == null || levelPrices == null) {
            Toast.makeText(this, "No levels selected", Toast.LENGTH_SHORT).show();
            return;
        }
        layoutSelectedLevel.removeAllViews();
        for (int i = 0; i < levelNames.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.row_selected_level, layoutSelectedLevel, false);
            TextView tvLevelName = view.findViewById(R.id.tvLevelName);
            TextView tvLevelPrice = view.findViewById(R.id.tvLevelPrice);
            tvLevelName.setText(levelNames.get(i));
            tvLevelPrice.setText("₹" + levelPrices.get(i));
            layoutSelectedLevel.addView(view);
        }
    }

    private void setTotalAmount() {
        tvTotal.setText(totalAmount != null ? totalAmount : "₹0");
        Log.d("Anji", "Total Amount set: " + tvTotal.getText());
    }

    private void clickListeners() {
        layoutBack.setOnClickListener(v -> finish());
        btnPayNow.setOnClickListener(v -> {
            Log.d("Anji", "Pay Now clicked");
            generateToken();
        });
    }

    private void generateToken() {
        Log.d("Anji", "Generating Token...");
        PhonePeApi api = RetrofitClient.getClient().create(PhonePeApi.class);
        Call<TokenResponse> call = api.generateToken(
                "SU2512311150282994206185",
                "1",
                "6108bcdf-e9e8-4d7f-9c8f-12724cd06134",
                "client_credentials"
        );

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessTokenGlobal = response.body().getAccessToken();
                    Log.d("Anji", "Token received: " + accessTokenGlobal);
                    createOrder(accessTokenGlobal);

                } else {
                    Log.e("Anji", "Token API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("Anji", "Token API Failure: " + t.getMessage());
            }
        });
    }

    private void createOrder(String accessToken) {
        Log.d("Anji", "Creating Order...");

        int amountInPaisa;

        try {
            double rupees = Double.parseDouble(totalAmount);
            amountInPaisa = (int) (rupees * 100);
        } catch (Exception e) {
            Log.e("Anji", "Amount conversion error: " + e.getMessage());
            return;
        }

        // ✅ IMPORTANT VALIDATIONS
        if (amountInPaisa < 100) {
            Log.e("Anji", "❌ Amount must be >= 100 paisa");
            return;
        }

        long expireAfter = 3600; // ✅ must be between 300–3600

        merchantOrderId = "TX" + System.currentTimeMillis();

        Log.d("Anji", "OrderId: " + merchantOrderId);
        Log.d("Anji", "Amount (paisa): " + amountInPaisa);
        Log.d("Anji", "ExpireAfter: " + expireAfter);

        // ✅ CREATE REQUEST
        OrderRequest request = new OrderRequest(
                merchantOrderId,
                amountInPaisa,
                expireAfter, // 👈 MUST PASS
                new MetaInfo("StudentPayment", "Abacus"),
                new PaymentFlow("PG_CHECKOUT")
        );

        PhonePeApi api = RetrofitClient.getClient().create(PhonePeApi.class);

        Call<OrderResponse> call = api.createOrder(
                "O-Bearer " + accessToken,
                request
        );

        call.enqueue(new Callback<OrderResponse>() {

            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {

                Log.d("Anji", "Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {

                    String orderId = response.body().getOrderId();
                    String token = response.body().getToken();

                    Log.d("Anji", "✅ ORDER CREATED SUCCESS");
                    Log.d("Anji", "OrderId: " + orderId);
                    Log.d("Anji", "Token: " + token);

                    // 👉 Next Step
                    startPhonePePayment(orderId, token);

                } else {

                    Log.e("Anji", "❌ CreateOrder Failed");

                    try {
                        String error = response.errorBody() != null
                                ? response.errorBody().string()
                                : "No error body";

                        Log.e("Anji", "❌ Error Body: " + error);

                    } catch (Exception e) {
                        Log.e("Anji", "Error parsing errorBody: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e("Anji", "❌ API Failure: " + t.getMessage());
            }
        });
    }
    private void startPhonePePayment(String orderId, String orderToken) {
        if (orderToken == null || orderToken.isEmpty()) return;
        try {
            Log.d("Anji", "Starting PhonePe Checkout -> orderId: " + orderId);
            PhonePeKt.startCheckoutPage(this, orderToken, orderId, activityResultLauncher);
        } catch (Exception e) {
            Log.e("API_RESPONSE", "Checkout Start Error: " + e.getMessage());
        }
    }

    private void checkOrderStatusWithRetry(String merchantOrderId, int attempt) {
        if (attempt > 4) return;
        if (accessTokenGlobal == null || accessTokenGlobal.isEmpty()) return;

        Log.d("Anji", "Checking Order Status attempt " + attempt + " for orderId: " + merchantOrderId);
        PhonePeApi api = RetrofitClient.getClient().create(PhonePeApi.class);
        Call<OrderStatusResponse> call = api.checkOrderStatus(
                "O-Bearer " + accessTokenGlobal,
                merchantOrderId,
                false, // safer default first
                false
        );

        call.enqueue(new Callback<OrderStatusResponse>() {
            @Override
            public void onResponse(Call<OrderStatusResponse> call, Response<OrderStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    logPaymentDetails(response.body());
                } else if (response.code() == 400 || response.code() == 401) {
                    Log.d("Anji", "Order Status API retryable error: " + response.code());
                    new Handler(Looper.getMainLooper()).postDelayed(() ->
                            checkOrderStatusWithRetry(merchantOrderId, attempt + 1), 3000);
                } else {
                    Log.e("Anji", "Status API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderStatusResponse> call, Throwable t) {
                Log.e("Anji", "Status API Failure: " + t.getMessage());
            }
        });
    }

    private void logPaymentDetails(OrderStatusResponse status) {
        String orderState = status.getState() != null ? status.getState().toUpperCase() : "UNKNOWN";
        if ("COMPLETED".equals(orderState) || "SUCCESS".equals(orderState)) {
            Log.d("Anji", "✅ Payment Success");
            // continue success flow
        }

// ❌ FAILED → OPEN FAILURE SCREEN
        else if ("FAILED".equals(orderState)) {

            Log.d("Anji", "❌ Payment Failed");

            String errorCode = status.getErrorContext() != null &&
                    status.getErrorContext().getErrorCode() != null
                    ? status.getErrorContext().getErrorCode()
                    : "PAYMENT_FAILED";

            Intent intent = new Intent(PaymentActivity.this, PaymentDetailsActivity.class);
            intent.putExtra("StudentId", studentId);
            intent.putExtra("Status", orderState);
            intent.putExtra("ErrorCode", errorCode);
            intent.putExtra("Amount", totalAmount);
            intent.putExtra("Currency", "INR");

            startActivity(intent);
            return; // ✅ STOP HERE
        }

// ⛔ EXPIRED → RETRY
        else if ("EXPIRED".equals(orderState)) {

            Log.d("Anji", "⛔ Order Expired → Retrying");

            Toast.makeText(this, "Session expired. Retrying...", Toast.LENGTH_SHORT).show();

            createOrder(accessTokenGlobal); // 🔁 NEW ORDER

            return;
        }

// ⏳ PENDING
        else if ("PENDING".equals(orderState)) {
            Log.d("Anji", "⏳ Payment still pending...");
            return;
        }

        if (status.getPaymentDetails() == null || status.getPaymentDetails().isEmpty()) {
            Log.d("Anji", "No payment attempts yet.");
            return;
        }

        for (OrderStatusResponse.PaymentDetail detail : status.getPaymentDetails()) {
            String worksheetRnm = workRnm != null ? workRnm : "UNKNOWN";
            String studentID = studentId != null ? studentId : "UNKNOWN";
            String totalAmt = totalAmount != null ? totalAmount : "0";

            String transactionId = detail.getTransactionId() != null ? detail.getTransactionId() : "UNKNOWN";
            String state = detail.getState() != null ? detail.getState().toUpperCase() : "UNKNOWN";
            String currency = "INR";
            long amount = detail.getAmount();
            String orderId = status.getOrderId() != null ? status.getOrderId() : "UNKNOWN";
            String paymentMode = detail.getPaymentMode() != null ? detail.getPaymentMode() : "UNKNOWN";
            long timestamp = detail.getTimestamp();

            Log.d("Anji", "✅ Payment Successful Details:");
            Log.d("Anji", "WorksheetRnm: " + worksheetRnm);
            Log.d("Anji", "StudentId: " + studentID);
            Log.d("Anji", "TotalAmount: " + totalAmt);
            Log.d("Anji", "TransactionId: " + transactionId);
            Log.d("Anji", "State: " + state);
            Log.d("Anji", "Currency: " + currency);
            Log.d("Anji", "Amount: " + amount);
            Log.d("Anji", "OrderId: " + orderId);
            Log.d("Anji", "PaymentMode: " + paymentMode);
            Log.d("Anji", "Timestamp: " + timestamp);

            showPaymentSuccessDialog(worksheetRnm, studentID, totalAmt, transactionId, state, currency, amount, orderId, paymentMode, timestamp);
        }
    }

    private void showPaymentSuccessDialog(String worksheetRnm, String studentId, String totalAmount,
                                          String transactionId, String state, String currency,
                                          long amount, String orderId, String paymentMode, long timestamp) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.abacustrainer.com/") // Replace with your API URL
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        ApiClient apiClient = retrofit.create(ApiClient.class);
        RequestBody WorksheetPart = RequestBody.create(MediaType.parse("text/plain"), worksheetRnm);
        RequestBody StudentPartPart = RequestBody.create(MediaType.parse("text/plain"), studentId);
        RequestBody AmountPart = RequestBody.create(MediaType.parse("text/plain"), totalAmount);
        RequestBody TransctionPartPart = RequestBody.create(MediaType.parse("text/plain"), transactionId);
        RequestBody statePart = RequestBody.create(MediaType.parse("text/plain"), state);
        RequestBody currencyPart = RequestBody.create(MediaType.parse("text/plain"), currency);
        RequestBody amountPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(amount));
        RequestBody orderPart = RequestBody.create(MediaType.parse("text/plain"), orderId);
        RequestBody paymentModePart = RequestBody.create(MediaType.parse("text/plain"), paymentMode);
        RequestBody timeStampPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(timestamp));

        Call<PaymentRefrence> call = apiClient.getPaymentInfo(WorksheetPart,StudentPartPart,AmountPart,TransctionPartPart,statePart,currencyPart,amountPart,orderPart,paymentModePart,timeStampPart);
        call.enqueue(new Callback<PaymentRefrence>() {
            @Override
            public void onResponse(Call<PaymentRefrence> call, Response<PaymentRefrence> response) {
                if (response.body() != null &&
                        "200".equals(response.body().getErrorCode())) {

                    PaymentRefrence.Result result = response.body().getResult();

                    String transction = result.getTransactionID();
                    String status = result.getState();
                    String currency = result.getCurrency();
                    String Amount = result.getAmount();
                    String paymentMethod = result.getPaymentMethod();
                    String created = result.getDateCreated();
                    Intent intent = new Intent(PaymentActivity.this, PaymentDetailsActivity.class);
                    intent.putExtra("StudentId",studentId);
                    intent.putExtra("Transaction",transction);
                    intent.putExtra("Status",status);
                    intent.putExtra("Currency",currency);
                    intent.putExtra("Amount",Amount);
                    intent.putExtra("Payment",paymentMethod);
                    intent.putExtra("Date",created);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<PaymentRefrence> call, Throwable t) {

            }
        });
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        Log.d("Anji", "Payment SDK callback received");

                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            String response = result.getData().getStringExtra("response");
                            Log.d("Anji", "PhonePe SDK Response: " + response);

                            // Always verify payment from server
                            checkOrderStatusWithRetry(merchantOrderId, 1);

                        } else {

                            Log.d("Anji", "⚠ Payment cancelled by user");

                            checkOrderStatusWithRetry(merchantOrderId, 1);
                        }
                    });
}
