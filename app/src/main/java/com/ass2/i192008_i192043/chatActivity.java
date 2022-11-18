package com.ass2.i192008_i192043;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class chatActivity extends AppCompatActivity {
    EditText msg;
    ImageButton send;
    ImageView recvProfileImg;
    TextView recvName, recvStatus;
    RecyclerView chat_rv;
    ImageButton back;
    FirebaseAuth mAuth;
    String uid;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        msg = findViewById(R.id.msg);
        send = findViewById(R.id.send);
        back = findViewById(R.id.back_bt);
        chat_rv = findViewById(R.id.chats_rv);
        recvProfileImg = findViewById(R.id.recv_prof_pic_top);
        recvName = findViewById(R.id.recv_name);
        recvStatus = findViewById(R.id.recv_status);
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getCurrentUser().getUid();
        recvName.setText(getIntent().getStringExtra("name"));
        String contactID = getIntent().getStringExtra("contactID");

        db.collection("users").document(uid).collection("contacts").document(contactID).get().addOnSuccessListener(documentSnapshot -> {
            recvStatus.setText(documentSnapshot.getString("status"));
        });

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this);
        chat_rv.setAdapter(adapter);
        chat_rv.setLayoutManager(new LinearLayoutManager(this));

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + contactID);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                Picasso.get().load(uri).fit().centerCrop().into(recvProfileImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(chatActivity.this, "Failed to get profile image", Toast.LENGTH_SHORT).show();
        });

        back.setOnClickListener(v -> {
            startActivity(new Intent(chatActivity.this, contactsActivity.class));
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = msg.getText().toString();
                if (message.isEmpty()) {
                    Toast.makeText(chatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                } else {
                    Message message_obj = new Message(msg.getText().toString(), uid, contactID);
                    db.collection("users").document(uid).collection("contacts").document(contactID).collection("messages").add(message_obj);
                    db.collection("users").document(contactID).collection("contacts").document(uid).collection("messages").add(message_obj);

                }
            }
        });


        db.collection("users").document(uid).collection("contacts").document(contactID).collection("messages").addSnapshotListener((value, error) -> {
            messages.clear();
            for (int i = 0; i < value.size(); i++) {
                Message message = new Message();
                message.setMessagetxt(value.getDocuments().get(i).getString("messagetxt"));
                message.setSender(value.getDocuments().get(i).getString("sender"));
                message.setReceiver(value.getDocuments().get(i).getString("receiver"));
                message.setTime(value.getDocuments().get(i).getString("time"));
                messages.add(message);
            }
            adapter.notifyDataSetChanged();
        });

    }
}