package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

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

public class AboutUsActivity extends AppCompatActivity {

    private LinearLayout btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AboutUsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        TextView tvTerms = findViewById(R.id.tvAbout);

        String termsText = "Who We Are\n\n" +
                "Our Abacus Trainer Team believes that every child holds an essence of imagination, creativity, and profound insight. " +
                "The ability to reveal this potential can be found in the ancient tools of the Abacus and Vedic Maths. We are an interdisciplinary team integrating" +
                " mathematics, technology, and education. We have empowered over 5,000 children (ages 4-15) with advanced concentration, mental calculation skills, " +
                "and self-assurance, guiding them to become global achievers.\n" +
                "What Sets Us Apart\n\n" +
                "Certified Expert Instructors\n\n" +
                "We help all learners with Junior Abacus (5 to 8 years) and Advanced Vedic Maths (12 to 15 years) in 6 to 10 progressive levels while tailoring to their" +
                " pace and distinctive strengths.\n\n" +
                "Flexible, Online Delivery\n\n" +
                "Lessons are accessible via desktop, tablet, or mobile, ensuring regional and global learning is not confined to a time or place. " +
                "Scheduling during weekdays, weekends, or after-school hours further enhances accessibility.\n\n" +
                "Transparent Progress Tracking\n\n" +
                "Interactive dashboards with real-time updates and monthly mock assessments give tracking capabilities for achieving milestones, bringing both" +
                " children and parents.\n" ;

        SpannableStringBuilder spannable = new SpannableStringBuilder(termsText);
        // Headings list
        String[] headings = {
                "Who We Are",
                "What Sets Us Apart",
                "Certified Expert Instructors",
                "Personalized Learning Paths",
                "Flexible, Online Delivery",
                "Transparent Progress Tracking",
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