package com.ass2.i192008_i192043;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.MotionEvent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.jraska.falcon.Falcon;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class chatActivity extends AppCompatActivity {
    User user;
    EditText msg;
    ImageButton send;
    ImageView recvProfileImg;
    TextView recvName, recvStatus;
    RecyclerView chat_rv;
    ImageButton back, selectImage, voiceRecord;
    ArrayList<Message> messages;
    MessageAdapter adapter;
    Calendar calendar= Calendar.getInstance();
    SimpleDateFormat currentTime=new SimpleDateFormat("hh:mm a");
    ImageView screen_shot;
    String contactID;
    private static int MIC_PERMISSION_CODE = 200;
    private static int RECORDING_NUMBER = 0;
    MediaRecorder mediaRecorder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove top blue bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        setContentView(R.layout.activity_chat);
        msg = findViewById(R.id.msg);
        selectImage = findViewById(R.id.camera);
        send = findViewById(R.id.send);
        back = findViewById(R.id.back_bt);
        chat_rv = findViewById(R.id.chats_rv);
        voiceRecord = findViewById(R.id.record);
        recvProfileImg = findViewById(R.id.recv_prof_pic_top);
        recvName = findViewById(R.id.recv_name);
        recvStatus = findViewById(R.id.recv_status);
        user = user.getCurrentUser();
        recvName.setText(getIntent().getStringExtra("name"));
        contactID = getIntent().getStringExtra("contactID");
        String recvProfileUrl = getIntent().getStringExtra("contactImg");
        screen_shot= findViewById(R.id.screen_shot); //added

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


        Thread t = new Thread() {
            @Override
            public void run() {
                if (isMicrophoneAvailable()) {
                    getMicrophonePermission();
                }
            }
        };
        t.start();

         voiceRecord.setOnTouchListener(new View.OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 switch (event.getAction()) {
                     case MotionEvent.ACTION_DOWN:
                         startRecording();
                         Toast.makeText(chatActivity.this, "Recording Started", Toast.LENGTH_SHORT).show();
                         break;
                     case MotionEvent.ACTION_UP:
                         stopRecording();
                         Toast.makeText(chatActivity.this, "Recording Stopped", Toast.LENGTH_SHORT).show();
                         break;
                 }
                 return false;
             }
         });



        back.setOnClickListener(v -> {
            startActivity(new Intent(chatActivity.this,contactsActivity.class));
        });

        screen_shot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = Falcon.takeScreenshotBitmap(chatActivity.this);
                ScreenShotActivity.bitmap = bitmap;
                Intent intent = new Intent(chatActivity.this, ScreenShotActivity.class);
                startActivity(intent);
                finish();
            }
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

                                        Message msg = new Message();
                                        msg.setMessageId(obj.getString("id"));
                                        msg.setMessagetxt(message);
                                        msg.setSender(user.getUserId());
                                        msg.setReceiver(contactID);
                                        msg.setTime(obj.getString("msgtime"));
                                        msg.setMsgtype("1");
                                        messages.add(msg);
                                        adapter.setList(messages);

                                        sendNotification(message);
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

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(i, "Choose Image"), 100);

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
                                    message.setMessageId(msgObj.getString("messageId"));
                                    message.setMessagetxt(msgObj.getString("message"));
                                    message.setSender(msgObj.getString("sender"));
                                    message.setReceiver(msgObj.getString("receiver"));
                                    message.setTime(msgObj.getString("msgtime"));
                                    message.setMsgtype(msgObj.getString("msgtype"));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            List<String> encodedImgs = new ArrayList<>();
            if(data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for(int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    InputStream inputStream = null;
                    try {
                        inputStream = getContentResolver().openInputStream(imageUri);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    encodedImgs.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
                }
            }
            else {
                Uri imageUri = data.getData();
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                encodedImgs.add(Base64.encodeToString(byteArray, Base64.DEFAULT));
            }

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();

            RequestQueue queue;
            for (String encodedImage : encodedImgs) {
                StringRequest request=new StringRequest(
                        Request.Method.POST,
                        "https://chitchatsmd.000webhostapp.com/imgMessageInsert.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj=new JSONObject(response);
                                    if(obj.getInt("code")==1)
                                    {
                                        progressDialog.dismiss();
                                        Toast.makeText(chatActivity.this,obj.get("msg").toString(), Toast.LENGTH_LONG).show();

                                        Message msg = new Message();
                                        msg.setMessageId(obj.getString("id"));
                                        msg.setMessagetxt(obj.getString("message"));
                                        msg.setSender(user.getUserId());
                                        msg.setReceiver(contactID);
                                        msg.setTime(obj.getString("msgtime"));
                                        msg.setMsgtype("2");
                                        messages.add(msg);
                                        adapter.setList(messages);

                                        sendNotification("Image");
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
                        params.put("sender", user.getUserId());
                        params.put("receiver", contactID);
                        String msgtime = currentTime.format(calendar.getTime());
                        params.put("msgtime", msgtime);
                        params.put("message", "_"+user.getUserId()+"_"+contactID+"_"+msgtime+".jpg");
                        params.put("msgtype", "2");
                        params.put("img", encodedImage);
                        return params;
                    }
                };
                queue = Volley.newRequestQueue(chatActivity.this);
                queue.add(request);
            }
            encodedImgs.clear();

        }

    }


    private void startRecording() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            RECORDING_NUMBER++;
            mediaRecorder.setOutputFile(getRecordingFilePath());
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String encodeRecording(){
        File file = new File(getRecordingFilePath());
        byte[] bytes = new byte[(int) file.length()];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.release();

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Recording...");
        progressDialog.show();

        String encodedAudio = encodeRecording();
        StringRequest request=new StringRequest(
            Request.Method.POST,
            "https://chitchatsmd.000webhostapp.com/recordingMessageInsert.php",
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject obj=new JSONObject(response);
                        if(obj.getInt("code")==1)
                        {
                            progressDialog.dismiss();
                            Toast.makeText(chatActivity.this,obj.getString("msg"), Toast.LENGTH_LONG).show();

                            Message msg = new Message();
                            msg.setMessageId(obj.getString("id"));
                            msg.setMessagetxt(obj.getString("message"));
                            msg.setSender(user.getUserId());
                            msg.setReceiver(contactID);
                            msg.setTime(obj.getString("msgtime"));
                            msg.setMsgtype("3");
                            messages.add(msg);
                            adapter.setList(messages);

                            sendNotification("Recording");
                        }
                        else{
                            Toast.makeText(chatActivity.this,obj.getString("msg"), Toast.LENGTH_LONG).show();
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
                String msgtime = currentTime.format(calendar.getTime());
                params.put("msgtime", msgtime);
                params.put("message", "_"+user.getUserId()+"_"+contactID+"_"+msgtime+".mp3");
                params.put("msgtype", "3");
                params.put("recording", encodedAudio);
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(chatActivity.this);
        queue.add(request);

        mediaRecorder = null;
    }

    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "recording" + (RECORDING_NUMBER) + ".mp3");
        return file.getPath();
    }

    private void getMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_CODE);
        }
    }

    private boolean isMicrophoneAvailable() {
        return this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
    }

    public void sendNotification(String message){
        JSONObject json = null;
        try {
            json= new JSONObject("{'app_id':'0ea59906-b1a9-4034-b5be-c0f50eba5c9b'," +
                    "'include_external_user_ids': [ '" + contactID + "' ]," +
                    "'contents': { 'en' : '"+message+"' } ," +
                    " 'headings' :{'en':'Message From "+user.getName()+"'} }");
            json.put("large_icon", "https://chitchatsmd.000webhostapp.com/Images/" + user.getProfileUrl());

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(chatActivity.this,"JSON Error",Toast.LENGTH_LONG).show();
        }

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, "https://onesignal.com/api/v1/notifications", json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //now handle the response
                Toast.makeText(chatActivity.this,  "Notification Sent", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //handle the error
                Toast.makeText(chatActivity.this, "Notification Not Sent", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        })
        {    //adding header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic OGNlYzkyZmQtOWI4Ni00ZWM4LTk2MjMtZTY2MTAwMTQwYTg2");
                params.put("Content-type", "application/json");
                return params;
            }
        };
        RequestQueue queue= Volley.newRequestQueue(chatActivity.this);
        queue.add(jsonRequest);
    }

}

