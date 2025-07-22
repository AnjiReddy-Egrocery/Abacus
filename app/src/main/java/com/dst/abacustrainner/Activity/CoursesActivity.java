package com.dst.abacustrainner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.Model.Courses;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    Button btnPurchase1, btnPurchase2, btnPurchase3,btnSubscribejunior,btnSubscribeSenior,btnSubscribeVedic;
    LinearLayout layoutCourseBack;
    TextView tvJunior, tvSenior, tvVedic;
    LinearLayout layoutAccordionJunior,layoutAccordionSenior,layoutAccordionVedic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        btnPurchase1 = findViewById(R.id.btnPurchase1);
        btnPurchase2 = findViewById(R.id.btnPurchase2);
        btnPurchase3 = findViewById(R.id.btnPurchase3);
        layoutCourseBack = findViewById(R.id.layout_course_back);
        layoutAccordionJunior = findViewById(R.id.layoutAccordionJunior);
        layoutAccordionSenior = findViewById(R.id.layoutAccordionSenior);
        layoutAccordionVedic = findViewById(R.id.layoutAccordionVedic);
        btnSubscribejunior = findViewById(R.id.btnSubscribejunior);
        btnSubscribeSenior = findViewById(R.id.btnSubscribeSenior);
        btnSubscribeVedic = findViewById(R.id.btnSubscribeVedic);


        // Reset all 3 course counts first to empty
        tvJunior = findViewById(R.id.tvSelectedCountJunior);
        tvSenior = findViewById(R.id.tvSelectedCountSenior);
        tvVedic = findViewById(R.id.tvSelectedCountVedic);

        btnPurchase1.setOnClickListener(v -> {
            if (layoutAccordionJunior.getVisibility() == View.GONE) {
                layoutAccordionJunior.setVisibility(View.VISIBLE);
                layoutAccordionSenior.setVisibility(View.GONE);
                layoutAccordionVedic.setVisibility(View.GONE);
            } else {
                layoutAccordionJunior.setVisibility(View.GONE);
            }
        });
        btnPurchase2.setOnClickListener(v -> {
            if (layoutAccordionSenior.getVisibility() == View.GONE) {
                layoutAccordionSenior.setVisibility(View.VISIBLE);
                layoutAccordionJunior.setVisibility(View.GONE);
            } else {
                layoutAccordionSenior.setVisibility(View.GONE);
            }
        });

        btnPurchase3.setOnClickListener(v -> {
            if (layoutAccordionVedic.getVisibility() == View.GONE) {
                layoutAccordionVedic.setVisibility(View.VISIBLE);
                layoutAccordionJunior.setVisibility(View.GONE);
                layoutAccordionSenior.setVisibility(View.GONE);
            } else {
                layoutAccordionVedic.setVisibility(View.GONE);
            }
        });


        btnSubscribejunior.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_name", "Abacus Junior");
            startActivity(intent);
        });

        btnSubscribeSenior.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_name", "Abacus Senior");
            startActivity(intent);
        });

        btnSubscribeVedic.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_name", "Vedic Maths");
            startActivity(intent);
        });

        layoutCourseBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoursesActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // optional: closes current activity
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateSelectedLevelBar();
    }

    private void updateSelectedLevelBar() {
        CartManager cart = CartManager.getInstance(this);

        // Reset all TextViews to 0
        tvJunior.setText("Selected: 0");
        tvSenior.setText("Selected: 0");
        tvVedic.setText("Selected: 0");

        int juniorCount = 0;
        int seniorCount = 0;
        int vedicCount = 0;

        // Count levels based on price indicator
        for (String level : cart.getAllSelectedLevels()) {
            if (level.contains("₹50")) {
                juniorCount++;
            } else if (level.contains("₹70")) {
                seniorCount++;
            } else if (level.contains("₹100")) {
                vedicCount++;
            }
        }

        // Update individual TextViews
        if (juniorCount > 0) {
            tvJunior.setText("Selected: " +juniorCount);
        }

        if (seniorCount > 0) {
            tvSenior.setText("Selected: " +seniorCount);
        }

        if (vedicCount > 0) {
            tvVedic.setText("Selected: " +vedicCount);
        }
    }
}
