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

public class RefoundPolicyActivity extends AppCompatActivity {
    private LinearLayout btnBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refound_policy);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RefoundPolicyActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        TextView tvTerms = findViewById(R.id.tvRefound);

        String termsText = "At AbacusTrainer.com, we aim to provide clear, transparent, and student-friendly policies. Please read the following refund terms carefully before enrolling in our programs or purchasing any services.\n\n" +
                "Student Training Fees (Kids Training Programs)\n\n" +
                "Students (or parents/guardians) will always receive a demo class before making any payment, ensuring full clarity about the teaching method, curriculum, platform and learning experience.\n" +
                "Student payments can be made in one-time full payment or in 1–2 EMIs, depending on the plan offered at the time of enrolment.\n" +
                "All student fees are strictly non-refundable, regardless of whether the payment was made in full or through EMIs.\n" +
                "If a student decides to drop out in the middle of the course, no refunds will be issued for the remaining or unused classes.\n" +
                "EMI instalments (if chosen) must still be completed, even if the student discontinues the course prematurely.\n" +
                "Seats and schedules are reserved based on enrollment; hence we cannot offer refunds once payment is made.\n" +
                "Instructor / Train-the-Trainer Program Fees\n\n" +
                "Instructor / Train-the-Trainer Program Fees\n" +
                "No partial refunds, cancellations, or adjustments will be allowed once fees are paid.\n" +
                "Training materials, modules, login access, and learning resources are fully delivered only after payment; therefore refunds are not applicable.\n" +
                "Worksheets, Practice Sheets & Digital Downloads\n\n" +
                "All worksheet purchases, practice materials, ebooks, downloadable files, and digital content are non-refundable.\n" +
                "Once accessed or downloaded, digital items cannot be returned.\n" +
                "Purchases of worksheets or practice sheets are final and cannot be exchanged or refunded.\n" +
                "Batch Changes, Class Rescheduling & Missed Classes\n\n" +
                "Refunds are not issued for missed classes.\n" +
                "Class reschedules may be offered at our discretion, depending on availability and validity of the course.\n" +
                "Batch changes may be allowed only when feasible; however, they do not qualify for a refund.\n" +
                "Technical Issues\n\n" +
                "If you face any technical issue accessing digital content or online classes, our support team will assist in resolving the issue.\n" +
                "However, technical issues do not qualify for refunds, unless the content is proven to be inaccessible due to a fault on our end.\n" +
                "Acceptance of Refund Policy\n\n" +
                "By completing payment for any student program, instructor training, worksheets," +
                " or digital products, you acknowledge and agree:\n\n" +
                "That you have received a demo session (where applicable)\n" +
                "That you have reviewed the course or material\n"+
                "That all fees are non-refundable\n"+
                "That you accept these terms fully without dispute\n"+
                "CONTACT US\n\n" +
                "For any queries, feedback or support, contact us at:\n\n"+
                "Email: info@abacustrainer.com\n" +
                "Address: Narsingi, Gandipet, Hyderabad, Telangana";

        SpannableStringBuilder spannable = new SpannableStringBuilder(termsText);
        // Headings list
        String[] headings = {
                "Student Training Fees (Kids Training Programs)",
                "Instructor / Train-the-Trainer Program Fees",
                "Worksheets, Practice Sheets & Digital Downloads",
                "Batch Changes, Class Rescheduling & Missed Classes",
                "Technical Issues",
                "Acceptance of Refund Policy",
                "CONTACT US"
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