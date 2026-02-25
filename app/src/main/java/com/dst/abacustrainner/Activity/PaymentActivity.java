package com.dst.abacustrainner.Activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.MetaInfo;
import com.dst.abacustrainner.Model.OrderRequest;
import com.dst.abacustrainner.Model.OrderResponse;
import com.dst.abacustrainner.Model.OrderStatusResponse;
import com.dst.abacustrainner.Model.PaymentFlow;
import com.dst.abacustrainner.Model.PaymentParams;
import com.dst.abacustrainner.Model.TokenResponse;
import com.dst.abacustrainner.Model.TokenUtils;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.Services.PhonePeApi;
import com.dst.abacustrainner.Services.RetrofitClient;
import com.google.gson.Gson;
import com.phonepe.intent.sdk.api.PhonePeKt;
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PaymentActivity extends AppCompatActivity {

    private LinearLayout layoutSelectedLevel;
    ImageView layoutBack;
    TextView tvTotal;
    Button btnPayNow;
    String studentId, workRnm, totalAmount;
    ArrayList<String> levelNames, levelPrices, cartIds;

    private String orderIdGlobal;
    private String tokenGlobal;
    private String flowId; // Production flowId


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        initViews();
        receiveIntentData();
        setLevelsUI();
        setTotalAmount();
        clickListeners();

        flowId = "FLOW_PROD_ABC123";

        boolean initResult = PhonePeKt.init(
                this,                         // Activity context
                "M23EB6GY8RWOK",              // Production MID
                flowId,
                PhonePeEnvironment.RELEASE,   // Production environment
                false,                        // Disable logging in production
                null                          // appId (optional)
        );

        if (initResult) {
            Log.d("Anji", "✅ PhonePe SDK initialized successfully with Production FlowId: " + flowId);
        } else {
            Log.e("Anji", "❌ SDK initialization failed. Do not call other SDK methods.");
        }

        Log.d("Anji", "SDK Init Result: " + initResult);





    }
    private void initViews() {
        layoutSelectedLevel = findViewById(R.id.layoutSelectedLevels);
        tvTotal = findViewById(R.id.tvTotal);
        btnPayNow = findViewById(R.id.btnPayNow);
        layoutBack = findViewById(R.id.layout_payment_back);
    }

    private void receiveIntentData() {
        Intent intent = getIntent();

        studentId = intent.getStringExtra("StudentId");
        workRnm = intent.getStringExtra("WorkRNM");
        totalAmount = intent.getStringExtra("TotalAmount");

        levelNames = intent.getStringArrayListExtra("LevelNames");
        levelPrices = intent.getStringArrayListExtra("LevelPrices");
        cartIds = intent.getStringArrayListExtra("CartIds");

        Log.e("Chandu", workRnm);
        Log.e("Chandu", totalAmount);
        Log.e("Chandu", studentId);
        Log.e("Chandu", String.valueOf(levelNames));
        Log.e("Chandu", String.valueOf(levelPrices));


    }
    @SuppressLint("MissingInflatedId")
    private void setLevelsUI() {
        if (levelNames == null || levelPrices == null) {
            Toast.makeText(this, "No levels selected", Toast.LENGTH_SHORT).show();
            return;
        }

        layoutSelectedLevel.removeAllViews();

        for (int i = 0; i < levelNames.size(); i++) {

            View view = LayoutInflater.from(this)
                    .inflate(R.layout.row_selected_level, layoutSelectedLevel, false);

             TextView tvLevelName = view.findViewById(R.id.tvLevelName);
            TextView tvLevelPrice = view.findViewById(R.id.tvLevelPrice);

            tvLevelName.setText(levelNames.get(i));
            tvLevelPrice.setText("₹" + levelPrices.get(i));

            layoutSelectedLevel.addView(view);
        }
    }

    private void setTotalAmount() {
        if (totalAmount != null) {
            tvTotal.setText(totalAmount);
        } else {
            tvTotal.setText("₹0");
        }
    }

    private void clickListeners() {

        layoutBack.setOnClickListener(v -> finish());

        btnPayNow.setOnClickListener(v -> {
            generateToken();
        });
    }

    private void generateToken() {
        Log.d("Anji", "Generate Token button clicked");

        PhonePeApi api = RetrofitClient.getClient().create(PhonePeApi.class);

        Call<TokenResponse> call = api.generateToken(
                "SU2512311150282994206185",       // Production Client ID
                "1",                               // Scope/version
                "6108bcdf-e9e8-4d7f-9c8f-12724cd06134", // Production Client Secret
                "client_credentials"
        );

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                Log.d("Anji", "Token API Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    TokenResponse tokenResponse = response.body();
                    String accessToken = tokenResponse.getAccessToken();
                    String encryptedToken = tokenResponse.getEncryptedAccessToken();

                    tokenGlobal = accessToken; // Keep global access token

                    // Log the tokens
                    Log.d("Anji", "Access Token: " + accessToken);
                    Log.d("Anji", "Encrypted Access Token: " + encryptedToken);

                    //txtAccessToken.setText(accessToken);
                    //txtEncryptedToken.setText(encryptedToken);

                    // Proceed to create order
                    createOrder(accessToken);

                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("Anji", "Token API Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("Anji", "Token parsing failed", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Log.e("Anji", "Token API Failure: " + t.getMessage(), t);
            }
        });
    }

    // ---------------- Order Creation ----------------
    private void createOrder(String accessToken) {
        PhonePeApi api = RetrofitClient.getClient().create(PhonePeApi.class);

        MetaInfo metaInfo = new MetaInfo("Test1", "Test2");


        OrderRequest request = new OrderRequest(
                "TX" + System.currentTimeMillis(), // Unique orderId
                1000,                              // Amount in paise
                1200,                              // Total
                metaInfo,
                new PaymentFlow("PG_CHECKOUT")
        );

        Log.d("Anji", "CreateOrder Request: " + new Gson().toJson(request));

        Call<OrderResponse> call = api.createOrder("O-Bearer " + accessToken, request);

        call.enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderResponse order = response.body();

                    orderIdGlobal = order.getOrderId();
                    tokenGlobal = order.getToken();

                    Log.d("Anji", "CreateOrder Response: " + new Gson().toJson(order));

                    ///txtOrderId.setText("OrderId:\n" + orderIdGlobal);
                   // txtEncryptedToken.setText("Order Token:\n" + tokenGlobal);

                    // ✅ Validate token
                    if (tokenGlobal == null || tokenGlobal.isEmpty()) {
                        Log.e("Anji", "❌ Order token is empty! Cannot start payment.");
                       // txtOrderId.setText("❌ Order Token empty, check credentials / flowId");
                        return;
                    }

                    startPhonePePayment(orderIdGlobal, tokenGlobal);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("Anji", "Order Error Body: " + errorBody);
                      //  txtOrderId.setText("❌ Failed to create order");
                    } catch (Exception e) {
                        Log.e("Anji", "Order parsing failed", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.e("Anji", "Order API Failure: " + t.getMessage());
               // txtOrderId.setText("❌ Failed to create order");
            }
        });
    }

    // ---------------- Start PhonePe Checkout ----------------
    private void startPhonePePayment(String orderId, String token) {
        if (token == null || token.isEmpty()) {
            Log.e("API_RESPONSE", "❌ Order token is empty! Cannot start payment.");
           // txtOrderId.setText("❌ Order Token empty, check credentials / flowId");
            return;
        }

        Log.d("Anji", "Starting PhonePe Checkout");
        Log.d("Anji", "OrderId: " + orderId);
        Log.d("Anji", "Token: " + token);
        Log.d("Anji", "FlowId: " + flowId);

        try {
            PhonePeKt.startCheckoutPage(this, token, orderId, activityResultLauncher);

        } catch (Exception e) {
            Log.e("API_RESPONSE", "Checkout Start Error: " + e.getMessage());
        }
    }

    // ---------------- Payment Status Check ----------------
    // Check Payment Status
    private void checkOrderStatus(String orderId) {
        Log.d("API_RESPONSE", "Checking status for OrderId: " + orderId);

        if (tokenGlobal == null || tokenGlobal.isEmpty()) {
            Log.e("API_RESPONSE", "❌ Access token is empty! Cannot check order status.");
           // txtOrderId.setText("❌ Access token empty");
            return;
        }

        PhonePeApi api = RetrofitClient.getClient().create(PhonePeApi.class);

        // Query parameters: details=true, errorContext=true
        Call<OrderStatusResponse> call = api.checkOrderStatus(
                "O-Bearer " + tokenGlobal,
                orderId,
                true,   // details
                true    // errorContext
        );

        call.enqueue(new Callback<OrderStatusResponse>() {
            @Override
            public void onResponse(Call<OrderStatusResponse> call, Response<OrderStatusResponse> response) {
                Log.d("API_RESPONSE", "Status Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    OrderStatusResponse status = response.body();
                    Log.d("API_RESPONSE", "Full Order Status Response: " + new Gson().toJson(status));

                    String state = status.getState() != null ? status.getState().toUpperCase() : "UNKNOWN";
                    Log.d("API_RESPONSE", "Payment State: " + state);

                    switch (state) {
                        case "COMPLETED":
                            //txtOrderId.setText("✅ Payment Success");
                            logPaymentDetails(status);
                            break;

                        case "FAILED":
                            //txtOrderId.setText("❌ Payment Failed");
                            if (status.getErrorContext() != null) {
                                Log.e("Anji", "Error Code: " + status.getErrorContext().getErrorCode());
                                Log.e("Anji", "Error Description: " + status.getErrorContext().getDescription());
                            }
                            break;

                        case "PENDING":
                           // txtOrderId.setText("⏳ Payment Pending");
                            break;

                        default:
                            //txtOrderId.setText("⚠️ Unknown Payment State");
                            Log.w("Anji", "Unknown payment state: " + state);
                            break;
                    }

                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                        Log.e("Anji", "Status Error Body: " + errorBody);

                        if (errorBody.contains("MERCHANT_ORDER_MAPPING_NOT_FOUND")) {
                            //txtOrderId.setText("❌ Invalid Order ID");
                            Log.e("Anji", "Invalid Order ID: " + orderId);
                        } else {
                           // txtOrderId.setText("❌ Error checking order status");
                        }
                    } catch (Exception e) {
                        Log.e("Anji", "Status Error parsing failed", e);
                        //txtOrderId.setText("❌ Status parsing failed");
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderStatusResponse> call, Throwable t) {
                Log.e("Anji", "Status API Failure: " + t.getMessage());
                //txtOrderId.setText("❌ Failed to check status");
            }
        });
    }

    // Helper method to log detailed payment info
    private void logPaymentDetails(OrderStatusResponse status) {
        if (status.getPaymentDetails() == null || status.getPaymentDetails().isEmpty()) return;

        for (OrderStatusResponse.PaymentDetail detail : status.getPaymentDetails()) {
            Log.d("Anji", "Payment Mode: " + detail.getPaymentMode());
            Log.d("Anji", "Amount: " + detail.getAmount());
            Log.d("Anji", "TransactionId: " + detail.getTransactionId());

            if (detail.getRail() != null) {
                Log.d("Anji", "Rail Type: " + detail.getRail().getType());
                Log.d("Anji", "UPI TransactionId: " + detail.getRail().getUpiTransactionId());
                Log.d("Anji", "VPA: " + detail.getRail().getVpa());
            }

            if (detail.getInstrument() != null) {
                Log.d("Anji", "Instrument Type: " + detail.getInstrument().getType());
                Log.d("Anji", "Masked Account: " + detail.getInstrument().getMaskedAccountNumber());
            }

            if (detail.getSplitInstruments() != null) {
                for (OrderStatusResponse.SplitInstrument split : detail.getSplitInstruments()) {
                    Log.d("Anji", "Split Amount: " + split.getAmount());
                    Log.d("Anji", "Split Instrument Type: " + split.getInstrument().getType());
                    Log.d("Anji", "Split UPI: " + split.getRail().getUpiTransactionId());
                }
            }
        }
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Log.d("Anji", "Payment Result Received");

                        if (result.getData() != null) {
                            String response = result.getData().getStringExtra("response");
                            Log.d("Anji", "Checkout Response: " + response);
                        } else {
                            Log.d("Anji", "Checkout Response is null");
                        }

                        if (orderIdGlobal != null) {
                            Log.d("Anji", "Checking order status for: " + orderIdGlobal);
                            checkOrderStatus(orderIdGlobal);
                        } else {
                            Log.e("Anji", "Order ID is null, cannot check status");
                        }
                    }
            );

}


