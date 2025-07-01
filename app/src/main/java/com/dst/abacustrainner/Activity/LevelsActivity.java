package com.dst.abacustrainner.Activity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dst.abacustrainner.R;

import java.util.ArrayList;

public class LevelsActivity extends AppCompatActivity {

    ListView levelListView;
    TextView courseTitle;
    ArrayAdapter<String> adapter;
    ArrayList<String> levels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        courseTitle = findViewById(R.id.courseTitle);
        levelListView = findViewById(R.id.levelListView);

        String courseName = getIntent().getStringExtra("courseName");
        courseTitle.setText(courseName);

        levels = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            levels.add("Level " + i);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, levels);
        levelListView.setAdapter(adapter);
    }
}
