package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class ProgressTrackerActivity extends AppCompatActivity {
    private LinearLayout btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_tracker);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ProgressTrackerActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

         TextView tvTerms = findViewById(R.id.tvProgress);

        String termsText = "Abacus Trainer Profile Progress Tracker\n\n" +
                "Track your learning journey and monitor your progress across different levels. This section helps you view completed lessons, practice performance, skill improvements, and overall growth. Stay motivated by checking your achievements and milestones as you advance through the Abacus training program.";

        SpannableStringBuilder spannable = new SpannableStringBuilder(termsText);
        // Headings list
        String[] headings = {
                "Abacus Trainer Profile Progress Tracker"

        };

        for (String heading : headings) {
            int start = termsText.indexOf(heading);
            if (start >= 0) {
                int end = start + heading.length();

                // Bold
                spannable.setSpan(new StyleSpan(Typeface.BOLD),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // Color #4d148c
                spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#4d148c")),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannable.setSpan(new AbsoluteSizeSpan(18, true),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        tvTerms.setText(spannable);



    }

}