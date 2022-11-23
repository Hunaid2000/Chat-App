package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class ScreenShotActivity extends AppCompatActivity {

    static Bitmap bitmap;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_shot);
        imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}