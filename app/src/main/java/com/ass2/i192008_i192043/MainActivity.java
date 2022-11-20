package com.ass2.i192008_i192043;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

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
        getUser("1");
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

    public void getUser(String id){
        StringRequest request=new StringRequest(
            Request.Method.GET,
            "https://chitchatsmd.000webhostapp.com/getUserById.php?userId="+id,
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
                        }
                        else{
                            Toast.makeText(MainActivity.this, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                }
            });
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        queue.add(request);
    }
}