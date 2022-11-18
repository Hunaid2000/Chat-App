package com.ass2.i192008_i192043;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class contactsActivity extends AppCompatActivity {
    RecyclerView contacts_rv;
    ImageButton addContact;
    EditText searchContactText;
    ImageView profileImg;
    ArrayList<Contact> contacts;
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
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + uid);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // fit the image in the circle imageView dimensions using the picasso
            try {
                Picasso.get().load(uri).fit().centerCrop().into(profileImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(contactsActivity.this, "Failed to get profile image", Toast.LENGTH_SHORT).show();
        });

        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(contacts, this);
        contacts_rv.setAdapter(adapter);
        contacts_rv.setLayoutManager(new LinearLayoutManager(this));


        db.collection("users").document(uid).collection("contacts").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < task.getResult().size(); i++) {
                    Contact contact = task.getResult().getDocuments().get(i).toObject(Contact.class);
                    contacts.add(contact);
                }
                adapter.notifyDataSetChanged();
            }
        });

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
                ArrayList<Contact> filteredContacts = new ArrayList<>();
                for (int i = 0; i < contacts.size(); i++) {
                    if (contacts.get(i).getFirstName().toLowerCase().contains(search.toLowerCase()) || contacts.get(i).getLastName().toLowerCase().contains(search.toLowerCase())) {
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
        builder.setMessage("Enter the Contact Number of the user you want to add");
        EditText contact_no = new EditText(this);
        builder.setView(contact_no);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String emailStr = contact_no.getText().toString();
            if (emailStr.isEmpty()) {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show();
            } else {
                db.collection("users").get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        for (DocumentSnapshot document : task1.getResult()) {
                            String email_user = document.getString("email");
                            if (email_user.equals(emailStr)) {
                                String contactUid = document.getId();
                                Contact contact = new Contact();
                                contact.setEmail(emailStr);
                                contact.setFirstName(document.getString("firstName"));
                                contact.setLastName(document.getString("lastName"));
                                contact.setBio(document.getString("bio"));
                                contact.setGender(document.getString("gender"));
                                contact.setProfileUrl(document.getId());
                                db.collection("users").document(uid).collection("contacts").document(contactUid).set(contact);
                                contacts.add(contact);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });
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