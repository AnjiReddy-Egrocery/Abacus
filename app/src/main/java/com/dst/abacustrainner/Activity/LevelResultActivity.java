package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.dst.abacustrainner.Adapter.LevelResultAdapter;
import com.dst.abacustrainner.Model.LevelResultModel;
import com.dst.abacustrainner.R;

import java.util.ArrayList;

public class LevelResultActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<LevelResultModel> resultList = new ArrayList<>();
    LevelResultAdapter adapter;
    LinearLayout layoutBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_result);

        recyclerView = findViewById(R.id.recyclerViewResults);
        layoutBack = findViewById(R.id.fragment_container);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sample static 4 results
        resultList.add(new LevelResultModel("12-07-2025", "10:30 AM"));
        resultList.add(new LevelResultModel("13-07-2025", "11:15 AM"));
        resultList.add(new LevelResultModel("14-07-2025", "09:45 AM"));
        resultList.add(new LevelResultModel("15-07-2025", "04:00 PM"));

        adapter = new LevelResultAdapter(this, resultList);
        recyclerView.setAdapter(adapter);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}