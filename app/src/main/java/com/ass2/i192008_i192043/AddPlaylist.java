package com.ass2.i192008_i192043;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class AddPlaylist extends AppCompatActivity {
    RelativeLayout selectPlayIcon;
    EditText playlist_name;
    EditText playlist_desc;
    AppCompatButton add_playlist;
    ImageView uploadedImageIcon;
    ImageView arrow_back;
    Uri dpp;
    FirebaseAuth mAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addplaylist);
        selectPlayIcon = findViewById(R.id.upload_playlistIcon);
        playlist_name = findViewById(R.id.playlist_name);
        playlist_desc = findViewById(R.id.playlist_desc);
        add_playlist = findViewById(R.id.add_playlist);
        uploadedImageIcon = findViewById(R.id.shit_design);
        arrow_back = findViewById(R.id.arrow_back);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // select icon as a Image in selectPlayIcon
        selectPlayIcon.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Choose the playlist Icon"), 100);
        });

        arrow_back.setOnClickListener(v -> {
            finish();
        });


        add_playlist.setOnClickListener(v -> {
                    Context context = getApplicationContext();
                    if (playlist_name.getText().toString().isEmpty()) {
                        Toast toast = Toast.makeText(context, "Please Enter playlistName", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    } else if (playlist_desc.getText().toString().isEmpty()) {
                        Toast toast = Toast.makeText(context, "Please Enter playlistDesc", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    } else if (dpp == null) {
                        Toast toast = Toast.makeText(context, "Please Select playlistIcon", Toast.LENGTH_SHORT);
                        toast.show();
                        return;
                    }
                    if(user!=null){
                        AddPlaylist_Db(user.getUid());
                    }else{
                        Toast toast = Toast.makeText(context, "Please Login First", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
        );
    }


    private void AddPlaylist_Db(String userId){
        Context context = getApplicationContext();
        // add playlist to database
        FirebaseStorage storage;
        StorageReference storageReference;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        String id = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("playlistIcon/" + id+"/");
        ref.putFile(dpp).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                // store the data in the database firebase
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> playlist = new HashMap<>();
                playlist.put("Name", playlist_name.getText().toString());
                playlist.put("description", playlist_desc.getText().toString());
                playlist.put("Icon_Id", id);

                // add playlist to database base on user id
                db.collection("users").document(userId).collection("playlists").add(playlist)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast toast = Toast.makeText(context, "Playlist Added", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast toast = Toast.makeText(context, "Playlist Not Added", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: ");
                Toast toast = Toast.makeText(context, "Error saving the playlist", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }


    // get the selected image and set it to selectPlayIcon
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            dpp = data.getData();
            uploadedImageIcon.setImageURI(dpp);
        }
    }
}