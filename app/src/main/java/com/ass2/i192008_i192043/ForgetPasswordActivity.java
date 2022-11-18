package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgetPasswordActivity extends AppCompatActivity {
    ImageView back;
    EditText email_input;
    AppCompatButton handleForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        back = findViewById(R.id.back);
        email_input = findViewById(R.id.email_input);
        handleForgetPassword = findViewById(R.id.handleForgetPassword);

        // back to sign In
        back.setOnClickListener(v -> {
            finish();
        });

        handleForgetPassword.setOnClickListener(v -> {
            if (email_input.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                // Firebase forget password
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email_input.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgetPasswordActivity.this, "Email Send, reset it now your", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgetPasswordActivity.this, "Please try with correct emails", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }
}