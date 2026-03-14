package com.dst.abacustrainner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.StudentDetailsActivity;
import com.dst.abacustrainner.User.WelcomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class SpalashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DELAY = 3200;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Handler().postDelayed(() -> {

            if (SharedPrefManager.getInstance(getApplicationContext()).isLoggedIn()) {

                SharedPreferences pref = getSharedPreferences("userProfile", MODE_PRIVATE);
                String studentListJson = pref.getString("student_list", null);
                List<StudentRegistationResponse.Result> studentList = new ArrayList<>();

                if (studentListJson != null) {
                    Type type = new TypeToken<List<StudentRegistationResponse.Result>>() {}.getType();
                    studentList = new Gson().fromJson(studentListJson, type);
                }

                String imageUrl = pref.getString("image_url", "");

                if (studentList.size() > 1) {
                    Intent intent = new Intent(SpalashActivity.this, StudentDetailsActivity.class);
                    intent.putExtra("studentList", (Serializable) studentList);
                    intent.putExtra("imageUrl", imageUrl);
                    startActivity(intent);
                } else {
                    startActivity(new Intent(SpalashActivity.this, HomeActivity.class));
                }


            } else {
                // not logged in → WelcomeActivity
                startActivity(new Intent(SpalashActivity.this, WelcomeActivity.class));
            }

            finish();

        }, SPLASH_SCREEN_DELAY);
    }
}