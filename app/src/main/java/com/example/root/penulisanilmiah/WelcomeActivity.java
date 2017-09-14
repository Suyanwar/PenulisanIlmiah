package com.example.root.penulisanilmiah;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class WelcomeActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 3000;
    ImageView splash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        splash = (ImageView)findViewById(R.id.splash);
        ((TransitionDrawable)splash.getDrawable()).startTransition(SPLASH_TIME_OUT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(WelcomeActivity.this, DeviceActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
