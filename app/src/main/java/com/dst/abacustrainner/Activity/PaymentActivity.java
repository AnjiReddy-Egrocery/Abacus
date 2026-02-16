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
import com.dst.abacustrainner.Model.PaymentParams;
import com.dst.abacustrainner.Model.TokenUtils;
import com.dst.abacustrainner.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PaymentActivity extends AppCompatActivity {

    private LinearLayout layoutSelectedLevel;
    ImageView layoutBack;
    TextView tvTotal;
    Button btnPayNow;
    String studentId, workRnm, totalAmount;
    ArrayList<String> levelNames, levelPrices, cartIds;

    private static final String CLIENT_ID = "M23EB6GY8RWOK_2601021928";
    private static final String CLIENT_SECRET = "ZjBlZDlhNzQtOWRjOC00YzQzLWJkM2ItNDRiMTY0YTExMGZh";
    private static final int PHONEPE_REQUEST_CODE = 4321;


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
            String orderId = "SU2512311150282994206185";
            String orderToken = "6108bcdf-e9e8-4d7f-9c8f-12724cd06134";

            startPhonePePayment();
        });
    }

    private void startPhonePePayment() {
        JSONObject orderPayload = PaymentParams.getOrderData();
        String orderString = orderPayload.toString();

        // Generate Token (Testing only)
        String token = TokenUtils.generateToken(CLIENT_ID, CLIENT_SECRET, orderString);

        Intent intent = new Intent();
        intent.setAction("com.phonepe.intent.action.PAYMENT");
        intent.putExtra("clientId", CLIENT_ID);
        intent.putExtra("orderData", orderString);
        intent.putExtra("token", token);
        intent.putExtra("mode", "TEST"); // important for sandbox
        intent.putExtra("redirectUrl", "phonepe://callback");

        // Use this for testing: check if PhonePe app exists first
        if (isPhonePeInstalled()) {
            intent.setPackage("com.phonepe.app"); // or "com.phonepe.sandbox" if using sandbox APK
            try {
                startActivityForResult(intent, PHONEPE_REQUEST_CODE);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "PhonePe app not found to handle payment.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "PhonePe app is not installed!", Toast.LENGTH_LONG).show();
            // Optional: redirect user to Play Store
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("market://details?id=com.phonepe.app"));
            startActivity(goToMarket);
        }
    }

    // Helper method to check if PhonePe app is installed
    private boolean isPhonePeInstalled() {
        try {
            getPackageManager().getPackageInfo("com.phonepe.app", 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}