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
import java.util.Collections;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private LinearLayout layoutCartItems,layoutCartBack;
    private TextView tvTotalAmount;
    private Button btnContinueShopping, btnCheckout;
    private final CartManager cart = CartManager.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        layoutCartItems = findViewById(R.id.layoutCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnContinueShopping = findViewById(R.id.btnContinueShopping);
        btnCheckout = findViewById(R.id.btnCheckout);
        layoutCartBack = findViewById(R.id.layout_cart_back);

        setupCartItems();

        btnContinueShopping.setOnClickListener(v ->
                finish()
        );

        btnCheckout.setOnClickListener(v ->
                startActivity(new Intent(this, PaymentActivity.class))
        );
        layoutCartBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, CourseDetailActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupCartItems() {
        layoutCartItems.removeAllViews();
        int total = 0;

        LayoutInflater inflater = LayoutInflater.from(this);

        List<String> levels = new ArrayList<>(cart.getSelectedLevels());
        Collections.reverse(levels); // Show newest first

        for (String level : levels) {
            View row = inflater.inflate(R.layout.item_level_row, layoutCartItems, false);
            CheckBox cb = row.findViewById(R.id.checkboxLevel);
            TextView tv = row.findViewById(R.id.tvLevelText);

            cb.setChecked(true);
            tv.setText(level);

            int price = extractPrice(level);
            total += price;

            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    cart.removeLevel(level);
                    setupCartItems();
                }
            });

            layoutCartItems.addView(row);
        }

        tvTotalAmount.setText("Total: ₹" + total);
    }

    private int extractPrice(String levelText) {
        try {
            String num = levelText.substring(levelText.indexOf("₹") + 1).trim();
            return Integer.parseInt(num);
        } catch (Exception e) {
            return 0;
        }
    }
}