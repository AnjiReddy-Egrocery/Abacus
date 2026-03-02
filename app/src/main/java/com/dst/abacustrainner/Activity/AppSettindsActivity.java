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

public class AppSettindsActivity extends AppCompatActivity {
    private LinearLayout btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settinds);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(AppSettindsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

         TextView tvTerms = findViewById(R.id.tvSettings);

        String termsText = "Account Settings \n\n" +
                "Manage your personal information and account details. You can update your profile, change password, and manage your login details to keep your account secure.\n\n" +
                "Notifications\n\n" +
                "Control how you receive notifications from the app. Enable or disable class reminders, practice alerts, and important announcements so you never miss an update.\n\n" +
                "Learning Preferences\n\n" +
                "Customize your learning experience. Choose your preferred language, adjust practice reminders, and personalize settings to suit your learning style.\n\n" +
                "App Preferences\n\n" +
                "Modify app appearance and behavior. Switch between light and dark mode, adjust font size, and manage app performance settings for a better experience.\n\n" +
                "Privacy & Security\n\n"+
                "Your privacy is important to us. View our Privacy Policy, Terms & Conditions, and learn how we protect your data. You can also request account deletion from here.\n\n"+
                "Subscriptions & Payments\n\n"+
                "View your active plans, purchase history, and payment details. Manage your subscription and review refund policies easily.\n\n"+
                "Help & Support\n\n" +
                "Need help? Check frequently asked questions, contact our support team, or send feedback to improve your experience.\n\n" +
                "About App\n\n"+
                "Learn more about AbacusTrainer, app version details, and developer information.\n\n" +
                "Logout\n\n" +
                "Sign out securely from your account.\n\n";

        SpannableStringBuilder spannable = new SpannableStringBuilder(termsText);
        // Headings list
        String[] headings = {
                "Account Settings",
                "Notifications",
                "Learning Preferences",
                "App Preferences",
                "Privacy & Security",
                "Subscriptions & Payments",
                "Help & Support",
                "About App",
                "Logout"

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

                spannable.setSpan(new AbsoluteSizeSpan(20, true),
                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        tvTerms.setText(spannable);



    }

}