package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.util.List;
import java.util.Map;

public class VideoCoursesActivity extends AppCompatActivity {
    Button btnPurchase11, btnPurchase21, btnPurchase31,btnSubscribejunior1,btnSubscribeSenior1,btnSubscribeVedic1;
    LinearLayout layoutCourseBack;
    TextView tvJunior1, tvSenior1, tvVedic1;
    LinearLayout layoutAccordionJunior1,layoutAccordionSenior1,layoutAccordionVedic1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_courses);

        btnPurchase11 = findViewById(R.id.btnPurchase11);
        btnPurchase21 = findViewById(R.id.btnPurchase21);
        btnPurchase31 = findViewById(R.id.btnPurchase31);
        layoutCourseBack = findViewById(R.id.layout_course_back);
        layoutAccordionJunior1 = findViewById(R.id.layoutAccordionJunior1);
        layoutAccordionSenior1 = findViewById(R.id.layoutAccordionSenior1);
        layoutAccordionVedic1 = findViewById(R.id.layoutAccordionVedic1);
        btnSubscribejunior1 = findViewById(R.id.btnSubscribejunior1);
        btnSubscribeSenior1= findViewById(R.id.btnSubscribeSenior1);
        btnSubscribeVedic1 = findViewById(R.id.btnSubscribeVedic1);


        // Reset all 3 course counts first to empty
        tvJunior1 = findViewById(R.id.tvSelectedCountJunior1);
        tvSenior1 = findViewById(R.id.tvSelectedCountSenior1);
        tvVedic1 = findViewById(R.id.tvSelectedCountVedic1);

        btnPurchase11.setOnClickListener(v -> {
            if (layoutAccordionJunior1.getVisibility() == View.GONE) {
                layoutAccordionJunior1.setVisibility(View.VISIBLE);
                layoutAccordionSenior1.setVisibility(View.GONE);
                layoutAccordionVedic1.setVisibility(View.GONE);
            } else {
                layoutAccordionJunior1.setVisibility(View.GONE);
            }
        });
        btnPurchase21.setOnClickListener(v -> {
            if (layoutAccordionSenior1.getVisibility() == View.GONE) {
                layoutAccordionSenior1.setVisibility(View.VISIBLE);
                layoutAccordionJunior1.setVisibility(View.GONE);
            } else {
                layoutAccordionSenior1.setVisibility(View.GONE);
            }
        });

        btnPurchase31.setOnClickListener(v -> {
            if (layoutAccordionVedic1.getVisibility() == View.GONE) {
                layoutAccordionVedic1.setVisibility(View.VISIBLE);
                layoutAccordionJunior1.setVisibility(View.GONE);
                layoutAccordionSenior1.setVisibility(View.GONE);
            } else {
                layoutAccordionVedic1.setVisibility(View.GONE);
            }
        });


        btnSubscribejunior1.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCoursesActivity.this, VideoCourseDetailActivity.class);
            intent.putExtra("course_name", "Abacus Junior");
            startActivity(intent);
        });

        btnSubscribeSenior1.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCoursesActivity.this, VideoCourseDetailActivity.class);
            intent.putExtra("course_name", "Abacus Senior");
            startActivity(intent);
        });

        btnSubscribeVedic1.setOnClickListener(v -> {
            Intent intent = new Intent(VideoCoursesActivity.this, VideoCourseDetailActivity.class);
            intent.putExtra("course_name", "Vedic Maths");
            startActivity(intent);
        });

        layoutCourseBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(VideoCoursesActivity.this, HomeActivity.class);
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

        // Reset all TextViews
        tvJunior1.setText("Selected: 0");
        tvSenior1.setText("Selected: 0");
        tvVedic1.setText("Selected: 0");

        Map<String, List<String>> selectedLevelsByCourse = cart.getSelectedLevelsByCourse("video");

        int juniorCount = 0;
        int seniorCount = 0;
        int vedicCount = 0;

        for (Map.Entry<String, List<String>> entry : selectedLevelsByCourse.entrySet()) {
            String courseName = entry.getKey();
            int levelCount = entry.getValue().size();

            if (courseName.equals("Abacus Junior")) {
                juniorCount = levelCount;
            } else if (courseName.equals("Abacus Senior")) {
                seniorCount = levelCount;
            } else if (courseName.equals("Vedic Maths")) {
                vedicCount = levelCount;
            }
        }

        if (juniorCount > 0) {
            tvJunior1.setText("Selected: " + juniorCount);
        }

        if (seniorCount > 0) {
            tvSenior1.setText("Selected: " + seniorCount);
        }

        if (vedicCount > 0) {
            tvVedic1.setText("Selected: " + vedicCount);
        }
    }
}