package com.ass2.i192008_i192043;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgetPasswordActivity extends AppCompatActivity {
    ImageView back;
    EditText email_input;
    AppCompatButton handleForgetPassword;
    EditText password_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove top blue bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_forgetpassword);
        back = findViewById(R.id.back);
        email_input = findViewById(R.id.email_input);
        password_input= findViewById(R.id.password_input);
        handleForgetPassword = findViewById(R.id.handleForgetPassword);

        // back to sign In
        back.setOnClickListener(v -> {
            finish();
        });

        handleForgetPassword.setOnClickListener(v -> {
            if (email_input.getText().toString().isEmpty() || password_input.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter all feild", Toast.LENGTH_SHORT).show();
            } else {
                // Volley request
                UpdatePassword(email_input.getText().toString(), password_input.getText().toString());
            }
        });
    }

    public void UpdatePassword( String phoneNumber, String password){
        StringRequest request=new StringRequest(
                Request.Method.POST,
                "https://chitchatsmd.000webhostapp.com/updateUserPassword.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            if(obj.getInt("code")==1)
                            {
                                Toast.makeText(ForgetPasswordActivity.this,"Sucess Updated", Toast.LENGTH_LONG).show();
                                finish();
                            }
                            else{
                                Toast.makeText(ForgetPasswordActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ForgetPasswordActivity.this,"Please enter correct Number", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ForgetPasswordActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                params.put("phoneNumber",phoneNumber);
                params.put("password",password);
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(ForgetPasswordActivity.this);
        queue.add(request);
    }
}