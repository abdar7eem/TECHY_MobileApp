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

    private static final int SPLASH_DURATION = 4000; // total duration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView gifLogo = findViewById(R.id.gifLogo);

        // Load the GIF
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading_logo)
                .into(gifLogo);

        // Start the pulse animation
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pup_up);
        gifLogo.startAnimation(pulse);

        // After ~5 seconds, switch to zoom-in animation, then go to main
        new Handler().postDelayed(() -> {
            gifLogo.clearAnimation(); // stop pulse

            Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.exit_top);
            gifLogo.startAnimation(zoomIn);

            // Delay starting MainActivity to let zoom finish
            new Handler().postDelayed(() -> {
                startActivity(new Intent(splash_screen.this, MainActivity.class));
                finish();
            }, 800); // match zoom_in duration

        }, 4000); // wait ~5.2s before zoom-in
    }
}
