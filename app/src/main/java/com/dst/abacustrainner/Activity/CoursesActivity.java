package com.dst.abacustrainner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;


import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.Model.Courses;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoursesActivity extends AppCompatActivity {

    ExpandableListView expandableListView;
    List<String> courses;
    HashMap<String, List<String>> levelMap;
    HashMap<String, String> courseDescriptions;
    private LinearLayout btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        expandableListView = findViewById(R.id.expandableListView);
        btnBack = findViewById(R.id.fragment_container);

        // Courses
        courses = Arrays.asList("Junior Abacus", "Senior Abacus", "Vedic Maths");

        // Levels map
        levelMap = new HashMap<>();
        for (String course : courses) {
            List<String> levels = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                levels.add("Level " + i);
            }
            levelMap.put(course, levels);
        }

        // Course descriptions
        courseDescriptions = new HashMap<>();
        courseDescriptions.put("Junior Abacus", "This course introduces basic abacus skills for children aged 5–8.");
        courseDescriptions.put("Senior Abacus", "Advanced abacus techniques to improve speed and accuracy.");
        courseDescriptions.put("Vedic Maths", "Learn fast and traditional Indian techniques for calculations.");

        // Set adapter
        CourseExpandableAdapter adapter = new CourseExpandableAdapter(this, courses, levelMap, courseDescriptions);
        expandableListView.setAdapter(adapter);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an instance of SchedulesFragment
                finish();
            }
        });
    }

}

