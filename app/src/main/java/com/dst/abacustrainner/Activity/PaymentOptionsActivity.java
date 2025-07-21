package com.dst.abacustrainner.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.dst.abacustrainner.R;

public class PaymentOptionsActivity extends AppCompatActivity {

    Button btnDebitCredit, btnGPay, btnPhonePe, btnPaytm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);

        btnDebitCredit = findViewById(R.id.btnDebitCredit);
        btnGPay = findViewById(R.id.btnGPay);
        btnPhonePe = findViewById(R.id.btnPhonePe);
        btnPaytm = findViewById(R.id.btnPaytm);

        btnDebitCredit.setOnClickListener(v -> {
            Toast.makeText(this, "Card Payment selected", Toast.LENGTH_SHORT).show();
            onPaymentSuccess();
        });

        btnGPay.setOnClickListener(v -> {
            launchUpiIntent("com.google.android.apps.nbu.paisa.user");
        });

        btnPhonePe.setOnClickListener(v -> {
            launchUpiIntent("com.phonepe.app");
        });

        btnPaytm.setOnClickListener(v -> {
            launchUpiIntent("net.one97.paytm");
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
        savePurchasedData();
        Intent intent = new Intent(PaymentOptionsActivity.this, DashboardActivity.class);
        intent.putExtra("openHome", true);
        startActivity(intent);
        finish();
    }

    private void savePurchasedData() {
        SharedPreferences prefs = getSharedPreferences("purchases", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("AbacusJunior", "Level 1, Level 2");
        editor.putString("VedicMaths", "Level 1");
        editor.putString("MentalArithmetic", "Level A");

        editor.apply();
    }
}
