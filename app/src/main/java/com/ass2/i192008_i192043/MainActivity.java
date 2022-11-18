package com.ass2.i192008_i192043;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    Animation topAnm, bottomAnm;
    ImageView logo_image;
    TextView logoText;
    private static final int SPLASH_SCREEN= 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        setContentView(R.layout.activity_main);

        // Animation
        topAnm   = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnm= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // load image and logo
        logo_image= findViewById(R.id.logo_icon);
        logoText= findViewById(R.id.logo_text);

        // Assign the animation
        logo_image.setAnimation(topAnm);
        logoText.setAnimation(bottomAnm);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (currentUser != null) {
                    // intent to the main player activity
                    intent = new Intent(MainActivity.this, MainplayersActivity.class);
                }else{
                    // intent to the login activity
                    intent = new Intent(MainActivity.this, loginActivity.class);

                }
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    };
}