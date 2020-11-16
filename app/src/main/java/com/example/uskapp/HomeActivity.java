package com.example.uskapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    RecyclerView main_recycler_view;
    Toolbar top_toolbar;
    ArrayList<QuestionPost> posts_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        top_toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);

        // get list of posts from Firebase
        posts_list = new ArrayList<QuestionPost>();
        QuestionPost customPost = new QuestionPost(new User("Gru", "Gru", null), false, "Hello World!", new ArrayList<byte[]>());
        customPost.addTag(new Tag("Dumb"));
        customPost.addTag(new Tag("Shhhhhhhh"));
        posts_list.add(customPost);

        // set up main recycler view: linear layout manager to manage the order
        main_recycler_view = findViewById(R.id.main_menu_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        main_recycler_view.setLayoutManager(linearLayoutManager);

        // Add DividerItemDecoration to divide between cards in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(main_recycler_view.getContext(),
                linearLayoutManager.getOrientation());
        main_recycler_view.addItemDecoration(dividerItemDecoration);

        // Set custom adapter to inflate the recycler view
        MainRecyclerViewAdapter viewAdapter = new MainRecyclerViewAdapter(this, posts_list);
        main_recycler_view.setAdapter(viewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }

}