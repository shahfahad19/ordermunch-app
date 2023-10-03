package com.app.ordermunch.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.app.ordermunch.R;
import com.orhanobut.hawk.Hawk;

public class SplashActivity extends AppCompatActivity {


    private static final int SPLASH_DURATION = 500; // Splash screen duration in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hawk.init(this).build();


        // Hide the status bar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_splash);



        ImageView imageViewSplash = findViewById(R.id.imageViewSplash);

        // Apply animation to the ImageView
        Animation splashAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        imageViewSplash.startAnimation(splashAnimation);

        // Navigate to the main activity after the splash duration
        new Handler().postDelayed(() -> {
            Intent intent;
            String jwtToken = Hawk.get("jwtToken", "");
            if (jwtToken.isEmpty()) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}