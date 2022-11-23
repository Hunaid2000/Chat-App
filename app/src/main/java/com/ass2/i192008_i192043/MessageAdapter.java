package com.ass2.i192008_i192043;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int msg_receiver = 0;
    public static final int msg_sender = 1;
    private List<Message> messagesList;
    private Context context;
    User user;

    public MessageAdapter(List<Message> messagesList, Context context) {
        this.messagesList = messagesList;
        this.context = context;
        user = user.getCurrentUser();
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
        String messageId = messagesList.get(position).getMessageId();
        String message = messagesList.get(position).getMessagetxt();
        String sender = messagesList.get(position).getSender();
        String receiver = messagesList.get(position).getReceiver();
        String time = messagesList.get(position).getTime();
        holder.msg.setText(message);
        holder.msg_time.setText(time);

        if (messagesList.get(position).getSender().equals(user.getUserId())) {
            try {
                Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + sender+".jpg").fit().centerCrop().into(holder.sent_prof_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + receiver+".jpg").fit().centerCrop().into(holder.recv_prof_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + receiver+".jpg").fit().centerCrop().into(holder.sent_prof_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + sender+".jpg").fit().centerCrop().into(holder.recv_prof_pic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (messagesList.get(position).getSender().equals(user.getUserId())) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Message Action");
                    builder.setMessage("Select an action to perform on this message");
                    builder.setPositiveButton("Edit", (dialog, which) -> {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(context);
                        builder2.setTitle("Edit Message");
                        builder2.setMessage("Type the new message");
                        EditText msg = new EditText(context);
                        builder2.setView(msg);
                        builder2.setPositiveButton("Edit", (dialog2, which2) -> {
                            String msgtxt = msg.getText().toString();
                            if (msgtxt.isEmpty()) {
                                Toast.makeText(context, "Please enter new message", Toast.LENGTH_SHORT).show();
                            } else {
                                StringRequest request = new StringRequest(
                                        Request.Method.POST,
                                        "https://chitchatsmd.000webhostapp.com/updateMessage.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject obj=new JSONObject(response);
                                                    if(obj.getInt("code")==1)
                                                    {
                                                        messagesList.get(position).setMessagetxt(msgtxt);
                                                        notifyDataSetChanged();
                                                        Toast.makeText(context,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                    else{
                                                        Toast.makeText(context,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(context,"Incorrect JSON", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(context,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                {
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params=new HashMap<>();
                                        params.put("messageId", messageId);
                                        params.put("message", msgtxt);
                                        return params;
                                    }
                                };
                                RequestQueue queue = Volley.newRequestQueue(context);
                                queue.add(request);
                            }
                        });

                        builder2.setNegativeButton("Cancel", (dialog2, which2) -> {
                            dialog2.dismiss();
                        });
                        builder2.show();
                    });

                    builder.setNegativeButton("Delete", (dialog, which) -> {
                        StringRequest request = new StringRequest(
                                Request.Method.POST,
                                "https://chitchatsmd.000webhostapp.com/deleteMessage.php",
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject obj=new JSONObject(response);
                                            if(obj.getInt("code")==1)
                                            {
                                                // remove the message from the list
                                                messagesList.remove(position);
                                                notifyDataSetChanged();
                                                Toast.makeText(context,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                Toast.makeText(context,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(context,"Incorrect JSON", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(context,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                                    }
                                })
                        {
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params=new HashMap<>();
                                params.put("messageId", messageId);
                                return params;
                            }
                        };
                        RequestQueue queue = Volley.newRequestQueue(context);
                        queue.add(request);
                    });

                    builder.setNeutralButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.show();

                }
            });
        }

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
        if (messagesList.get(position).getSender().equals(user.getUserId())) {
            return msg_sender;
        } else {
            return msg_receiver;
        }
    }

    public void setList(List<Message> newList) {
        this.messagesList = newList;
        notifyDataSetChanged();
    }

}

