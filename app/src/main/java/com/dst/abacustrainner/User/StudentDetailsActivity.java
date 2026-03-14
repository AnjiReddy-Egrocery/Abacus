package com.dst.abacustrainner.User;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.dst.abacustrainner.Adapter.StudentAdapter;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;

import java.util.ArrayList;
import java.util.List;

public class StudentDetailsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StudentAdapter adapter;
    List<StudentRegistationResponse.Result> studentList;
    String imageBaseUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        recyclerView = findViewById(R.id.studentRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        studentList = (List<StudentRegistationResponse.Result>)
                getIntent().getSerializableExtra("studentList");

        if (studentList != null) {
            Log.d("LoginDebug", "Student list received: " + studentList.size());
        } else {
            Log.d("LoginDebug", "Student list is null in intent!");
            studentList = new ArrayList<>(); // prevent crash
        }


        imageBaseUrl = getIntent().getStringExtra("imageUrl");

        adapter = new StudentAdapter(this, studentList, imageBaseUrl);
        recyclerView.setAdapter(adapter);
    }
}