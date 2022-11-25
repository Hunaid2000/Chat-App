package com.ass2.i192008_i192043;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class  chatsFragment extends Fragment {
    User user;
    RecyclerView contacts_rv;
    FloatingActionButton addContact;
    EditText searchContactText;
    ArrayList<User> contacts;
    ContactsAdapter adapter;
    Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);
        context= getContext();
        contacts_rv = view.findViewById(R.id.contacts_rv);
        user = User.getCurrentUser();
        addContact = view.findViewById(R.id.add_contact);
        searchContactText = view.findViewById(R.id.search_contacts_text);

        contacts = new ArrayList<>();
        adapter = new ContactsAdapter(contacts, context);
        contacts_rv.setAdapter(adapter);

        contacts_rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        contacts_rv.setLayoutManager(linearLayoutManager);

        addContact.setOnClickListener(v -> {
            addContactDailogbox();
        });
        getUserChat();

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
                    Toast.makeText(context, "No contacts found", Toast.LENGTH_SHORT).show();
                }
                adapter.setList(filteredContacts);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    private void getUserChat(){
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
                                Toast.makeText(context, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(context, obj.get("msg").toString(), Toast.LENGTH_LONG).show();
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





    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void addContactDailogbox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Contact");
        builder.setMessage("Enter the Phone Number of the user you want to add");
        EditText contact_no = new EditText(context);
        builder.setView(contact_no);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String phno = contact_no.getText().toString();
            if (phno.isEmpty()) {
                Toast.makeText(context, "Please enter Phone Number", Toast.LENGTH_SHORT).show();
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
                                                                Toast.makeText(context, obj.getString("msg"), Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Log.d("error1",obj.getString("e1"));
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
                                                params.put("userId", user.getUserId());
                                                params.put("connectionId", contact.getUserId());
                                                return params;
                                            }
                                        };
                                        RequestQueue queue = Volley.newRequestQueue(context);
                                        queue.add(request);

                                    }
                                    else{
                                        Toast.makeText(context, "Incorrect Phone Number", Toast.LENGTH_LONG).show();
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
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}