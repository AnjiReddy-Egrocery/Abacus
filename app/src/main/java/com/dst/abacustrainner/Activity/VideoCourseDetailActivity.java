package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;

import java.util.HashMap;
import java.util.Map;

public class VideoCourseDetailActivity extends AppCompatActivity {
    private TextView tvCourseTitle1, tvCartCount;
    private CheckBox cbSelectAll1;
    private LinearLayout layoutLevels1,layoutCourseDetailBack;
    private Button btnpurchasemore1, btnCart;
    private ImageView ivCart;

    private String courseName;
    private String[] currentLevels;

    private final CartManager cartManager = CartManager.getInstance(this);

    private CompoundButton.OnCheckedChangeListener selectAllListener;
    private final Map<String, String> levelDescriptions = new HashMap<>();
    RelativeLayout layoutCart;

    @SuppressLint("MissingInflatedId")
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_course_detail);

        tvCourseTitle1 = findViewById(R.id.tvCourseTitle1);
        cbSelectAll1 = findViewById(R.id.cbSelectAll1);
        layoutLevels1 = findViewById(R.id.layoutLevels1);
        btnpurchasemore1 = findViewById(R.id.btnPurchaseMore1);
        btnCart = findViewById(R.id.cart);
        tvCartCount = findViewById(R.id.tvCartCount);
        ivCart = findViewById(R.id.ivCart);
        layoutCourseDetailBack = findViewById(R.id.layout_coursedetail_back);
        layoutCart = findViewById(R.id.layout_cart);

        courseName = getIntent().getStringExtra("course_name");

        if (courseName != null) {
            switch (courseName) {
                case "Abacus Junior":
                    currentLevels = new String[]{
                            "Junior_Level_1 - ₹90", "Junior_Level_2 - ₹50", "Junior_Level_3 - ₹50",
                            "Junior_Level_Level 4 - ₹50", "Junior_Level_Level 5 - ₹50", "Junior_Level_Level 6 - ₹50"
                    };
                    break;

                case "Abacus Senior":
                    currentLevels = new String[]{
                            "Senior_Level_1 - ₹70", "Senior_Level_2 - ₹70", "Senior_Level_3 - ₹70",
                            "Senior_Level_4 - ₹70", "Senior_Level_5 - ₹70", "Senior_Level_6 - ₹70",
                            "Senior_Level_7 - ₹70", "Senior_Level_8 - ₹70", "Senior_Level_9 - ₹70", "Senior_Level_10 - ₹70"
                    };
                    break;

                case "Vedic Maths":
                    currentLevels = new String[]{
                            "Vedic_Level_1 - ₹100", "Vedic_Level_2 - ₹100", "Vedic_Level_3 - ₹100", "Vedic_Level_4 - ₹100"
                    };
                    break;

                default:
                    currentLevels = new String[]{};
            }
            setupLevelDescriptions();
            showCourseLevels();
        }

        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CartManager.getInstance(VideoCourseDetailActivity.this).getAllSelectedLevels().isEmpty()) {
                    Toast.makeText(VideoCourseDetailActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(VideoCourseDetailActivity.this, CartActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnpurchasemore1.setOnClickListener(v -> {
            finish();
        });

        btnCart.setOnClickListener(v -> {
            if (CartManager.getInstance(this).getAllSelectedLevels().isEmpty()) {
                Toast.makeText(VideoCourseDetailActivity.this, "Cart is empty", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(VideoCourseDetailActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });


        layoutCourseDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoCourseDetailActivity.this, VideoCoursesActivity.class);
                startActivity(intent);

            }
        });



        selectAllListener = (buttonView, isChecked) -> {
            for (int i = 0; i < layoutLevels1.getChildCount(); i++) {
                View row = layoutLevels1.getChildAt(i);
                CheckBox cb = row.findViewById(R.id.checkboxLevel);
                cb.setOnCheckedChangeListener(null);
                cb.setChecked(isChecked);

                String levelText = ((TextView) row.findViewById(R.id.tvLevelText)).getText().toString();
                if (isChecked) {
                    cartManager.addLevel(courseName, levelText);
                } else {
                    cartManager.removeLevel(courseName,levelText);
                }

                cb.setOnCheckedChangeListener(getLevelCheckboxListener(levelText, cb));
            }
            updateCartCount();
        };

        cbSelectAll1.setOnCheckedChangeListener(selectAllListener);
    }

    private void setupLevelDescriptions() {
        switch (courseName) {
            case "Abacus Junior":
                levelDescriptions.put("Junior_Level_1 - ₹50", "Introduction to basic counting and bead movement.");
                levelDescriptions.put("Junior_Level_2 - ₹50", "Addition and subtraction using the abacus.");
                levelDescriptions.put("Junior_Level_3 - ₹50", "Intermediate level exercises.");
                levelDescriptions.put("Junior_Level_Level 4 - ₹50", "Complex mental math patterns.");
                levelDescriptions.put("Junior_Level_Level 5 - ₹50", "Speed and accuracy enhancement.");
                levelDescriptions.put("Junior_Level_Level 6 - ₹50", "Mastery of junior level concepts.");
                break;

            case "Abacus Senior":
                levelDescriptions.put("Senior_Level_1 - ₹70", "Introduction to advanced abacus techniques.");
                levelDescriptions.put("Senior_Level_2 - ₹70", "Working with multiple-digit numbers.");
                levelDescriptions.put("Senior_Level_3 - ₹70", "Speed training and advanced patterns.");
                levelDescriptions.put("Senior_Level_4 - ₹70", "Introduction to advanced abacus techniques.");
                levelDescriptions.put("Senior_Level_5 - ₹70", "Working with multiple-digit numbers.");
                levelDescriptions.put("Senior_Level_6 - ₹70", "Speed training and advanced patterns.");
                levelDescriptions.put("Senior_Level_7 - ₹70", "Introduction to advanced abacus techniques.");
                levelDescriptions.put("Senior_Level_8 - ₹70", "Working with multiple-digit numbers.");
                levelDescriptions.put("Senior_Level_9 - ₹70", "Speed training and advanced patterns.");
                levelDescriptions.put("Senior_Level_10 - ₹70", "Speed training and advanced patterns.");
                break;

            case "Vedic Maths":
                levelDescriptions.put("Vedic_Level_1 - ₹100", "Introduction to Vedic formulas.");
                levelDescriptions.put("Vedic_Level_2 - ₹100", "Speed multiplication techniques.");
                levelDescriptions.put("Vedic_Level_3 - ₹100", "Division tricks and patterns.");
                levelDescriptions.put("Vedic_Level_4 - ₹100", "Advanced Vedic applications.");
                break;

            default:
                // No descriptions
        }
    }

    @SuppressLint("MissingInflatedId")
    private void showCourseLevels() {
        tvCourseTitle1.setText(courseName);
        layoutLevels1.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String level : currentLevels) {
            View row = inflater.inflate(R.layout.item_videolevel_row, layoutLevels1, false);
            CheckBox cb = row.findViewById(R.id.checkboxLevel);
            TextView tv = row.findViewById(R.id.tvLevelText);
            ImageView ivArrow = row.findViewById(R.id.ivArrow);
            TextView tvDescription = row.findViewById(R.id.tvDescription);

            tv.setText(level);
            cb.setChecked(cartManager.isSelected(level));
            cb.setOnCheckedChangeListener(getLevelCheckboxListener(level, cb));

            // Dummy description - you can load based on level name
            tvDescription.setText(levelDescriptions.getOrDefault(level, "No description available."));

            // Arrow click listener to toggle description
            ivArrow.setOnClickListener(v -> {
                if (tvDescription.getVisibility() == View.GONE) {
                    tvDescription.setVisibility(View.VISIBLE);
                    ivArrow.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
                } else {
                    tvDescription.setVisibility(View.GONE);
                    ivArrow.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
                }
            });

            // Make the full row clickable
            row.setOnClickListener(v -> {
                boolean isChecked = !cb.isChecked();
                cb.setChecked(isChecked); // this will trigger the listener
            });

            layoutLevels1.addView(row);
        }

        cbSelectAll1.setOnCheckedChangeListener(null);
        cbSelectAll1.setChecked(allLevelsSelected());
        cbSelectAll1.setOnCheckedChangeListener(selectAllListener);

        updateCartCount();
    }


    private CompoundButton.OnCheckedChangeListener getLevelCheckboxListener(String levelText, CheckBox cb) {
        return (buttonView, isChecked) -> {
            if (isChecked) {
                cartManager.addLevel(courseName,levelText);
            } else {
                cartManager.removeLevel(courseName,levelText);
            }
            updateCartCount();

            cbSelectAll1.setOnCheckedChangeListener(null);
            cbSelectAll1.setChecked(allLevelsSelected());
            cbSelectAll1.setOnCheckedChangeListener(selectAllListener);
        };
    }

    private void updateCartCount() {
        tvCartCount.setText(String.valueOf(cartManager.getCount()));
    }

    private boolean allLevelsSelected() {
        for (String level : currentLevels) {
            if (!cartManager.isSelected(level)) return false;
        }
        return true;
    }
}