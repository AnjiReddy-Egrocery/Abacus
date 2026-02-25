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

public class PrivacyPolicyActivity extends AppCompatActivity {
    private LinearLayout btnBack;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        btnBack=findViewById(R.id.btn_back_to_home);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(PrivacyPolicyActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        TextView tvTerms = findViewById(R.id.tvPolicy);

        String termsText = "AbacusTrainer.com respect your privacy. This Privacy Policy describes how we collect, use, share and protect personal information or data when you visit our website, access our services or otherwise engage with us. By using the Site, you consent to this Privacy Policy. Handled by Deccan Spark Technologies.\n\n" +
                "Information We Collect\n\n" +
                "We may receive the following types of information:\n\n" +
                "Personal Information You Provide\n\n" +
                "Name\n" +
                "Email address\n" +
                "Phone number (if you include it)\n" +
                "Any other information you decide to provide us (through, for example, contact or registration forms)\n\n" +
                "Usage Information\n\n"+
                "IP address\n"+
                "Browser type and version\n"+
                "Operating system\n"+
                "Pages you visit on our Site\n" +
                "Time and date of access\n" +
                "Time spent on each page\n"+
                "Referring site/exit pages\n" +
                "Device identifiers (if applicable)\n" +
                "Cookies and Tracking Technologies\n\n"+
                "We and our service providers use cookies and similar technologies, such as pixels, tags, device IDs, local storage, or other identifiers, to collect information about how you use our Site (for example, we remember your preferences) as well as to count visits and analyse traffic.\n\n" +
                "Cookies can be controlled through browser settings; however, disabling some cookies may affect your use of parts or all of our Site.\n\n" +
                "How We Use Your Information\n\n"+
                "Maintaining, developing and expanding the Site\n" +
                "Replying to your contact (e.g., contact form inquiries)\n" +
                "We will send you updates, newsletters or other communications (only if you voluntarily subscribe to it)\n" +
                "Enhancing our Site (e.g., by examining how the website is used)\n"+
                "Tracking and analyzing trends in usage and for other user experience needs\n" +
                "Ensuring our Site’s security and integrity\n"+
                "Sharing Your Information\n\n"+
                "We will not sell your data. We may disclose your information in the following circumstances:\n\n" +
                "Service Providers: We may transfer and disclose information to third parties who provide services on our behalf such as hosting providers, e-mail service providers, analytics companies or other service providers.\n\n"+
                "Legal Reasons: We have the right to share your information with the police and /or other authorities if it is required by law, a legal request (such as a subpoena) or in certain other cases.\n" +
                "Business Transfers: If we are involved in a merger, acquisition, or sale of all or a portion of our assets, your information may be transferred as part of that transaction.\n" +
                "Data Retention\n\n"+
                "We will only keep your personal information for as long as it is necessary to fulfil the purposes set out in this Privacy Policy, unless a longer retention period is required or permitted by law. We will delete or anonymize your data securely after it is no longer needed.\n\n" +
                "Security\n\n"+
                "We employ the necessary technical and organizational measures to safeguard your information. The security of your Personal Information is important to us, but remember that no method of transmission over the Internet, or method of electronic storage, is 100% secure.\n\n" +
                "International Data Transfers\n\n"+
                "If you are accessing our Site from outside India (or any other country), please be aware that your" +
                " information may be transferred to, stored in facilities operated and maintained by or on behalf of us, " +
                "and we use such a transfer to provide the Services. If you use our Site, you agree to such transfer and " +
                "the processing of your data according to the terms contained in this Policy.\n\n" +
                "Your Rights\n\n" +
                "The collection and use of personal data used on or in connection with this Website " +
                "is governed by applicable privacy legislation. Depending on the country you reside in, " +
                "you may have certain rights that relate to your personal information, including\n\n" +
                "Request to access or delete your personal data\n"+
                "The right to restrict or object to our processing of your data.\n"+
                "The right to withdraw consent if processing is based on consent.\n"+
                "The right to testify a complaint with a data protection authority (if applicable)\n"+
                "If you would like to use any of your rights, then please email us under the Contact Us\n\n" +
                "Children’s Privacy\n\n"+
                "Our services are child-centred with worksheets, study materials and learning resources for kids. " +
                "Our vision is to keep children’s personal information safe and promote a safe environment for learning.\n" +
                "We don’t ask for personal information unless we truly need it to give you a premium download." +
                " No personal information (including a child's name, address or location) is entered without " +
                "the parent/guardian's permission, except where required for enrolment of any courses/products.\n\n" +
                "Changes to This Privacy Policy\n\n"+
                "We can revise this Privacy Policy from time to time. If and when we do, we will post the revised date " +
                "at the top of this page and alert you of such changes. We suggest that you periodically check this" +
                " policy for any modifications.\n\n" +
                "CONTACT US\n\n" +
                "For any queries, feedback or support, contact us at:\n\n"+
                "Email: info@abacustrainer.com\n" +
                "Address: Narsingi, Gandipet, Hyderabad, Telangana";

        SpannableStringBuilder spannable = new SpannableStringBuilder(termsText);
        // Headings list
        String[] headings = {
                "Information We Collect",
                "Cookies and Tracking Technologies",
                "How We Use Your Information",
                "Sharing Your Information",
                "Data Retention",
                "Security",
                "International Data Transfers",
                "Your Rights",
                "Children’s Privacy",
                "Changes to This Privacy Policy",
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