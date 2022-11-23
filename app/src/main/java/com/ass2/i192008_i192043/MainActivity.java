package com.ass2.i192008_i192043;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
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
    User currentUser;

    private static final int SPLASH_SCREEN= 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        setContentView(R.layout.activity_main);
        loadUser();

        // Animation
        topAnm   = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnm= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // load image and logo
        logo_image= findViewById(R.id.logo_icon);
        logoText= findViewById(R.id.logo_text);

        // Assign the animation
        logo_image.setAnimation(topAnm);
        logoText.setAnimation(bottomAnm);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (currentUser != null) {
                    // intent to the main player activity
                    intent = new Intent(MainActivity.this, contactsActivity.class);
                }else{
                    // intent to the login activity
                    intent = new Intent(MainActivity.this, SigninActivity.class);

                }
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    };


    public void   loadUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String userId = sharedPreferences.getString("id", null);
        // if user is not logged in
        if (userId == null) {
            currentUser = null;
            return;
        }else{
            User.getCurrentUser().setUserId(userId);
            User.getCurrentUser().setName(sharedPreferences.getString("name", null));
            User.getCurrentUser().setPhno(sharedPreferences.getString("phoneNumber", null));
            User.getCurrentUser().setBio(sharedPreferences.getString("bio", null));
            User.getCurrentUser().setGender(sharedPreferences.getString("gender",null));
            User.getCurrentUser().setProfileUrl(sharedPreferences.getString("profile",null));

            String previouslyEncodedImage = sharedPreferences.getString("image_data", null);
            if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                User.getCurrentUser().setUserImg(bitmap);
            }
            currentUser = User.getCurrentUser();
        }
    }

}