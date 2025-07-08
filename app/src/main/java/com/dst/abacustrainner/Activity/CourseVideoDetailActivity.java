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

    private final CartManager cartManager = CartManager.getInstance();

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
                            "Level 1 - ₹50", "Level 2 - ₹50", "Level 3 - ₹50",
                            "Level 4 - ₹50", "Level 5 - ₹50", "Level 6 - ₹50"
                    };
                    break;

                case "Abacus Senior":
                    currentLevels = new String[]{
                            "Level 1 - ₹70", "Level 2 - ₹70", "Level 3 - ₹70",
                            "Level 4 - ₹70", "Level 5 - ₹70", "Level 6 - ₹70",
                            "Level 7 - ₹70", "Level 8 - ₹70", "Level 9 - ₹70", "Level 10 - ₹70"
                    };
                    break;

                case "Vedic Maths":
                    currentLevels = new String[]{
                            "Level 1 - ₹100", "Level 2 - ₹100", "Level 3 - ₹100", "Level 4 - ₹100"
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
                if (isChecked) {
                    cartManager.addLevel(levelText);
                } else {
                    cartManager.removeLevel(levelText);
                }

                cb.setOnCheckedChangeListener(getLevelCheckboxListener(levelText, cb));
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
            cb.setChecked(cartManager.isSelected(level));
            cb.setOnCheckedChangeListener(getLevelCheckboxListener(level, cb));

            layoutVideoLevels.addView(row);
        }

        cbVideoSelectAll.setOnCheckedChangeListener(null);
        cbVideoSelectAll.setChecked(allLevelsSelected());
        cbVideoSelectAll.setOnCheckedChangeListener(selectAllListener);

        updateCartCount();
    }

    private CompoundButton.OnCheckedChangeListener getLevelCheckboxListener(String levelText, CheckBox cb) {
        return (buttonView, isChecked) -> {
            if (isChecked) {
                cartManager.addLevel(levelText);
            } else {
                cartManager.removeLevel(levelText);
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
            if (!cartManager.isSelected(level)) return false;
        }
        return true;
    }
}