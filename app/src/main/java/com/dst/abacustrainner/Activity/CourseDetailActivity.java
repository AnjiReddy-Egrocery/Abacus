package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;

public class CourseDetailActivity extends AppCompatActivity {

    private TextView tvCourseTitle, tvCartCount;
    private CheckBox cbSelectAll;
    private LinearLayout layoutLevels,layoutCourseDetailBack;
    private Button btnpurchasemore, btnCart;
    private ImageView ivCart;

    private String courseName;
    private String[] currentLevels;

    private final CartManager cartManager = CartManager.getInstance();

    private CompoundButton.OnCheckedChangeListener selectAllListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        cbSelectAll = findViewById(R.id.cbSelectAll);
        layoutLevels = findViewById(R.id.layoutLevels);
        btnpurchasemore = findViewById(R.id.btnPurchaseMore);
        btnCart = findViewById(R.id.cart);
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

        btnpurchasemore.setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailActivity.this, CoursesActivity.class);
            startActivity(intent);
        });

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });

        layoutCourseDetailBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, CoursesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        selectAllListener = (buttonView, isChecked) -> {
            for (int i = 0; i < layoutLevels.getChildCount(); i++) {
                View row = layoutLevels.getChildAt(i);
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

        cbSelectAll.setOnCheckedChangeListener(selectAllListener);
    }

    private void showCourseLevels() {
        tvCourseTitle.setText(courseName);
        layoutLevels.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (String level : currentLevels) {
            View row = inflater.inflate(R.layout.item_level_row, layoutLevels, false);
            CheckBox cb = row.findViewById(R.id.checkboxLevel);
            TextView tv = row.findViewById(R.id.tvLevelText);

            tv.setText(level);
            cb.setChecked(cartManager.isSelected(level));
            cb.setOnCheckedChangeListener(getLevelCheckboxListener(level, cb));

            layoutLevels.addView(row);
        }

        cbSelectAll.setOnCheckedChangeListener(null);
        cbSelectAll.setChecked(allLevelsSelected());
        cbSelectAll.setOnCheckedChangeListener(selectAllListener);

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

            cbSelectAll.setOnCheckedChangeListener(null);
            cbSelectAll.setChecked(allLevelsSelected());
            cbSelectAll.setOnCheckedChangeListener(selectAllListener);
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