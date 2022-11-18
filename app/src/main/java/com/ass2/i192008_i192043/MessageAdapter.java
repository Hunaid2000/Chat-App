package com.ass2.i192008_i192043;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int msg_receiver = 0;
    public static final int msg_sender = 1;
    private List<Message> messagesList;
    private Context context;

    public MessageAdapter(List<Message> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == msg_sender) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_chat_item, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recv_chat_item, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String message = messagesList.get(position).getMessagetxt();
        String sender = messagesList.get(position).getSender();
        String receiver = messagesList.get(position).getReceiver();
        String time = messagesList.get(position).getTime();
        holder.msg.setText(message);
        holder.msg_time.setText(time);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + sender);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                Picasso.get().load(uri).fit().centerCrop().into(holder.sent_prof_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to get sender profile image", Toast.LENGTH_SHORT).show();
        });

        storageReference = FirebaseStorage.getInstance().getReference().child("images/" + receiver);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            try {
                Picasso.get().load(uri).fit().centerCrop().into(holder.recv_prof_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to get receiver profile image", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView msg, msg_time;
        ImageView recv_prof_pic, sent_prof_pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            msg = itemView.findViewById(R.id.msg);
            msg_time = itemView.findViewById(R.id.msg_time);
            recv_prof_pic = itemView.findViewById(R.id.recv_prof_pic);
            sent_prof_pic = itemView.findViewById(R.id.sent_prof_pic);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return msg_sender;
        } else {
            return msg_receiver;
        }
    }
}

