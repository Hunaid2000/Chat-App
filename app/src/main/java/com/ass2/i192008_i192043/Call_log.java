package com.ass2.i192008_i192043;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
public class Call_log extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    User user;
    ImageView profile;
    EditText searchContactText;
    ImageView profileImg;
    NavigationView navigationView;
    TextView userName;
    DrawerLayout drawerLayout;
    TabLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_log);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_contacts);
        profileImg = findViewById(R.id.profile_image);
        tableLayout= findViewById(R.id.tab_layout);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        View HeaderView = navigationView.getHeaderView(0);
        userName = HeaderView.findViewById(R.id.userName);
        profile = HeaderView.findViewById(R.id.profileImage);
        user = User.getCurrentUser();
        userName.setText(user.getName());

        profile.setImageBitmap(user.getUserImg());

        searchContactText = findViewById(R.id.search_contacts_text);


        profileImg.setImageBitmap(user.getUserImg());

        TabLayout.Tab tab = tableLayout.getTabAt(2);
        tab.select();




        // add onchange listener to the tab layout
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==0)
                {
                    Intent intent=new Intent(Call_log.this,contactsActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(tab.getPosition()==1)
                {
                    Intent intent=new Intent(Call_log.this,RecordActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    tab.select();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        if(item.getItemId()== R.id.logout) {
                Log.d("logout", "onClick: logout");
                SharedPreferences preferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(Call_log.this, SigninActivity.class);
                startActivity(intent);
                finish();
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}