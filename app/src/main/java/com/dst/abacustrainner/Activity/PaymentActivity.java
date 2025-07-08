package com.dst.abacustrainner.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout layoutSelectedLevels,layoutBack;
    private TextView tvTotal;
    private Button btnPayNow;
    private final CartManager cartManager = CartManager.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        layoutSelectedLevels = findViewById(R.id.layoutSelectedLevels);
        tvTotal = findViewById(R.id.tvTotal);
        btnPayNow = findViewById(R.id.btnPayNow);
        layoutBack = findViewById(R.id.layout_payment_back);

        showSelectedLevels();

        btnPayNow.setOnClickListener(v -> {
            // Handle payment logic here
          Intent intent = new Intent(PaymentActivity.this,PaymentOptionsActivity.class);
          startActivity(intent);
        });

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void showSelectedLevels() {
        layoutSelectedLevels.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        int total = 0;
        for (String level : new ArrayList<>(cartManager.getSelectedLevels())) {
            View row = inflater.inflate(R.layout.item_level_row, layoutSelectedLevels, false);
            TextView tv = row.findViewById(R.id.tvLevelText);
            CheckBox cb = row.findViewById(R.id.checkboxLevel);

            cb.setVisibility(View.GONE); // hide checkbox
            tv.setText(level);

            int price = 0;
            try {
                price = Integer.parseInt(level.substring(level.indexOf("₹") + 1).trim());
            } catch (Exception ignored) {}
            total += price;

            layoutSelectedLevels.addView(row);
        }

        tvTotal.setText("Total Amount: ₹" + total);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            if (data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("success")) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

                    cartManager.clear();

                    Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("go_to_home", true); // optional if needed to control navigation
                    startActivity(intent);
                    finish(); // close current screen
                } else {
                    Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No response from UPI app", Toast.LENGTH_SHORT).show();
            }
        }
    }

}