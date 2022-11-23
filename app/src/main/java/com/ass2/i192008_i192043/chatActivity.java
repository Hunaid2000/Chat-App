package com.ass2.i192008_i192043;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class chatActivity extends AppCompatActivity {
    User user;
    EditText msg;
    ImageButton send;
    ImageView recvProfileImg;
    TextView recvName, recvStatus;
    RecyclerView chat_rv;
    ImageButton back;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    Calendar calendar= Calendar.getInstance();
    SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");


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
        user = user.getCurrentUser();
        recvName.setText(getIntent().getStringExtra("name"));
        String contactID = getIntent().getStringExtra("contactID");
        String recvProfileUrl = getIntent().getStringExtra("contactImg");

        try {
            Picasso.get().load("https://chitchatsmd.000webhostapp.com/Images/" + recvProfileUrl).fit().centerCrop().into(recvProfileImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringRequest request=new StringRequest(
            Request.Method.GET,
            "https://chitchatsmd.000webhostapp.com/getUserStatus.php?id="+contactID,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj=new JSONObject(response);
                        if(obj.getInt("code")==1)
                        {
                            String status = obj.getString("onlineStatus");
                            if(status.equals("offline"))
                            {
                                recvStatus.setText("Last Seen: " + obj.getString("lastSeen"));
                            }
                            else
                            {
                                recvStatus.setText(status);
                            }
                        }
                        else{
                            Toast.makeText(chatActivity.this, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(chatActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(chatActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                }
            });
        RequestQueue queue= Volley.newRequestQueue(chatActivity.this);
        queue.add(request);

        messages = new ArrayList<>();
        adapter = new MessageAdapter(messages, this);
        chat_rv.setAdapter(adapter);
        chat_rv.setLayoutManager(new LinearLayoutManager(this));


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
                    StringRequest request=new StringRequest(
                        Request.Method.POST,
                        "https://chitchatsmd.000webhostapp.com/messageInsert.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj=new JSONObject(response);
                                    if(obj.getInt("code")==1)
                                    {
                                        msg.setText("");
                                        Toast.makeText(chatActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Log.d("query", obj.getString("query"));
                                        Toast.makeText(chatActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(chatActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(chatActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                            }
                        })
                    {
                        @Nullable
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params=new HashMap<>();
                            params.put("sender", user.getUserId());
                            params.put("receiver", contactID);
                            params.put("msgtime", currentTime.format(calendar.getTime()));
                            params.put("message", message);
                            params.put("msgtype", "1");
                            return params;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(chatActivity.this);
                    queue.add(request);

                }
            }
        });

        getMessages(user.getUserId(), contactID);


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
                                Toast.makeText(chatActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(chatActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(chatActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
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
        RequestQueue queue = Volley.newRequestQueue(chatActivity.this);
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
                                Toast.makeText(chatActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(chatActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(chatActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
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
        RequestQueue queue = Volley.newRequestQueue(chatActivity.this);
        queue.add(request);

    }

    public void getMessages(String sender, String receiver){
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
                                JSONArray messages_arr=obj.getJSONArray("messages");
                                for (int i=0; i<messages_arr.length();i++)
                                {
                                    JSONObject msgObj = messages_arr.getJSONObject(i);
                                    Message message = new Message();
                                    message.setMessagetxt(msgObj.getString("message"));
                                    message.setSender(msgObj.getString("sender"));
                                    message.setReceiver(msgObj.getString("receiver"));
                                    message.setTime(msgObj.getString("msgtime"));
                                    messages.add(message);
                                }
                                adapter.setList(messages);
                                Toast.makeText(chatActivity.this, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(chatActivity.this, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(chatActivity.this,"Incorrect JSON", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(chatActivity.this,"Cannot Connect to the Server", Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue queue= Volley.newRequestQueue(chatActivity.this);
        queue.add(request);
    }

}
