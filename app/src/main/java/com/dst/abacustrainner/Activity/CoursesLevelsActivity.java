package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.Adapter.CourseLevelsAdapter;
import com.dst.abacustrainner.Model.CourseListResponse;
import com.dst.abacustrainner.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CoursesLevelsActivity extends AppCompatActivity {

    RecyclerView recyclerLevels;
    CourseLevelsAdapter adapter;
    private LinearLayout layoutBack;
    TextView txtHeader;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_levels);



        layoutBack = findViewById(R.id.fragment_container);
        recyclerLevels = findViewById(R.id.recycler_course);
        txtHeader = findViewById(R.id.txt_headerName);
        recyclerLevels.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CourseLevelsAdapter(this);
        recyclerLevels.setAdapter(adapter);

        String levelsJson = getIntent().getStringExtra("levels");
        String studentId = getIntent().getStringExtra("StudentId");
        String headerName = getIntent().getStringExtra("HeaderName");

        String orderId = getIntent().getStringExtra("OrderId");


        txtHeader.setText(headerName);

// ✅ LOG here

        Log.d("LEVEL_DEBUG", "Received StudentId: " + studentId);

        // ✅ Parse
        Log.d("LEVEL_DEBUG", "Received Levels JSON: " + levelsJson);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Type type = new TypeToken<List<CourseListResponse.CourseLevels>>(){}.getType();

        List<CourseListResponse.CourseLevels> levels =
                new Gson().fromJson(levelsJson, type);

        // ✅ Set to adapter
        adapter.setLevels(levels,studentId,orderId);

    }
}