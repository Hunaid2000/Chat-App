package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.ImageView;

public class voicecallActivity extends AppCompatActivity {

    ImageView hangupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voicecall);
        hangupButton = findViewById(R.id.hangupButton);
        hangupButton.setOnClickListener(v -> {
            finish();
        });
    }
}