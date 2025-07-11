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


import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.Model.Courses;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    Button btnPurchase1, btnPurchase2, btnPurchase3;
    LinearLayout layoutCourseBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        btnPurchase1 = findViewById(R.id.btnPurchase1);
        btnPurchase2 = findViewById(R.id.btnPurchase2);
        btnPurchase3 = findViewById(R.id.btnPurchase3);
        layoutCourseBack = findViewById(R.id.layout_course_back);

        btnPurchase1.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_name", "Abacus Junior");
            startActivity(intent);
        });

        btnPurchase2.setOnClickListener(v -> {
            Intent intent = new Intent(CoursesActivity.this, CourseDetailActivity.class);
            intent.putExtra("course_name", "Abacus Senior");
            startActivity(intent);
        });

        btnPurchase3.setOnClickListener(v -> {
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
}

