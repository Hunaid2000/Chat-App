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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private List<User> contactsList;
    private Context context;
    String contactId;
    String userId = User.getCurrentUser().getUserId();

    public ContactsAdapter(List<User> contactsList, Context context) {
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
        String contactName = contactsList.get(position).getName();
        contactId = contactsList.get(position).getUserId();
//        String contactTime = contactsList.get(position).getLastSeen();
        String contactImg = contactsList.get(position).getProfileUrl();
        getLastMessage(userId, contactId, holder.contactLastMsg, holder.contactTime);
        holder.contactName.setText(contactName);
        try {
            Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + contactImg).fit().centerCrop().into(holder.contactImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,chatActivity.class);
                intent.putExtra("name", holder.contactName.getText().toString());
                intent.putExtra("contactID", contactsList.get(position).getUserId());
                intent.putExtra("contactImg", contactImg);
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

    public void getLastMessage(String sender, String receiver, TextView lastMsg, TextView lastMsgTime) {
        StringRequest request=new StringRequest(
                Request.Method.GET,
                "https://chitchatsmd.000webhostapp.com/getMessages.php?sender="+sender + "&receiver=" + receiver,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj=new JSONObject(response);
                            if(obj.getInt("code")==1)
                            {
                                List<Message> messages = new ArrayList<>();
                                JSONArray messages_arr=obj.getJSONArray("messages");
                                for (int i=0; i<messages_arr.length();i++)
                                {
                                    JSONObject msgObj = messages_arr.getJSONObject(i);
                                    Message message = new Message();
                                    message.setMessageId(msgObj.getString("messageId"));
                                    if(msgObj.getString("message").contains("jpg"))
                                    {
                                        message.setMessagetxt("Image");
                                    }
                                    else
                                    {
                                        message.setMessagetxt(msgObj.getString("message"));
                                    }
                                    message.setSender(msgObj.getString("sender"));
                                    message.setReceiver(msgObj.getString("receiver"));
                                    message.setTime(msgObj.getString("msgtime"));
                                    messages.add(message);
                                }
                                lastMsg.setText(messages.get(messages.size()-1).getMessagetxt());
                                lastMsgTime.setText(messages.get(messages.size()-1).getTime());
                            }
                            else{

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
                });
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }

    public void setList(List<User> newList) {
        this.contactsList = newList;
        notifyDataSetChanged();
    }


}

