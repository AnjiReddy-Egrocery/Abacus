package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;

import java.util.List;
import java.util.Map;

public class PurchasedActivity extends AppCompatActivity {

    private LinearLayout layoutPurchasedLevels,layoutBack;
    private CartManager cartManager;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchased);


        layoutPurchasedLevels = findViewById(R.id.layoutPurchasedLevels);
        layoutBack = findViewById(R.id.fragment_container);
        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        cartManager = CartManager.getInstance(getApplicationContext());

        displayPurchasedLevels();
    }
    @SuppressLint("MissingInflatedId")
    private void displayPurchasedLevels() {
        LayoutInflater inflater = LayoutInflater.from(this);

        Map<String, List<String>> data = cartManager.getSelectedLevelsByCourse();
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            String courseName = entry.getKey();
            List<String> levels = entry.getValue();

            // Optionally add a title for course
            TextView courseTitle = new TextView(this);
            courseTitle.setText("Course: " + courseName);
            courseTitle.setTextSize(18f);
            courseTitle.setPadding(8, 16, 8, 8);
            layoutPurchasedLevels.addView(courseTitle);

            for (String level : levels) {
                View row = inflater.inflate(R.layout.item_level_purchased, layoutPurchasedLevels, false);
                //CheckBox cb = row.findViewById(R.id.checkboxLevel);
                TextView tv = row.findViewById(R.id.tvLevelText);

                tv.setText(level);

                row.setOnClickListener(v -> {
                    Intent intent = new Intent(PurchasedActivity.this, LevelTopicActivity.class);
                    intent.putExtra("level_name", level);
                    startActivity(intent);
                });

                //cb.setChecked(true);
               // cb.setEnabled(false);

                layoutPurchasedLevels.addView(row);
            }

            if (levels.isEmpty()) {
                Toast.makeText(this, "No purchased levels found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}