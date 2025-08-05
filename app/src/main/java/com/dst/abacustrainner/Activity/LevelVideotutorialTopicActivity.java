package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.dst.abacustrainner.Adapter.TopicsAdapter;
import com.dst.abacustrainner.Adapter.VideoTopicsAdapter;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class LevelVideotutorialTopicActivity extends AppCompatActivity {

    private RecyclerView recyclerTopics;
    private VideoTopicsAdapter adapter;
    private List<String> topicsList = new ArrayList<>();
    private String levelName;
    LinearLayout layoutBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_videotutorial_topic);

        recyclerTopics = findViewById(R.id.recycler_view);
        layoutBack = findViewById(R.id.fragment_container);
        levelName = getIntent().getStringExtra("level_name");

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Load static topics based on level
        loadTopicsForLevel(levelName);

        adapter = new VideoTopicsAdapter(topicsList, levelName, this);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerTopics.setAdapter(adapter);
    }

    private void loadTopicsForLevel(String level) {
        // Static topics by level (add more as needed)
        if (level.equalsIgnoreCase("Level 1")) {
            topicsList.add("Addition");
            topicsList.add("Subtraction");
        } else if (level.equalsIgnoreCase("Level 2")) {
            topicsList.add("Multiplication");
            topicsList.add("Division");
        } else {
            topicsList.add("Addition");
            topicsList.add("Subtraction");
            topicsList.add("Multiplication");
            topicsList.add("Division");
            topicsList.add("qube");
            topicsList.add("SquareRoot");
            topicsList.add("Numaric Root");
            topicsList.add("Genaric Root");

        }
    }

}