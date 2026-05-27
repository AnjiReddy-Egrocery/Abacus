package com.dst.abacustrainner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.Activity.AssignmentListActivity;
import com.dst.abacustrainner.Activity.PurchasedActivity;
import com.dst.abacustrainner.Activity.TopicListActivity;
import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.StudentDetailsActivity;
import com.dst.abacustrainner.User.WelcomeActivity;
import com.dst.abacustrainner.database.SharedPrefManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
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

        askNotificationPermission();

        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {

                    if(!task.isSuccessful()){
                        return;
                    }

                    String token =
                            task.getResult();

                    Log.d(
                            "FCM_TOKEN",
                            token
                    );

                    FirebaseMessaging.getInstance()
                            .subscribeToTopic(
                                    "all_users"
                            );
                });

        new Handler().postDelayed(() -> {

            Intent incomingIntent = getIntent();

            String notificationFor =
                    incomingIntent.getStringExtra("notificationFor");

            String studentId =
                    incomingIntent.getStringExtra("studentId");

            String dateId =
                    incomingIntent.getStringExtra("dateId");

            Intent intent;

            // TOPIC
            if ("topic".equals(notificationFor)) {

                intent = new Intent(
                        SpalashActivity.this,
                        TopicListActivity.class
                );

                intent.putExtra("studentId", studentId);
                intent.putExtra("dateId", dateId);
            }

            // ASSIGNMENT
            else if ("assignment".equals(notificationFor)) {

                intent = new Intent(
                        SpalashActivity.this,
                        AssignmentListActivity.class
                );

                intent.putExtra("studentId", studentId);
                intent.putExtra("dateId", dateId);
            }

            // SUBSCRIPTION EXPIRED
            else if ("subscriptionExpired".equals(notificationFor)) {

                intent = new Intent(
                        SpalashActivity.this,
                        PurchasedActivity.class
                );

                intent.putExtra("studentId", studentId);
            }

            // SCHEDULES
            else if ("schedules".equals(notificationFor)) {

                intent = new Intent(
                        SpalashActivity.this,
                        HomeActivity.class
                );

                intent.putExtra("openFragment", "schedules");
                intent.putExtra("studentId", studentId);
            }

            else {

                // Normal Flow
                if (SharedPrefManager.getInstance(
                        getApplicationContext()
                ).isLoggedIn()) {

                    SharedPreferences pref =
                            getSharedPreferences(
                                    "userProfile",
                                    MODE_PRIVATE
                            );

                    String studentListJson =
                            pref.getString(
                                    "student_list",
                                    null
                            );

                    List<StudentRegistationResponse.Result>
                            studentList = new ArrayList<>();

                    if (studentListJson != null) {

                        Type type =
                                new TypeToken<
                                        List<StudentRegistationResponse.Result>>() {
                                }.getType();

                        studentList =
                                new Gson().fromJson(
                                        studentListJson,
                                        type
                                );
                    }

                    String imageUrl =
                            pref.getString(
                                    "image_url",
                                    ""
                            );

                    if (studentList.size() > 1) {

                        intent =
                                new Intent(
                                        SpalashActivity.this,
                                        StudentDetailsActivity.class
                                );

                        intent.putExtra(
                                "studentList",
                                (Serializable) studentList
                        );

                        intent.putExtra(
                                "imageUrl",
                                imageUrl
                        );

                    } else {

                        intent =
                                new Intent(
                                        SpalashActivity.this,
                                        HomeActivity.class
                                );
                    }

                } else {

                    intent =
                            new Intent(
                                    SpalashActivity.this,
                                    WelcomeActivity.class
                            );
                }
            }

            startActivity(intent);
            finish();

        }, SPLASH_SCREEN_DELAY);
    }

    private void askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= 33) {

            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
            }
        }
    }
}