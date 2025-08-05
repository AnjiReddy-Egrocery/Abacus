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
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PaymentActivity extends AppCompatActivity {

    private LinearLayout layoutSelectedLevels,layoutBack;
    private TextView tvTotal;
    private Button btnPayNow;
    private final CartManager cartManager = CartManager.getInstance(this);



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
            ArrayList<String> cartTypes = new ArrayList<>();
            if (!cartManager.getSelectedLevelsByCourse("video").isEmpty()) {
                cartTypes.add("video");
            }
            if (!cartManager.getSelectedLevelsByCourse("live").isEmpty()) {
                cartTypes.add("live");
            }

            if (cartTypes.isEmpty()) {
                Toast.makeText(this, "No items in cart!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(PaymentActivity.this, PaymentOptionsActivity.class);
            intent.putStringArrayListExtra("cartTypes", cartTypes);
            startActivity(intent);
        });

        layoutBack.setOnClickListener(view -> {
            Intent intent = new Intent(PaymentActivity.this, CartActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void showSelectedLevels() {
        layoutSelectedLevels.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        int total = 0;

        // Loop through all types (video, live, worksheet)
        Set<String> allTypes = cartManager.getAllTypes();

        for (String cartType : allTypes) {
            Map<String, List<String>> courseLevelMap = cartManager.getSelectedLevelsByCourse(cartType);

            if (courseLevelMap.isEmpty()) continue;

            // Section Heading (Video Tutorials, Live Classes, Worksheets)
            TextView typeHeading = new TextView(this);
            typeHeading.setText(getTypeHeading(cartType));
            typeHeading.setTextSize(20);
            typeHeading.setPadding(0, 24, 0, 16);
            layoutSelectedLevels.addView(typeHeading);

            for (Map.Entry<String, List<String>> entry : courseLevelMap.entrySet()) {
                String courseName = entry.getKey();
                List<String> levels = entry.getValue();

                // Course Name
                TextView courseTitle = new TextView(this);
                courseTitle.setText(courseName);
                courseTitle.setTextSize(18);
                courseTitle.setPadding(16, 16, 16, 8);
                layoutSelectedLevels.addView(courseTitle);

                for (String level : levels) {
                    View row = inflater.inflate(R.layout.item_level_payment, layoutSelectedLevels, false);
                    TextView tv = row.findViewById(R.id.tvLevelText);
                    CheckBox cb = row.findViewById(R.id.checkboxLevel);

                    cb.setVisibility(View.GONE); // Hide checkbox in Payment page
                    tv.setText(level);

                    int price = extractPrice(level);
                    total += price;

                    layoutSelectedLevels.addView(row);
                }
            }
        }

        tvTotal.setText("Total Amount: ₹" + total);
    }

    private int extractPrice(String levelText) {
        try {
            String num = levelText.substring(levelText.indexOf("₹") + 1).trim();
            return Integer.parseInt(num);
        } catch (Exception e) {
            return 0;
        }
    }

    private String getTypeHeading(String type) {
        switch (type) {
            case "video":
                return "Video Tutorials";
            case "live":
                return "Worksheets";

            default:
                return "Other Items";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 123) {
            if (data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("success")) {
                    Toast.makeText(this, "Payment Successful", Toast.LENGTH_SHORT).show();

                    cartManager.clearAll(); // Clear entire cart after payment

                    Intent intent = new Intent(PaymentActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No response from payment app", Toast.LENGTH_SHORT).show();
            }
        }
    }
}