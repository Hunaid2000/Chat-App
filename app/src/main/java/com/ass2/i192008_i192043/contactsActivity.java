package com.ass2.i192008_i192043;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class contactsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    User user;
    ImageView profile;
    ImageView profileImg;
    Calendar calendar= Calendar.getInstance();
    NavigationView navigationView;
    TextView userName;
    DrawerLayout drawerLayout;
    TabLayout tableLayout;

    SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        // remove top blue bar
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

        // tab input for contacts
        ViewPager viewPager=findViewById(R.id.view_paper);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new chatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new GroupsFragment(),"Groups");
        viewPager.setAdapter(viewPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
        profileImg.setImageBitmap(user.getUserImg());
    }


    static class ViewPagerAdapter extends FragmentPagerAdapter {

        final private ArrayList<Fragment> fragments;
        final private ArrayList<String>titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        StringRequest request = new StringRequest(
            Request.Method.POST,
            "https://chitchatsmd.000webhostapp.com/updateUserStatus.php",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj=new JSONObject(response);
                        if(obj.getInt("code")!=1)
                        {
                            Toast.makeText(contactsActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(contactsActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(contactsActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                }
            })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params=new HashMap<>();
                String lastSeen = currentTime.format(calendar.getTime());
                params.put("id", user.getUserId());
                params.put("lastSeen", lastSeen);
                params.put("onlineStatus", "online");
                user.setLastSeen(lastSeen);
                user.setStatus("online");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(contactsActivity.this);
        queue.add(request);

    }

    @Override
    protected void onPause() {
        super.onPause();

        StringRequest request = new StringRequest(
            Request.Method.POST,
            "https://chitchatsmd.000webhostapp.com/updateUserStatus.php",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj=new JSONObject(response);
                        if(obj.getInt("code")!=1)
                        {
                            Toast.makeText(contactsActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(contactsActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(contactsActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                }
            })
        {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params=new HashMap<>();
                String lastSeen = currentTime.format(calendar.getTime());
                params.put("id", user.getUserId());
                params.put("lastSeen", lastSeen);
                params.put("onlineStatus", "offline");
                user.setLastSeen(lastSeen);
                user.setStatus("offline");
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(contactsActivity.this);
        queue.add(request);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.logout: {
                Log.d("logout", "onClick: logout");
                SharedPreferences preferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(contactsActivity.this, SigninActivity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
        //close navigation drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}