package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    private LinearLayout layoutCartItems, layoutCartBack;
    private TextView tvTotalAmount;
    private Button btnContinueShopping, btnCheckout;
    private final CartManager cart = CartManager.getInstance(this);

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

        btnContinueShopping.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, CoursesActivity.class);
            startActivity(intent);
        });

        btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
        });

        // Optional: Back button logic (if needed)
        /*
            layoutCartBack.setOnClickListener(view -> {
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        */
    }

    private void setupCartItems() {
        layoutCartItems.removeAllViews();
        int total = 0;

        LayoutInflater inflater = LayoutInflater.from(this);
        Map<String, List<String>> courseLevelMap = cart.getSelectedLevelsByCourse();

        Log.d("CartLevels", "Selected Levels: " + courseLevelMap);

        for (Map.Entry<String, List<String>> entry : courseLevelMap.entrySet()) {
            String courseName = entry.getKey();
            List<String> levels = entry.getValue();

            // Show course name
            TextView tvCourse = new TextView(this);
            tvCourse.setText(courseName);
            tvCourse.setTextSize(18);
            tvCourse.setPadding(0, 16, 0, 8);
            layoutCartItems.addView(tvCourse);

            for (String level : levels) {
                View row = inflater.inflate(R.layout.item_level_cart, layoutCartItems, false);
                CheckBox cb = row.findViewById(R.id.checkboxLevel);
                TextView tv = row.findViewById(R.id.tvLevelText);

                tv.setText(level);
                cb.setOnCheckedChangeListener(null);
                cb.setChecked(true);

                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (!isChecked) {
                        cart.removeLevel(courseName, level);
                        setupCartItems(); // refresh view
                    }
                });

                row.setOnClickListener(v -> cb.setChecked(!cb.isChecked()));
                layoutCartItems.addView(row);
                total += extractPrice(level);
            }
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
