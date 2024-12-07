package com.dst.abacustrainner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.dst.abacustrainner.User.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;


public class SpalashActivity extends AppCompatActivity {
    private static  int SPLASH_SCREEN=3000;
    Animation topAnimation,bottomAnimation;

    ImageView imageView;
    TextView textView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topAnimation=AnimationUtils.loadAnimation(SpalashActivity.this,R.anim.animation);
        //bottomAnimation=AnimationUtils.loadAnimation(SpalashActivity.this,R.anim.second_animation);

        imageView=findViewById(R.id.imageView);
        textView=findViewById(R.id.textView_name);


        imageView.setAnimation(topAnimation);
        //textView.setAnimation(bottomAnimation);

        setText("www.abacustrainer.com");

        Thread loading = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_SCREEN);
                    Intent main = new Intent(SpalashActivity.this, LoginActivity.class);
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

    private void setText(String s) {

        final int[] i = new int[1];
        i[0] = 0;
        final int length = s.length();
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                char c= s.charAt(i[0]);
                textView.append(String.valueOf(c));
                i[0]++;
            }
        };
        final Timer timer = new Timer();
        TimerTask taskEverySplitSecond = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                if (i[0] == length - 1) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(taskEverySplitSecond, 1, 100);
    }
}