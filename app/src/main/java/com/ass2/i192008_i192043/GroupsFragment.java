package com.ass2.i192008_i192043;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GroupsFragment extends Fragment {

    RecyclerView groups_recyclerView;
    FloatingActionButton create_group_actionBar;
    EditText editTextSearch;
    String currentUserId;
    TextView textViewNothing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_groups, container, false);

        setHasOptionsMenu(true);

        groups_recyclerView=view.findViewById(R.id.group_list);
        create_group_actionBar=view.findViewById(R.id.create_group);
        editTextSearch=view.findViewById(R.id.search_groups);
        textViewNothing=view.findViewById(R.id.txt_nothing);

        textViewNothing.setVisibility(View.VISIBLE);

        groups_recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        groups_recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
