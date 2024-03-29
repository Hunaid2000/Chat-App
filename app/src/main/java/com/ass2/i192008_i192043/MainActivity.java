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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    Animation topAnm, bottomAnm;
    ImageView logo_image;
    TextView logoText;
    User currentUser;
    private static final String ONESIGNAL_APP_ID = "0ea59906-b1a9-4034-b5be-c0f50eba5c9b";
    private boolean isNotificationOpened = false;
    private static final int SPLASH_SCREEN= 3000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OneSignal.setNotificationOpenedHandler(
            new OneSignal.OSNotificationOpenedHandler() {
                @Override
                public void notificationOpened(OSNotificationOpenedResult result) {
                    String name=null, contactID=null, profileUrl=null, isMessageNotification=null;
                    try {
                        isMessageNotification = result.getNotification().getAdditionalData().getString("isMessageNotification");
                        if(isMessageNotification.equals("1")){
                            name = result.getNotification().getAdditionalData().getString("name");
                            contactID = result.getNotification().getAdditionalData().getString("contactID");
                            profileUrl = result.getNotification().getAdditionalData().getString("profileUrl");
                            Intent intent = new Intent(MainActivity.this, chatActivity.class);
                            intent.putExtra("name", name);
                            intent.putExtra("contactID", contactID);
                            intent.putExtra("contactImg", profileUrl);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            isNotificationOpened = true;
                            startActivity(intent);
                        }
                        else {
                            name = result.getNotification().getAdditionalData().getString("name");
                            Intent intent = new Intent(MainActivity.this, IncomingCall.class);
                            intent.putExtra("name", name);
                            isNotificationOpened = true;
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            });
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        setContentView(R.layout.activity_main);
        loadUser();

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
        OneSignal.promptForPushNotifications();

        // Animation
        topAnm   = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnm= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // load image and logo
        logo_image= findViewById(R.id.logo_icon);
        logoText= findViewById(R.id.logo_text);

        // Assign the animation
        logo_image.setAnimation(topAnm);
        logoText.setAnimation(bottomAnm);
        if(!isNotificationOpened){
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
        }

    };


    public void  loadUser(){
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