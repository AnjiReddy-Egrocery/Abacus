package com.dst.abacustrainner.User;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dst.abacustrainner.R;


public class ForgotPasswordActivity extends AppCompatActivity {

    Button butSend;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        butSend=findViewById(R.id.but_send);

        butSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ForgotPasswordActivity.this,VerifyActivity.class);
                startActivity(intent);
            }
        });
    }
}