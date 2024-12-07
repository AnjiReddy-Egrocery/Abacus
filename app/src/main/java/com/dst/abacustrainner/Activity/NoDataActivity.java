package com.dst.abacustrainner.Activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.R;


public class NoDataActivity extends AppCompatActivity {

    String name="";

    String topicname="";
    TextView txtName,txtTopicName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_data);

        txtName=findViewById(R.id.txt_name);
        txtTopicName=findViewById(R.id.txt_topic_name);

        Bundle bundle=getIntent().getExtras();
        name=bundle.getString("firstName");
        topicname=bundle.getString("topicName");

        txtName.setText(name);
        txtTopicName.setText(topicname);
    }
}