package com.dst.abacustrainner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.User.WelcomeActivity;


public class SpalashActivity extends AppCompatActivity {
    private static  int SPLASH_SCREEN=3000;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Thread loading = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_SCREEN);
                    Intent main = new Intent(SpalashActivity.this, WelcomeActivity.class);
                    startActivity(main);
                }

                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    finish();
                }
            }
        };
        loading.start();
    }


}