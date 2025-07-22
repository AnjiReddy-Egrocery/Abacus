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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;

public class CourseVideoDetailActivity extends AppCompatActivity {

    private TextView tvCoursevideoTitle, tvCartCount;
    private CheckBox cbVideoSelectAll;
    private LinearLayout layoutVideoLevels,layoutCourseDetailBack;
    private Button btnpurchase, btnVideoCart;
    private ImageView ivCart;

    private String courseName;
    private String[] currentLevels;

    private final CartManager cartManager = CartManager.getInstance(this);

    private CompoundButton.OnCheckedChangeListener selectAllListener;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_course_video_detail);

        tvCoursevideoTitle = findViewById(R.id.tvCoursevideoTitle);
        cbVideoSelectAll = findViewById(R.id.cbvideoSelectAll);
        layoutVideoLevels = findViewById(R.id.layoutvideoLevels);
        btnpurchase = findViewById(R.id.btnPurchase);
        btnVideoCart = findViewById(R.id.btn_cart);
        tvCartCount = findViewById(R.id.tvCartCount);
        ivCart = findViewById(R.id.ivCart);
        layoutCourseDetailBack = findViewById(R.id.layout_coursedetail_back);

        courseName = getIntent().getStringExtra("course_name");

        if (courseName != null) {
            switch (courseName) {
                case "Abacus Junior":
                    currentLevels = new String[]{
                            "Junior_Level_Video_1 - ₹50", "Junior_Level_Video_2 - ₹50", "Junior_Level_Video_3 - ₹50",
                            "Junior_Level_Video_4 - ₹50", "Junior_Level_Level_Video_5 - ₹50", "Junior_Level_Level_Video_6 - ₹50"
                    };
                    break;

                case "Abacus Senior":
                    currentLevels = new String[]{
                            "Senior_Level_Video_1 - ₹70", "Senior_Level_Video_2 - ₹70", "Senior_Level_Video_3 - ₹70",
                            "Senior_Level_Video_4 - ₹70", "Senior_Level_Video_5 - ₹70", "Senior_Level_Video_6 - ₹70",
                            "Senior_Level_Video_7 - ₹70", "Senior_Level_Video_8 - ₹70", "Senior_Level_Video_9 - ₹70", "Senior_Level_Video_10 - ₹70"
                    };
                    break;

                case "Vedic Maths":
                    currentLevels = new String[]{
                            "Vedic_Level_Video_1 - ₹100", "Vedic_Level_Video_2 - ₹100", "Vedic_Level_Video_3 - ₹100", "Vedic_Level_Video_4 - ₹100"
                    };
                    break;

                default:
                    currentLevels = new String[]{};
            }

            showCourseLevels();
        }

        btnpurchase.setOnClickListener(v -> {
            Intent intent = new Intent(CourseVideoDetailActivity.this, CoursesVideoActivity.class);
            startActivity(intent);
        });

        btnVideoCart.setOnClickListener(v -> {
            Intent intent = new Intent(CourseVideoDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        layoutCourseDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseVideoDetailActivity.this, CoursesVideoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

        selectAllListener = (buttonView, isChecked) -> {
            for (int i = 0; i < layoutVideoLevels.getChildCount(); i++) {
                View row = layoutVideoLevels.getChildAt(i);
                CheckBox cb = row.findViewById(R.id.checkboxLevel);
                cb.setOnCheckedChangeListener(null);
                cb.setChecked(isChecked);

                String levelText = ((TextView) row.findViewById(R.id.tvLevelText)).getText().toString();
                String levelKey = levelText; // unique key for video levels
                if (isChecked) {
                 //   cartManager.addLevel(levelKey);
                } else {
                 //   cartManager.removeLevel(levelKey);
                }

                cb.setOnCheckedChangeListener(getLevelCheckboxListener(levelKey, cb));
            }
            updateCartCount();
        };

        cbVideoSelectAll.setOnCheckedChangeListener(selectAllListener);
    }

    private void showCourseLevels() {
        tvCoursevideoTitle.setText(courseName);
        layoutVideoLevels.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String level : currentLevels) {
            View row = inflater.inflate(R.layout.item_level_row, layoutVideoLevels, false);
            CheckBox cb = row.findViewById(R.id.checkboxLevel);
            TextView tv = row.findViewById(R.id.tvLevelText);

            tv.setText(level);
            String levelKey = level;

            cb.setChecked(cartManager.isSelected(levelKey));
            cb.setOnCheckedChangeListener(getLevelCheckboxListener(levelKey, cb));

            layoutVideoLevels.addView(row);
        }

        cbVideoSelectAll.setOnCheckedChangeListener(null);
        cbVideoSelectAll.setChecked(allLevelsSelected());
        cbVideoSelectAll.setOnCheckedChangeListener(selectAllListener);

        updateCartCount();
    }

    private CompoundButton.OnCheckedChangeListener getLevelCheckboxListener(String levelKey, CheckBox cb) {
        return (buttonView, isChecked) -> {
            if (isChecked) {
              //  cartManager.addLevel(levelKey);
            } else {
             //  cartManager.removeLevel(levelKey);
            }
            updateCartCount();

            cbVideoSelectAll.setOnCheckedChangeListener(null);
            cbVideoSelectAll.setChecked(allLevelsSelected());
            cbVideoSelectAll.setOnCheckedChangeListener(selectAllListener);
        };
    }

    private void updateCartCount() {
        tvCartCount.setText(String.valueOf(cartManager.getCount()));
    }

    private boolean allLevelsSelected() {
        for (String level : currentLevels) {
            String levelKey = level;
            if (!cartManager.isSelected(levelKey)) return false;
        }
        return true;
    }
}