package com.ass2.i192008_i192043;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity {
    Animation topAnm, bottomAnm;
    ImageView logo_image;
    TextView logoText;
    User currentUser;
    private static final String ONESIGNAL_APP_ID = "104a1154-86fd-4499-b831-6198e6ccf128";

    private static final int SPLASH_SCREEN= 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        setContentView(R.layout.activity_main);
        loadUser();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        // promptForPushNotifications will show the native Android notification permission prompt.
        // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
        OneSignal.promptForPushNotifications();

        System.out.println("Contact ID: " + OneSignal.getDeviceState().getUserId());

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
            if(previouslyEncodedImage!=null){
                if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                    byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    User.getCurrentUser().setUserImg(bitmap);
                }
            }else{
                User.getCurrentUser().SetUserFromURl("https://chitchatsmd.000webhostapp.com/Images/"+User.getCurrentUser().getProfileUrl());
            }
            currentUser = User.getCurrentUser();
        }
    }

}