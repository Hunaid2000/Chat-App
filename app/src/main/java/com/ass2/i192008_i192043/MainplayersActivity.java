package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.Objects;

public class MainplayersActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ImageView menu_toggle;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView profile;
    TextView userName;
    LinearLayout playlist_listScroll;
    FirebaseFirestore db;
    StorageReference storageReference;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainplayers);
        menu_toggle = findViewById(R.id.menu_toggle);
        bottomNavigationView = findViewById(R.id.bottom_menu);
        menu_toggle = findViewById(R.id.menu_toggle);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        View HeaderView = navigationView.getHeaderView(0);
        userName = HeaderView.findViewById(R.id.userName);
        profile = HeaderView.findViewById(R.id.profileImage);
        playlist_listScroll = findViewById(R.id.playlist_listScroll);

        User user = new User();
        // find the user information from the database firebase->firestorm->users->user id->user information
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        storageReference = (FirebaseStorage.getInstance()).getReference();
        StorageReference ref = storageReference.child("images/" + uid);
        // get information from the firebaseFirestore
        loadProfile(db, uid, ref, user);
        // add the drawer layout listener
        addDrawerLayoutListener();
        // Handle drawer layout toggle on the button
        drawerHandler();
        // click listener on the bottom navigation
        bottomNavigationHandle();
        // load data for the playlists
        loadPlaylists(db, uid, storageReference);
    }

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void loadPlaylists(FirebaseFirestore db, String uid, StorageReference ref) {
        // check if playlist is empty in playlist_listScroll
        if (playlist_listScroll.getChildCount() > 1) {
           playlist_listScroll.removeAllViews();
        }
        int widthHeight = dpToPx(100, this);
        int margin = dpToPx(10, this);
        // load the playlists from the database
        db.collection("users").document(uid).collection("playlists").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // load the playlists into the linear layout
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    RelativeLayout relativeLayout = new RelativeLayout(this);
                    relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    relativeLayout.setPadding(0, 0, margin, 0);
                    // add an id to the relative layout
                    relativeLayout.setId(View.generateViewId());
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(new RelativeLayout.LayoutParams(widthHeight, widthHeight));
                    // add an id to the image view
                    imageView.setId(View.generateViewId());
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    // get the playlist image
                    String playlistImage = document.getString("Icon_Id");
                    ref.child("playlistIcon/" + playlistImage).getDownloadUrl().addOnSuccessListener(uri -> {
                        // fit the image into the image view
                        Picasso.get().load(uri).fit().into(imageView);
                    });
                    TextView textView = new TextView(this);
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                    textView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                    // layout below the image
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, imageView.getId());
                    textView.setText(document.getString("Name"));
                    textView.setTextColor(getResources().getColor(R.color.orange, null));
                    // add the image and text view to the relative layout
                    relativeLayout.addView(imageView);
                    relativeLayout.addView(textView);
                    playlist_listScroll.addView(relativeLayout);
                    relativeLayout.setOnClickListener(v -> {
                        // go to the playlist activity
                        Intent intent = new Intent(MainplayersActivity.this, playsongActivity.class);
                        intent.putExtra("playlistId", document.getId());
                        startActivity(intent);
                    });
                }
            } else {
                Toast.makeText(MainplayersActivity.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addDrawerLayoutListener() {
        // set the user information in the navigation drawer
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.userName:
                    break;
            }
            return true;
        });
    }


    private void loadProfile(FirebaseFirestore db, String uid, StorageReference ref, User user) {
        db.collection("users").document(uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // get the user information
                user.setName(task.getResult().getString("firstName"));
                user.setGender(task.getResult().getString("gender"));
                user.setBio(task.getResult().getString("bio"));
                userName.setText(user.getName());
                // get profile image from the firebase storage
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    // fit the image in the circle imageView dimensions using the picasso
                    try {
                        Picasso.get().load(uri).fit().centerCrop().into(profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainplayersActivity.this, "Failed to get profile image", Toast.LENGTH_SHORT).show();
                });
            } else {
                // if the user information is not found
                System.out.println("Error getting documents: " + task.getException());
                Toast.makeText(getApplicationContext(), "Error getting documents", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void drawerHandler() {
        menu_toggle.setOnClickListener(v -> {
            // check if the drawer is open or not
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void bottomNavigationHandle() {
        // add event listener to bottom navigation
        bottomNavigationView.setOnItemSelectedListener(item -> {
            // log to console
            System.out.println(item.getItemId());
            System.out.println(item.getItemId());


            if (item.getItemId() == R.id.add) {
                Intent intent = new Intent(MainplayersActivity.this, SelectPlaylist.class);
                startActivity(intent);

            } else if (item.getItemId() == R.id.favorite) {
                Intent intent= new Intent(MainplayersActivity.this, LikedSong.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.search) {
                Intent intent= new Intent(MainplayersActivity.this, SearchSong.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.watchLater) {
                Intent intent= new Intent(MainplayersActivity.this, ListenLater.class);
                startActivity(intent);
            }
            return true;
        });

    }
}