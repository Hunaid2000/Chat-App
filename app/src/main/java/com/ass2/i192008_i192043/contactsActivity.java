package com.ass2.i192008_i192043;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class contactsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    User user;
    ImageView profile;
    RecyclerView contacts_rv;
    ImageButton addContact;
    EditText searchContactText;
    ImageView profileImg;
    ArrayList<User> contacts;
    ContactsAdapter adapter;
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
        addContact = findViewById(R.id.add_contact);

        // tab input for contacts
        ViewPager viewPager=findViewById(R.id.view_paper);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new chatsFragment(),"Chats");
        viewPagerAdapter.addFragment(new GroupsFragment(),"Groups");
        viewPager.setAdapter(viewPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
        profileImg.setImageBitmap(user.getUserImg());

        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(contacts, this);
        contacts_rv.setAdapter(adapter);
        contacts_rv.setLayoutManager(new LinearLayoutManager(this));


        StringRequest request=new StringRequest(
            Request.Method.GET,
            "https://chitchatsmd.000webhostapp.com/getContactsById.php?userId="+user.getUserId(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj=new JSONObject(response);
                        if(obj.getInt("code")==1)
                        {
                            JSONArray users=obj.getJSONArray("users");
                            for (int i=0; i<users.length();i++)
                            {
                                JSONObject userObj = users.getJSONObject(i);
                                User contact = new User();
                                contact.setName(userObj.getString("name"));
                                contact.setUserId(userObj.getString("userId"));
                                contact.setProfileUrl(contact.getUserId()+".jpg");
                                contacts.add(contact);
                            }
                            adapter.setList(contacts);
                            Toast.makeText(contactsActivity.this, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(contactsActivity.this, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
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
            });
        RequestQueue queue= Volley.newRequestQueue(contactsActivity.this);
        queue.add(request);

        addContact.setOnClickListener(v -> {
            addContactDailogbox();
        });

        searchContactText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = s.toString().toLowerCase();
                ArrayList<User> filteredContacts = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getName().toLowerCase().contains(search.toLowerCase())) {
                        filteredContacts.add(contacts.get(i));
                    }
                }

                if(filteredContacts.isEmpty() && !search.isEmpty()){
                    Toast.makeText(contactsActivity.this, "No contacts found", Toast.LENGTH_SHORT).show();
                }
                adapter.setList(filteredContacts);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // add onchange listener to the tab layout
        tableLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition()==1)
                {
                    Intent intent=new Intent(contactsActivity.this,RecordActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(tab.getPosition()==2)
                {
                    Intent intent=new Intent(contactsActivity.this,Call_log.class);
                    startActivity(intent);
                    finish();
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


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String>titles;

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
                        if(obj.getInt("code")==1)
                        {
                        }
                        else{
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
                        if(obj.getInt("code")==1)
                        {

                        }
                        else{
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
                editor.commit();
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