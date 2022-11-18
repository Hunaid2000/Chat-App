package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

public class uploadMusicActivity extends AppCompatActivity {
    AppCompatButton record_music;
    String PlaylistId;
    ImageView arrow_back;
    RelativeLayout upload_music_holder;
    Uri musicUri;
    EditText title;
    EditText genre;
    EditText song_desc;
    AppCompatButton upload_record;
    FirebaseFirestore db;
    StorageReference storageReference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadmusic);
        musicUri= null;
        record_music= findViewById(R.id.record_music);
        arrow_back  = findViewById(R.id.arrow_back);
        upload_music_holder = findViewById(R.id.upload_music_holder);
        // get the id of the playlist from intent extra
        PlaylistId =  getIntent().getStringExtra("playlistId");
        title= findViewById(R.id.title);
        genre= findViewById(R.id.genre);
        song_desc= findViewById(R.id.song_desc);
        upload_record= findViewById(R.id.upload_record);
        db= FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        mAuth= FirebaseAuth.getInstance();
        addOnclickListener();
    }

    // Add OnclickListener
    private  void addOnclickListener(){
        record_music.setOnClickListener(v->{
            Intent intent = new Intent(uploadMusicActivity.this, recordActivity.class);
            intent.putExtra("playlistId", PlaylistId);
            startActivity(intent);
        });
        arrow_back.setOnClickListener(v->{
            finish();
        });
        // upload a music from the device
        upload_music_holder.setOnClickListener(v->{
            //  upload a music from the device
            Intent i = new Intent();
            i.setType("audio/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i,1);
        });

        upload_record.setOnClickListener(v->{
            if(validateInput()){
                System.out.println("Validated");
                // upload the music to the firebase
                uploadMusic();
            }
        });

    }

    private  void uploadMusic(){
        String uid= mAuth.getUid();
        StorageReference ref= storageReference.child("music/"+ uid+"/"+ PlaylistId+"/"+ UUID.randomUUID().toString());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        ref.putFile(musicUri).addOnSuccessListener(taskSnapshot -> {
            ref.getDownloadUrl().addOnSuccessListener(uri -> {
                // get the song url
                String songUrl= uri.toString();
                // print the song url on the console
                System.out.println("Song Url"+ songUrl);
                // add the music to the playlist
                Map<String, Object> music= new HashMap<>();
                music.put("title", title.getText().toString());
                music.put("genre", genre.getText().toString());
                music.put("song_desc", song_desc.getText().toString());
                music.put("songUrl", songUrl);

                assert uid != null;
                db.collection("users").document(uid).collection("playlists").document(PlaylistId)
                        .collection("music").add(music).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(uploadMusicActivity.this, "Music uploaded successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            });
        }).addOnProgressListener(snapshot -> {
            double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
            progressDialog.setMessage("Progress: " + (int) progressPercent + "%");
        }).addOnFailureListener(e -> {
            Toast.makeText(uploadMusicActivity.this, "Failed to upload music", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });
    }

    private boolean validateInput(){
        Context context = getApplicationContext();
        if(title.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(genre.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a genre", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(song_desc.getText().toString().isEmpty()){
            Toast.makeText(context, "Please enter a description", Toast.LENGTH_SHORT).show();
            return  false;
        }
        if(musicUri==null){
            Toast.makeText(context, "Please upload a music", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }


    // on activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            // get the uri of the music
             musicUri=  data.getData();
        }
    }


    // override onRestart
    @Override
    public void onRestart() {
        super.onRestart();
    }
}