package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class WorksheetSubscriptionActivity extends AppCompatActivity {

    Button btnYes, btnNo;
    private LinearLayout btnBack;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worksheet_subscription);

        btnYes = findViewById(R.id.btn_yes);
        btnNo = findViewById(R.id.btn_no);
        btnBack=findViewById(R.id.btn_back_to_home);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to courses page
                Intent intent = new Intent(WorksheetSubscriptionActivity.this, CoursesActivity.class);
                startActivity(intent);
                finish(); // optional
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to dashboard
                Intent intent = new Intent(WorksheetSubscriptionActivity.this, HomeActivity.class);
                startActivity(intent);
                finish(); // optional
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(WorksheetSubscriptionActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}