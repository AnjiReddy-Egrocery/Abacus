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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelVideotutorialTopicActivity extends AppCompatActivity {

    private RecyclerView recyclerTopics;
    private VideoTopicsAdapter adapter;
    private List<String> topicsList = new ArrayList<>();
    private String levelName;
    LinearLayout layoutBack;

    Map<String, String> topicUrls = new HashMap<>();

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

        /*adapter = new  VideoTopicsAdapter(topicsList, topicUrls,levelName, this);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerTopics.setAdapter(adapter);*/
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
            topicsList.add("cube");
            topicsList.add("SquareRoot");
            topicsList.add("Numaric Root");
            topicsList.add("Genaric Root");
        }

        // 👇 put your sampleUrl mapping here (for ALL topics)
        String sampleUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4";

        topicUrls.put("Addition", sampleUrl);
        topicUrls.put("Subtraction", sampleUrl);
        topicUrls.put("Multiplication", sampleUrl);
        topicUrls.put("Division", sampleUrl);
        topicUrls.put("Cube", sampleUrl);
        topicUrls.put("SquareRoot", sampleUrl);
        topicUrls.put("Numaric Root", sampleUrl);
        topicUrls.put("Genaric Root", sampleUrl);

        // 👇 initialize adapter
        adapter = new VideoTopicsAdapter(topicsList, topicUrls, levelName, this);
        recyclerTopics.setLayoutManager(new LinearLayoutManager(this));
        recyclerTopics.setAdapter(adapter);

        }
    }

