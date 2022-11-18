package com.ass2.i192008_i192043;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class SelectPlaylist extends AppCompatActivity {
    RelativeLayout add_playlist;
    RecyclerView rv;
    TextView go_back_main;

    LinearLayout playlist;
    FirebaseFirestore db;
    StorageReference storageReference;
    String uid;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_playlist);
        go_back_main=  findViewById(R.id.go_back);
        playlist= findViewById(R.id.playlist_listScroll);

        go_back_main.setOnClickListener(v -> {
            finish();
        });
        db= FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        auth= FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(auth).getUid();
        add_playlist = findViewById(R.id.add_playlist);
        add_playlist.setOnClickListener(v -> {
            Intent intent = new Intent(SelectPlaylist.this, AddPlaylist.class);
            startActivity(intent);
        });
        loadPlaylists(db, uid, storageReference);
    }



    // load the playlist
    private void loadPlaylists(FirebaseFirestore db, String uid, StorageReference ref) {
        if(playlist.getChildCount()>1){
            playlist.removeAllViews();
        }
        int widthHeight = MainplayersActivity.dpToPx(120, this);
        int margin = MainplayersActivity.dpToPx(10, this);
        int paddingImg= MainplayersActivity.dpToPx(20, this);
        // load the playlists from the database
        db.collection("users").document(uid).collection("playlists").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // load the playlists into the linear layout
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    LinearLayout linearLayout = new LinearLayout(this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    linearLayout.setWeightSum(10);
                    linearLayout.setPadding(0, paddingImg, 0, paddingImg);

                    // relative layout
                    RelativeLayout relativeLayout = new RelativeLayout(this);
                    relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f));
                    // text view1
                    TextView textView1 = new TextView(this);
                    textView1.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    textView1.setText(document.getString("Name"));
                    textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    textView1.setTypeface(null, Typeface.BOLD);
                    textView1.setId(View.generateViewId());
                    textView1.setTextColor(getResources().getColor(R.color.orange));


                    // text view2
                    TextView textView2 = new TextView(this);
                    textView2.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    textView2.setText(document.getString("description"));
                    textView2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    textView1.setTextColor(getResources().getColor(R.color.orange));
                    // make the text view2 below the text view1
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView2.getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, textView1.getId());
                    textView2.setLayoutParams(params);
                    relativeLayout.addView(textView1);
                    relativeLayout.addView(textView2);

                    // add the image and text view to the relative layout
                    // add an id to the relative layout
                    ImageView imageView = new ImageView(this);
                    // set layout weight on the image view
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(0, widthHeight, 6f));
                    imageView.setPadding(paddingImg, 0, paddingImg, 0);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    // get the playlist image
                    String playlistImage = document.getString("Icon_Id");
                    ref.child("playlistIcon/" + playlistImage).getDownloadUrl().addOnSuccessListener(uri -> {
                        // fit the image into the image view
                        Picasso.get().load(uri).fit().into(imageView);
                    }).addOnFailureListener(e -> Toast.makeText(SelectPlaylist.this, "Error loading image", Toast.LENGTH_SHORT).show());
                    linearLayout.addView(imageView);
                    linearLayout.addView(relativeLayout);
                    playlist.addView(linearLayout);
                    System.out.println("Playlist loaded");
                    // made a click selection for the playlist
                    linearLayout.setOnClickListener(v -> {
                        Intent intent = new Intent(SelectPlaylist.this, uploadMusicActivity.class);
                        intent.putExtra("playlistId", document.getId());
                        System.out.println("Ids::"+document.getId());
                        startActivity(intent);
                    });
                }
            } else {
                Toast.makeText(SelectPlaylist.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loadPlaylists(db, uid, storageReference);
    }
}