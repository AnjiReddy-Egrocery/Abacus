package com.dst.abacustrainner.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentOptionsActivity extends AppCompatActivity {

    Button btnDebitCredit, btnGPay, btnPhonePe, btnPaytm;

    LinearLayout layoutBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);

        btnDebitCredit = findViewById(R.id.btnDebitCredit);
        btnGPay = findViewById(R.id.btnGPay);
        btnPhonePe = findViewById(R.id.btnPhonePe);
        btnPaytm = findViewById(R.id.btnPaytm);
        layoutBack = findViewById(R.id.fragment_container);


        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnDebitCredit.setOnClickListener(v -> {
            Toast.makeText(this, "Card Payment selected", Toast.LENGTH_SHORT).show();
            onPaymentSuccess();
        });

        btnGPay.setOnClickListener(v -> {
            //launchUpiIntent("com.google.android.apps.nbu.paisa.user");
            Toast.makeText(this, "GPay Payment selected", Toast.LENGTH_SHORT).show();
            onPaymentSuccess();
        });

        btnPhonePe.setOnClickListener(v -> {
            //launchUpiIntent("com.phonepe.app");
            Toast.makeText(this, "PhonePe Payment selected", Toast.LENGTH_SHORT).show();
            onPaymentSuccess();
        });

        btnPaytm.setOnClickListener(v -> {
            //launchUpiIntent("net.one97.paytm");
            Toast.makeText(this, "Paytm Payment selected", Toast.LENGTH_SHORT).show();
            onPaymentSuccess();
        });
    }

    private void launchUpiIntent(String packageName) {
        String upiId = "merchantupi@icici";
        String name = "Your Business Name";
        String note = "Payment for Course";
        String amount = "150.00";

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        intent.setPackage(packageName);

        try {
            startActivityForResult(intent, 123);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "App not installed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123 && data != null) {
            String response = data.getStringExtra("response");

            if (response != null && response.toLowerCase().contains("success")) {
                Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();
                onPaymentSuccess();
            } else {
                Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onPaymentSuccess() {
        SharedPreferences prefs = getSharedPreferences("purchases", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Mark payment as successful (one-time flag)
        editor.putBoolean("payment_success", true);

        // Mark user has purchased something permanently
        editor.putBoolean("has_purchased", true);

        editor.apply();
        // Navigate to Dashboard with intent
        Intent intent = new Intent(PaymentOptionsActivity.this, HomeActivity.class);
        intent.putExtra("openHome", true);
        startActivity(intent);
        finish();
    }

    private void savePurchasedData() {
        SharedPreferences prefs = getSharedPreferences("purchases", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        CartManager cartManager = CartManager.getInstance(this);
        Map<String, List<String>> selectedMap = cartManager.getSelectedLevelsByCourse();

        for (Map.Entry<String, List<String>> entry : selectedMap.entrySet()) {
            String course = entry.getKey();
            List<String> levels = entry.getValue();

            if (!levels.isEmpty()) {
                StringBuilder levelList = new StringBuilder();
                for (String level : levels) {
                    if (levelList.length() > 0) levelList.append(", ");
                    levelList.append(level);
                }
                editor.putString(course, levelList.toString());
            }
        }

        editor.apply();
    }
}
