package com.example.assignment1;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class splash_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView gifLogo = findViewById(R.id.gifLogo);

        Glide.with(this)
                .asGif()
                .load(R.drawable.loading_logo)
                .into(gifLogo);

        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pup_up);
        gifLogo.startAnimation(pulse);

        new Handler().postDelayed(() -> {
            gifLogo.clearAnimation();

            Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.exit_top);
            gifLogo.startAnimation(zoomIn);

            new Handler().postDelayed(() -> {
                startActivity(new Intent(splash_screen.this, MainActivity.class));
                finish();
            }, 800);

        }, 4000);
    }
}
