package com.dst.abacustrainner.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.Model.CartManager;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;

public class CoursesActivity extends AppCompatActivity {

    Button btnPurchase1, btnPurchase2, btnPurchase3;
    Button btnSubscribejunior, btnSubscribeSenior, btnSubscribeVedic;
    LinearLayout layoutCourseBack;
   // LinearLayout layoutAccordionJunior, layoutAccordionSenior, layoutAccordionVedic;
    TextView tvJunior, tvSenior, tvVedic;


    String selectedDuration = "";

    String studentId, batchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // Buttons
        btnPurchase1 = findViewById(R.id.btnPurchase1);
        btnPurchase2 = findViewById(R.id.btnPurchase2);
        btnPurchase3 = findViewById(R.id.btnPurchase3);

        // Layouts
        layoutCourseBack = findViewById(R.id.layout_course_back);


        // TextViews


        // Intent data
        studentId = getIntent().getStringExtra("studentId");
        batchId = getIntent().getStringExtra("batchId");

        Log.d("Reddy", "studentId=" + studentId + " batchId=" + batchId);


        //setupAccordion();
        //setupSubscribeButtons();
        setupBack();

        btnPurchase1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursesActivity.this,CourseDetailActivity.class);
                intent.putExtra("course_name", "Abacus Junior"); // MUST
                startActivity(intent);
            }
        });


        btnPurchase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursesActivity.this,CourseDetailActivity.class);
                intent.putExtra("course_name", "Abacus Senior");
                startActivity(intent);
            }
        });

        btnPurchase3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoursesActivity.this,CourseDetailActivity.class);
                intent.putExtra("course_name", "Vedic Maths");
                startActivity(intent);
            }
        });
    }


    // ================= ACCORDION =================
   /* private void setupAccordion() {
        btnPurchase1.setOnClickListener(v -> toggle(layoutAccordionJunior));
        btnPurchase2.setOnClickListener(v -> toggle(layoutAccordionSenior));
        btnPurchase3.setOnClickListener(v -> toggle(layoutAccordionVedic));
    }
*/
/*    private void toggle(LinearLayout target) {
        layoutAccordionJunior.setVisibility(View.GONE);
        layoutAccordionSenior.setVisibility(View.GONE);
        layoutAccordionVedic.setVisibility(View.GONE);

        target.setVisibility(View.VISIBLE);
    }*/

    // ================= SUBSCRIBE =================
    private void setupSubscribeButtons() {

        btnSubscribejunior.setOnClickListener(v -> openDetails("Abacus Junior"));
        btnSubscribeSenior.setOnClickListener(v -> openDetails("Abacus Senior"));
        btnSubscribeVedic.setOnClickListener(v -> openDetails("Vedic Maths"));
    }

    private void openDetails(String course) {


        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra("course_name", course);

        startActivity(intent);
    }

    // ================= BACK =================
    private void setupBack() {
        layoutCourseBack.setOnClickListener(v -> goHome());
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("studentId", studentId);
        intent.putExtra("batchId", batchId);
        startActivity(intent);
        finish();
    }

    // ================= CART COUNTS =================
 /*   @Override
    protected void onResume() {
        super.onResume();
        updateSelectedLevelBar();
    }*/

    /*private void updateSelectedLevelBar() {
        CartManager cart = CartManager.getInstance(getApplicationContext());

        int junior = 0, senior = 0, vedic = 0;

        for (String level : cart.getAllSelectedLevels("live")) {
            if (level.contains("₹50")) junior++;
            else if (level.contains("₹70")) senior++;
            else if (level.contains("₹100")) vedic++;
        }

        tvJunior.setText("Selected: " + junior);
        tvSenior.setText("Selected: " + senior);
        tvVedic.setText("Selected: " + vedic);
    }*/
}
