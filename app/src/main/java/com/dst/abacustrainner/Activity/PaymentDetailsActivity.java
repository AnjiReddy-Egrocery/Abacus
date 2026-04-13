package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dst.abacustrainner.Model.StudentRegistationResponse;
import com.dst.abacustrainner.R;
import com.dst.abacustrainner.User.HomeActivity;
import com.dst.abacustrainner.User.StudentDetailsActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentDetailsActivity extends AppCompatActivity {

    TextView txtTransction,txtstatus,txtCurrency,txtAmount,txtmethod,txtDate;

    String transction,status,currency,amount,method,date,studentId ,errorcode;
    Button butDashboard;
    TextView txtResult;

    ArrayList<StudentRegistationResponse.Result> studentList;
    String imageUrl;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        txtTransction = findViewById(R.id.txt_transction);
        txtstatus = findViewById(R.id.txt_status);
        txtCurrency = findViewById(R.id.txt_currency);
        txtAmount = findViewById(R.id.txt_amount);
        txtmethod = findViewById(R.id.txt_pay_method);
        txtDate = findViewById(R.id.txt_date);
        butDashboard = findViewById(R.id.btndashboard);
        txtResult = findViewById(R.id.txt_result);

        transction=getIntent().getStringExtra("Transaction");
        status = getIntent().getStringExtra("Status");
        currency = getIntent().getStringExtra("Currency");
        amount = getIntent().getStringExtra("Amount");
        method= getIntent().getStringExtra("Payment");
        date=getIntent().getStringExtra("Date");
        studentId = getIntent().getStringExtra("StudentId");
        errorcode = getIntent().getStringExtra("ErrorCode");

        studentList = (ArrayList<StudentRegistationResponse.Result>)
                getIntent().getSerializableExtra("studentList");

        imageUrl = getIntent().getStringExtra("imageUrl");

        long timestamp = 0;

        if (date != null && !date.isEmpty()) {
            try {
                timestamp = Long.parseLong(date) * 1000; // seconds → ms
            } catch (Exception e) {
                Log.e("Anji", "Date parse error: " + e.getMessage());
            }
        } else {
            Log.e("Anji", "Date is null or empty");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String formattedDate = sdf.format(new Date(timestamp));

        Log.d("Anji", "Formatted Date: " + formattedDate);

        if ("COMPLETED".equalsIgnoreCase(status)) {

            txtResult.setText("Payment Completed Successfully");
            txtResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));

        }
        else if ("FAILED".equalsIgnoreCase(status)) {

            txtResult.setText("Payment Failed");

            if (errorcode != null) {
                txtResult.append("\nReason: " + errorcode);
            }

            txtResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }

        txtTransction.setText(transction);
        txtstatus.setText(status);
        txtCurrency.setText(currency);
        txtAmount.setText(amount);
        txtmethod.setText(method);
        txtDate.setText(formattedDate);

        butDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentDetailsActivity.this, HomeActivity.class);
                intent.putExtra("StudentId", studentId);
                intent.putExtra("navigate_to", "home");
                startActivity(intent);
                finish();

            }
        });


    }
}