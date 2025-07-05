package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;

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
            cartManager.clear(); // optionally clear after payment
            finish(); // or go to a thank-you page
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

}