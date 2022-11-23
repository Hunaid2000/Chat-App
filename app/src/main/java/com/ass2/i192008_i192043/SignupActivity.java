package com.ass2.i192008_i192043;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.onesignal.OneSignal;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    EditText name, phno, password, bio;
    String gender = "";
    RelativeLayout genderImage1, genderImage2, genderImage3;
    AppCompatButton signupButton;
    RelativeLayout profileImageHolder;
    ImageView profileImage;
    Uri dpp;
    User user;
    TextView showPassword;
    Calendar calendar= Calendar.getInstance();
    SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_signup);
        name = findViewById(R.id.name);
        phno = findViewById(R.id.phno);
        password = findViewById(R.id.password);
        bio = findViewById(R.id.bio);
        user = new User();

        genderImage1 = findViewById(R.id.gender_male);
        genderImage2 = findViewById(R.id.gender_female);
        genderImage3 = findViewById(R.id.gender_other);


        // sign up button
        signupButton = findViewById(R.id.sign_up);
        profileImage = findViewById(R.id.dp);
        profileImageHolder = findViewById(R.id.profile_image);

        AddEventListenerForGender();
        showPassword= findViewById(R.id.showPassword);

        // add event listener for show password
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

        // Add sign up button listener
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                if (dpp == null) {
                    Toast toast = Toast.makeText(context, "Please Select the profile picture", Toast.LENGTH_SHORT);
                    toast.show();
                }
                if (ValidateInput()) {
                    // save the user to local storage sqlite
                    user.setName(name.getText().toString());
                    user.setGender(gender);
                    user.setBio(bio.getText().toString());
                    user.setPhno(phno.getText().toString());
                    user.setPassword(password.getText().toString());

                    StringRequest request=new StringRequest(
                        Request.Method.POST,
                        "https://chitchatsmd.000webhostapp.com/userInsert.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj=new JSONObject(response);
                                    if(obj.getInt("code")==1)
                                    {
                                        user.setUserId(String.valueOf(obj.getInt("id")));
                                        // set the current user
                                        uploadUserImage(user.getUserId());
                                    }
                                    else{
                                        Toast.makeText(SignupActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(SignupActivity.this,"Incorrect JSON user Insert", Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(SignupActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                            }
                        })
                    {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params=new HashMap<>();
                            params.put("name",name.getText().toString());
                            params.put("phoneNumber",phno.getText().toString());
                            params.put("password",password.getText().toString());
                            params.put("gender",gender);
                            params.put("bio",bio.getText().toString());
                            params.put("lastSeen", currentTime.format(calendar.getTime()));
                            params.put("onlineStatus", "online");
                            params.put("playerid", OneSignal.getDeviceState().getUserId());
                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                    queue.add(request);

                }
            }
        });
    }

    private void AddEventListenerForGender() {
        // add click listener on genderImage1
        genderImage1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // set background to drawable resource orange file
                        genderImage1.setBackgroundResource(R.drawable.gender_oranger);
                        // set background to drawable resource white file
                        genderImage2.setBackgroundResource(R.drawable.gender_circle);
                        genderImage3.setBackgroundResource(R.drawable.gender_circle);
                        gender = "male";
                    }
                }
        );
        genderImage2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // set background to drawable resource orange file
                        genderImage2.setBackgroundResource(R.drawable.gender_oranger);
                        // set background to drawable resource white file
                        genderImage1.setBackgroundResource(R.drawable.gender_circle);
                        genderImage3.setBackgroundResource(R.drawable.gender_circle);
                        gender = "female";
                    }
                }
        );

        genderImage3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // set background to drawable resource orange file
                        genderImage3.setBackgroundResource(R.drawable.gender_oranger);
                        // set background to drawable resource white file
                        genderImage1.setBackgroundResource(R.drawable.gender_circle);
                        genderImage2.setBackgroundResource(R.drawable.gender_circle);
                        gender = "other";
                    }
                }
        );

        // upload handler
        profileImageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Choose your DP"), 100);
            }
        });
    }


    private boolean ValidateInput() {
        Context context = getApplicationContext();
        if(RegistrationUtils.validateUserInput(name.getText().toString(),password.getText().toString(), phno.getText().toString())){
            Toast toast = Toast.makeText(context, "Please enter correct input", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    public void Sign_In(View view) {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            dpp = data.getData();
            profileImage.setImageURI(dpp);
        }
    }

    public void uploadUserImage(String id){
        try {
            InputStream inputStream = getContentResolver().openInputStream(dpp);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImg = Base64.encodeToString(byteArray, Base64.DEFAULT);
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    "https://chitchatsmd.000webhostapp.com/imageUpload.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj=new JSONObject(response);
                                if(obj.getInt("code")==1)
                                {
                                    Toast.makeText(SignupActivity.this, "User Registered Successfully", Toast.LENGTH_LONG).show();
                                    SharedPreferences putUser = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = putUser.edit();
                                    editor.putString("id", user.getUserId());
                                    editor.putString("name", user.getName());
                                    editor.putString("phoneNumber", user.getPhno());
                                    editor.putString("gender",user.getGender());
                                    editor.putString("bio",user.getBio());
                                    editor.putString("profile",user.getUserId()+".jpg");
                                    editor.putString("image_data", encodedImg);
                                    editor.commit();

                                    User.getCurrentUser().setUserId(user.getUserId());
                                    User.getCurrentUser().setName(user.getName());
                                    User.getCurrentUser().setPhno(user.getPhno());
                                    User.getCurrentUser().setBio(user.getBio());
                                    User.getCurrentUser().setGender(user.getGender());
                                    User.getCurrentUser().setProfileUrl(user.getUserId()+".jpg");
                                    if( !encodedImg.equalsIgnoreCase("") ){
                                        byte[] b = Base64.decode(encodedImg, Base64.DEFAULT);
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                                        User.getCurrentUser().setUserImg(bitmap);
                                    }

                                    Intent intent = new Intent(SignupActivity.this, contactsActivity.class);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(SignupActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // write the response to log
                                Log.d("Id", "Some thing went wrong");
                                e.printStackTrace();
                                Toast.makeText(SignupActivity.this,"Incorrect JSON Image", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SignupActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                        }
                    })
            {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params=new HashMap<>();
                    params.put("id", id);
                    params.put("profileUrl", encodedImg);
                    return params;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
            queue.add(request);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}