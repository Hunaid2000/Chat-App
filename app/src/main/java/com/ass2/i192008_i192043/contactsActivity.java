package com.ass2.i192008_i192043;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jean.jcplayer.model.JcAudio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class contactsActivity extends AppCompatActivity {
    User user;
    RecyclerView contacts_rv;
    ImageButton addContact;
    EditText searchContactText;
    ImageView profileImg;
    ArrayList<User> contacts;
    ContactsAdapter adapter;
    FirebaseAuth mAuth;
    String uid;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        contacts_rv = findViewById(R.id.contacts_rv);
        profileImg = findViewById(R.id.profile_image);
        addContact = findViewById(R.id.add_contact);
        searchContactText = findViewById(R.id.search_contacts_text);
        user = user.getCurrentUser();
        user.setProfileUrl(user.getUserId()+".jpg");
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        try {
            Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + user.getProfileUrl()).fit().centerCrop().into(profileImg);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uid);
//        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
//            // fit the image in the circle imageView dimensions using the picasso
//            try {
//                Picasso.get().load(uri).fit().centerCrop().into(profileImg);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).addOnFailureListener(e -> {
//            Toast.makeText(contactsActivity.this, "Failed to get profile image", Toast.LENGTH_SHORT).show();
//        });

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
                                JSONArray users=obj.getJSONArray("user");
                                for (int i=0; i<users.length();i++)
                                {
                                    JSONObject userObj = obj.getJSONObject("user");
                                    User contact = new User();
                                    contact.setName(userObj.getString("name"));
                                    contact.setUserId(userObj.getString("userId"));
                                    contacts.add(contact);
                                }
                                adapter.notifyDataSetChanged();
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

                if(filteredContacts.isEmpty()){
                    Toast.makeText(contactsActivity.this, "No contacts found", Toast.LENGTH_SHORT).show();
                }
                adapter.setFilteredList(filteredContacts);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void addContactDailogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Contact");
        builder.setMessage("Enter the Phone Number of the user you want to add");
        EditText contact_no = new EditText(this);
        builder.setView(contact_no);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String phno = contact_no.getText().toString();
            if (phno.isEmpty()) {
                Toast.makeText(this, "Please enter Phone Number", Toast.LENGTH_SHORT).show();
            } else {
                StringRequest request=new StringRequest(
                    Request.Method.GET,
                    "https://chitchatsmd.000webhostapp.com/getUserbyPhno.php?phoneNumber="+phno,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                if(obj.getInt("code")==1)
                                {
                                    JSONObject userObj = obj.getJSONObject("user");
                                    User contact = new User();
                                    contact.setName(userObj.getString("name"));
                                    contact.setUserId(userObj.getString("userId"));
                                    contact.setPhno(userObj.getString("phoneNumber"));
                                    contact.setBio(userObj.getString("bio"));
                                    contact.setGender(userObj.getString("gender"));

                                    StringRequest request=new StringRequest(
                                            Request.Method.POST,
                                            "https://chitchatsmd.000webhostapp.com/contactInsert.php",
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject obj=new JSONObject(response);
                                                        if(obj.getInt("code")==1)
                                                        {
                                                            contacts.add(contact);
                                                            adapter.notifyDataSetChanged();
                                                            Toast.makeText(contactsActivity.this, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                        }
                                                        else{
                                                            Log.d("error1",obj.getString("e1"));
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
                                            params.put("userId", user.getUserId());
                                            params.put("connectionId", contact.getUserId());
                                            return params;
                                        }
                                    };
                                    RequestQueue queue = Volley.newRequestQueue(contactsActivity.this);
                                    queue.add(request);

                                }
                                else{
                                    Toast.makeText(contactsActivity.this, "Incorrect Phone Number", Toast.LENGTH_LONG).show();
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
//                db.collection("users").get().addOnCompleteListener(task1 -> {
//                    if (task1.isSuccessful()) {
//                        for (DocumentSnapshot document : task1.getResult()) {
//                            String email_user = document.getString("email");
//                            if (email_user.equals(emailStr)) {
//                                String contactUid = document.getId();
//                                Contact contact = new Contact();
//                                contact.setEmail(emailStr);
//                                contact.setName(document.getString("firstName"));
//                                contact.setBio(document.getString("bio"));
//                                contact.setGender(document.getString("gender"));
//                                contact.setProfileUrl(document.getId());
//                                db.collection("users").document(uid).collection("contacts").document(contactUid).set(contact);
//                                contacts.add(contact);
//                                adapter.notifyDataSetChanged();
//                                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();
//
//                            }
//                        }
//                    }
//                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        db.collection("users").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                for (DocumentSnapshot document : task1.getResult()) {
                    if (uid != document.getId()) {
                        String contactUid = document.getId();
                        db.collection("users").document(contactUid).collection("contacts").document(uid).update("status", "online");
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.collection("users").get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                for (DocumentSnapshot document : task1.getResult()) {
                    if (uid != document.getId()) {
                        String contactUid = document.getId();
                        db.collection("users").document(contactUid).collection("contacts").document(uid).update("status", "offline");
                    }
                }
            }
        });
    }
}