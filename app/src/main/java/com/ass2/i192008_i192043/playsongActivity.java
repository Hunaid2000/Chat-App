package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class playsongActivity extends AppCompatActivity {

    String PlaylistId;
    ImageView arrow_back;
    ListView playlist_holder;
    ArrayList<String> arrayListSongName;
    ArrayList<String> arrayListSongUrl;
    ArrayAdapter<String> arrayAdapter;
    FirebaseFirestore db;
    FirebaseAuth mauth;
    JcPlayerView jcplayerView;
    ArrayList<JcAudio> jcAudios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playsong);
        arrow_back = findViewById(R.id.arrow_back);
        playlist_holder = findViewById(R.id.playlist_holder);
        jcplayerView= findViewById(R.id.jc_player);
        PlaylistId = getIntent().getStringExtra("playlistId");
        System.out.println("Id_s: " + PlaylistId);
        arrayListSongName = new ArrayList<>();
        arrayListSongUrl = new ArrayList<>();
        jcAudios = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mauth = FirebaseAuth.getInstance();
        addEventListeners();
        LoadSong();

        playlist_holder.setOnItemClickListener((parent, view, position, id) -> {
            jcplayerView.playAudio(jcAudios.get(position));
            jcplayerView.setVisibility(View.VISIBLE);
            jcplayerView.createNotification();
        });
    }


    private void LoadSong() {
        // add value event listener to get the data from firebase
        db.collection("users").document(mauth.getCurrentUser().getUid()).collection("playlists").
                document(PlaylistId).collection("music").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String title = document.getString("title") ;
                            String url = document.getString("songUrl");

                            if(title != null && url != null){
                                arrayListSongName.add(title);
                                arrayListSongUrl.add(url);
                                jcAudios.add(JcAudio.createFromURL(title, url));
                            }
                        }
                        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListSongName){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                                textView.setSingleLine(true);
                                textView.setMaxLines(1);
                                return view;
                            }
                        };
                        playlist_holder.setAdapter(arrayAdapter);
                        jcplayerView.initPlaylist(jcAudios, null);
                    }
                });
    }


    private void addEventListeners() {
        arrow_back.setOnClickListener(v -> {
            finish();
        });
    }
}