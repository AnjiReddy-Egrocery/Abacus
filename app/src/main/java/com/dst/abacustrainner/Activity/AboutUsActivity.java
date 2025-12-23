package com.dst.abacustrainner.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

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
    }
}