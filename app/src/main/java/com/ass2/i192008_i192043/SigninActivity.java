package com.ass2.i192008_i192043;

import android.content.Context;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

public class SigninActivity extends AppCompatActivity {

    AppCompatButton btn_signin;
    TextView txt_signup;
    EditText email, password;
    TextView showPassword;
    TextView forgetPassword;
    User User;
    Context context;

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
        context= this;


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
                getUserByEmailPassword(emailStr, passwordStr);

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

    public void getUserByEmailPassword( String phoneNumber, String password){
        String url= "https://chitchatsmd.000webhostapp.com/getUserByContactPassword.php?password="+password+ "&phoneNumber="+phoneNumber;
        System.out.println("Url::"+ url );
        StringRequest request=new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            if(obj.getInt("code")==1)
                            {
                                JSONObject user=obj.getJSONObject("user");
                                User.getCurrentUser().setName(user.getString("name"));
                                User.getCurrentUser().setUserId(user.getString("userId"));
                                User.getCurrentUser().setPhno(user.getString("phoneNumber"));
                                User.getCurrentUser().setBio(user.getString("bio"));
                                User.getCurrentUser().setGender(user.getString("gender"));
                                Toast.makeText(SigninActivity.this, "Sign in success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SigninActivity.this, contactsActivity.class);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(SigninActivity.this, "Incorrect Number or Password", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SigninActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SigninActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue queue= Volley.newRequestQueue(SigninActivity.this);
        queue.add(request);
    }

}
