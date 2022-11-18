package com.ass2.i192008_i192043;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignupActivity extends AppCompatActivity {
    EditText firstName, lastName, email, password, bio;
    String str = "";
    RelativeLayout genderImage1, genderImage2, genderImage3;
    AppCompatButton signupButton;
    RelativeLayout profileImageHolder;
    ImageView profileImage;
    Uri dpp;
    FirebaseAuth mAuth;
    FirebaseUser User;
    TextView showPassword;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_signup);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        bio = findViewById(R.id.bio);

        genderImage1 = findViewById(R.id.gender_male);
        genderImage2 = findViewById(R.id.gender_female);
        genderImage3 = findViewById(R.id.gender_other);


        // sign up button
        signupButton = findViewById(R.id.sign_up);
        profileImage = findViewById(R.id.dp);
        profileImageHolder = findViewById(R.id.profile_image);
        mAuth = FirebaseAuth.getInstance();

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
                    User user = new User();
                    user.setFirstName(firstName.getText().toString());
                    user.setLastName(lastName.getText().toString());
                    user.setGender(str);
                    user.setBio(bio.getText().toString());
                    FirebaseStorage storage;
                    StorageReference storageReference;
                    storage = FirebaseStorage.getInstance();
                    storageReference = storage.getReference();

                    // create a user in firebase
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("TAG", "createUserWithEmail:success");
                                        User = mAuth.getCurrentUser();
                                        String str= "";
                                        // upload the image to firebase storage
                                        StorageReference ref = storageReference.child("images/" + User.getUid());
                                        ref.putFile(dpp)
                                                .addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                                                        // Get a URL to the uploaded content
                                                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                // save the user to firebase firestore
                                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                                Map<String, Object> user1 = new HashMap<>();
                                                                user.setProfileUrl(dpp.toString());
                                                                user1.put("firstName", user.getFirstName());
                                                                user1.put("lastName", user.getLastName());
                                                                user1.put("gender", user.getGender());
                                                                user1.put("bio", user.getBio());
                                                                // save user1 to db
                                                                db.collection("users").document(User.getUid())
                                                                        .set(user1)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                                                                Intent intent = new Intent(SignupActivity.this, MainplayersActivity.class);
                                                                                startActivity(intent);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.w("TAG", "Error writing document", e);
                                                                            }
                                                                        });
                                                            }
                                                        });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                    }
                                                });
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignupActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
                        str = "male";
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
                        str = "female";
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
                        str = "other";
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
        if (firstName.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(context, "Please enter the first name", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if (lastName.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(context, "Please enter the last name", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if (email.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(context, "Please enter the email", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if (password.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(context, "Please enter the password", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if (str.isEmpty()) {
            Toast toast = Toast.makeText(context, "Please select the gender", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        if (bio.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(context, "Please enter the bio", Toast.LENGTH_SHORT);
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
}