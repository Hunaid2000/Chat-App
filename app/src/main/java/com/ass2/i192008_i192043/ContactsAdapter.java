package com.ass2.i192008_i192043;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<Contact> contactsList;
    private Context context;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ContactsAdapter(List<Contact> contactsList, Context context) {
        this.contactsList = contactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String contactName = contactsList.get(position).getFirstName() + " " + contactsList.get(position).getLastName();
        String contactTime = contactsList.get(position).getLastSeen();
        String contactImg = contactsList.get(position).getProfileUrl();
        getLastMessage(contactImg, holder.contactLastMsg, holder.contactTime);
        holder.contactName.setText(contactName);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + contactImg);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                Picasso.get().load(uri).fit().centerCrop().into(holder.contactImg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to get profile image", Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,chatActivity.class);
                intent.putExtra("name", holder.contactName.getText().toString());
                intent.putExtra("contactID", contactImg);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView contactName, contactTime, contactLastMsg;
        ImageView contactImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            contactName = itemView.findViewById(R.id.contact_name);
            contactTime = itemView.findViewById(R.id.contact_time);
            contactImg = itemView.findViewById(R.id.contact_img);
            contactLastMsg = itemView.findViewById(R.id.contact_last_msg);
        }
    }

    public void getLastMessage(String contactID, TextView lastMsg, TextView lastMsgTime) {
        db.collection("users").document(uid).collection("contacts").document(contactID).collection("messages").orderBy("time", Query.Direction.DESCENDING).limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    lastMsg.setText(document.getString("messagetxt"));
                    lastMsgTime.setText(document.getString("time"));
                }
            }
        });
    }

    public void setFilteredList(List<Contact> newList) {
        this.contactsList = newList;
        notifyDataSetChanged();
    }

}

