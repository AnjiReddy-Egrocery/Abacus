package com.dst.abacustrainner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class CoursesVideoActivity extends AppCompatActivity {
    Button btnPurchaseVideoCourses1, btnPurchaseVideoCourses2, btnPurchaseVideoCourses3;
    LinearLayout layoutCourseBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_courses_video);

        btnPurchaseVideoCourses1 = findViewById(R.id.btnPurchase_videocourses1);
        btnPurchaseVideoCourses2 = findViewById(R.id.btnPurchase_videocourses2);
        btnPurchaseVideoCourses3 = findViewById(R.id.btnPurchase_videoCourses3);
        layoutCourseBack = findViewById(R.id.layout_course_back);

        btnPurchaseVideoCourses1.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesVideoActivity.this, CourseVideoDetailActivity.class);
            intent.putExtra("course_name", "Abacus Junior");
            startActivity(intent);
        });

        btnPurchaseVideoCourses2.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesVideoActivity.this, CourseVideoDetailActivity.class);
            intent.putExtra("course_name", "Abacus Senior");
            startActivity(intent);
        });

        btnPurchaseVideoCourses3.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesVideoActivity.this, CourseVideoDetailActivity.class);
            intent.putExtra("course_name", "Vedic Maths");
            startActivity(intent);
        });

        layoutCourseBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CoursesVideoActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // optional: closes current activity
            }
        });
    }
}