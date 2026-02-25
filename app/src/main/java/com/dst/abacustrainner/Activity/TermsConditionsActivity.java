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

public class TermsConditionsActivity extends AppCompatActivity {
    private LinearLayout btnBack;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(TermsConditionsActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        TextView tvTerms = findViewById(R.id.tvTerms);

        String termsText = "WHAT WE DO\n\n" +
                "These are the terms that apply to use of AbacusTrainer.com Handled by Deccan Spark Technologies. By visiting or using our website, worksheets, " +
                "learning tools, downloads materials, videos and any services relating to the foregoing (Services), you agree to these Terms." +
                " In the event you do not accept, please cease using the Website right now.\n\n" +
                "USE OF WEBSITE & SERVICES\n\n" +
                "AbacusTrainer provides educational content such as training for Kids, Abacus language tools, worksheets, practice material and much more. The Website with its learning resources is for:\n" +
                "\n\n" +
                "Parents\n\n"+
                "Guardians\n\n"+
                "Teachers\n\n"+
                "Students under supervision\n\n"+
                "You agree that you will not use the Website or its resources.\n\n" +

                "Eligibility\n\n" +
                "Minors We do not knowingly collect personal information from children under 13, except that a child may use the Website for purely educational purposes provided a parent, guardian or authorized teacher submits applicable information and such submission is done in accordance with relevant law.\n\n"+

                "INTELLECTUAL PROPERTY RIGHTS\n\n" +
                "All materials, in whatever form (documents and/or downloadable files), i.e. worksheets, images, videos, school books or designs, graphics as well as text, logos and branding are copyrighted and intellectual property of AbacusTrainer.com.\n\n" +
                "You may not reproduce, distribute, modify, create derivative works of, publicly display or sell any content without permission!\n"+
                "Worksheets can be downloaded for personal use, but may not be sold or redistributed in any form.\n\n"+

                "USER CONDUCT\n\n" +
                "You agree NOT to \n\n Post fake, harmful or inappropriate content,\n\n Try to gain unauthorized access to, damage, or deactivate the Website\n\n " +
                "Utilize the material for commercial purposes without permission\n\n Upload material that contains viruses, destructive features, spam, or malicious code\n\n Abuse worksheets, downloads, or course materials\n\n" +
                "Account Registration (If Applicable)\n\n"+
                "If any portion of the Website requires you to open an account:\n\n" +
                "You are responsible for keeping your information up to date.\n\n"+
                "You must keep your login credentials confidential.\n\n"+
                "We can suspend or stop an account found to be violating these Terms.\n\n"+
                "PAYMENTS & REFUND POLICY\n\n" +
                "If the Website provides paid worksheets, courses, subscriptions, or training:\n\n" +
                "All prices are clearly stated on the Website.\n"+
                "All transactions are carried out securely through third-party payment gateways.\n"+
                "All payments are final and non-refundable as students/instructors pay only after attending a demo session and understanding how the course/program works.\n\n"+
                "You will NOT be entitled to a refund for:\n\n" +
                "Student course fees\n"+
                "Instructor training fees\n"+
                "Workbook or worksheet purchases\n"+
                "Digital downloads\n"+
                "Subscriptions or any other paid services\n\n"+

                "Third-Party Links\n\n"+
                "The Website may include links to external sites (tools, resources, payment gateways). We are not liable for the content, privacy, or services on those third-party websites.\n\n" +
                "Children’s Use\n\n"+
                "Because we're a platform built for students:\n" +
                "Kids can access worksheets and practice without typing personal details.\n"+
                "A parent, guardian or instructor must submit any required information for registration, communication, or certificate issuance.\n"+
                "We strive to provide a safe environment for learning.\n\n" +

                "DISCLAIMER OF WARRANTIES\n\n" +
                "Our worksheets, tools, and learning resources are provided without any warranty. We do not guarantee uninterrupted access or error-free data handling.\n" +
                "You use this site at your own risk.\n\n"+

                "Limitation of Liability\n\n"+
                "AbacusTrainer.com, to the maximum extent permitted by law, shall NOT be liable for:\n\n" +
                "Direct, indirect, incidental, consequential or punitive damages\n"+
                "Lost data, profits, or business interruption\n"+
                "Problems caused by misuse of content or materials\n"+
                "You agree to use the Website properly and at your own risk.\n\n" +

                "Indemnification\n\n"+
                "You further agree to indemnify and hold AbacusTrainer.com (or any third party) from all liabilities, claims, damages and expenses, including reasonable lawyers' fees and costs made against.\n\n"+
                "Your misuse of the Website\n"+
                "Violation of these Terms\n"+
                "Violation of third-party rights\n\n"+

                "Changes to Terms\n\n"+
                "We may change or revise these Terms at any time.\n\n" +
                "The “Effective Date” shall be amended accordingly. Your continued use of the Website following any changes constitutes your acceptance of the new Terms.\n\n"+

                "CONTACT US\n\n" +
                "For any queries, feedback or support, contact us at:\n\n"+
                "Email: info@abacustrainer.com\n" +
                "Address: Narsingi, Gandipet, Hyderabad, Telangana";

        SpannableStringBuilder spannable = new SpannableStringBuilder(termsText);
        // Headings list
        String[] headings = {
                "WHAT WE DO",
                "USE OF WEBSITE & SERVICES",
                "Eligibility",
                "Intellectual Property Rights",
                "User Conduct",
                "Account Registration (If Applicable)",
                "Payments, Purchases & Refund Policy",
                "Third-Party Links",
                "Children’s Use",
                "Disclaimer of Warranties",
                "Limitation of Liability",
                "Indemnification",
                "Changes to Terms",
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