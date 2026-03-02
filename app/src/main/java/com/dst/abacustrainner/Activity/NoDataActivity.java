package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.R;


public class NoDataActivity extends AppCompatActivity {

    LinearLayout layoutBack;

    String name="";

    String topicname="";
    TextView txtTopicName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_data);

        //txtName=findViewById(R.id.txt_name);
        txtTopicName=findViewById(R.id.txt_topic_name);
        layoutBack = findViewById(R.id.layout_back);


        Bundle bundle=getIntent().getExtras();
        name=bundle.getString("firstName");
        topicname=bundle.getString("topicName");

       // txtName.setText(name);
        txtTopicName.setText(topicname);

        layoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoDataActivity.this, AssignmentListActivity.class);
                startActivity(intent);
            }
        });
    }
}