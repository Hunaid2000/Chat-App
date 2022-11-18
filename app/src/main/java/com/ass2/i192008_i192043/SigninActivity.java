package com.ass2.i192008_i192043;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity {

    AppCompatButton btn_signin;
    TextView txt_signup;
    FirebaseAuth auth;
    EditText email, password;
    TextView showPassword;
    TextView forgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_signin);

        btn_signin = findViewById(R.id.signIn_btn);
        txt_signup = findViewById(R.id.sign_up);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);
        showPassword= findViewById(R.id.show_password);
        forgetPassword= findViewById(R.id.forgot_password);
        auth= FirebaseAuth.getInstance();

        showPassword.setOnClickListener(v -> {
            String str1 = showPassword.getText().toString();
            if (str1.equalsIgnoreCase("show")) {
                // change the type of edittext to text
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPassword.setText("hide");
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPassword.setText("show");
            }
        });

        // SignIn Button listener
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get user with the email and password
                String emailStr = email.getText().toString();
                String passwordStr = password.getText().toString();
                if(emailStr.isEmpty() || passwordStr.isEmpty()) {
                    Toast.makeText(SigninActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                auth.signInWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(SigninActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SigninActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SigninActivity.this, MainplayersActivity.class);
                            startActivity(intent);
                        } else {
                            System.out.println("Sign in failed");
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigninActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

            }
        });

        // SignUp TextView listener
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });


        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SigninActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

}
