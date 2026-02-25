package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class HelpActivity extends AppCompatActivity {
    private LinearLayout btnBack;
    LinearLayout container;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(HelpActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });


        container = findViewById(R.id.faqContainer);
        LayoutInflater inflater = LayoutInflater.from(this);

        String[][] faqs = {

                {"What are Abacus and Vedic Maths, and how do they benefit my child?",
                        "Abacus and Vedic Maths are mental math techniques that improve arithmetic skills, concentration, and overall brain development in children. They provide a strong foundation in mathematics."},

                {"At what age can my child start learning?",
                        "Children can start learning from age 5 depending on their interest and readiness."},

                {"How does Abacus improve concentration?",
                        "It activates both sides of the brain which improves memory, focus, and attention span."},

                {"Is prior math knowledge required?",
                        "No prior math knowledge is needed. Beginners can easily start learning."},

                {"How long is the course duration?",
                        "Course duration typically ranges from 6 months to 2 years depending on levels."},

                {"Will this help in school exams?",
                        "Yes, it improves speed, accuracy, and confidence in solving math problems."},

                {"Are online classes available?",
                        "Yes, both online and offline classes are available."},

                {"Do students get certificates?",
                        "Yes, certificates are provided after completing each level."},

                {"Is there a demo class available?",
                        "Yes, a free demo class is provided before enrollment."},

                {"How can I enroll my child?",
                        "You can enroll through our website or contact support team."}
        };

        for (String[] faq : faqs) {

            View view = inflater.inflate(R.layout.faq_item, container,false);

             TextView question = view.findViewById(R.id.question);
            TextView answer = view.findViewById(R.id.answer);
            TextView icon = view.findViewById(R.id.icon);
            LinearLayout header = view.findViewById(R.id.header);

            question.setText(faq[0]);
            answer.setText(faq[1]);

            header.setOnClickListener(v -> {

                if (answer.getVisibility() == View.GONE) {
                    answer.setVisibility(View.VISIBLE);
                    icon.setText("−");
                } else {
                    answer.setVisibility(View.GONE);
                    icon.setText("+");
                }

            });

            container.addView(view);
        }


    }
}