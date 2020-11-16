package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TestSubjectActivity extends AppCompatActivity {
    RecyclerView main_recycler_view;
    Toolbar top_toolbar;
    ArrayList<QuestionPost> posts_list;
    DatabaseReference dataRef;
    Query query;
    MainRecyclerViewAdapter viewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_subject);

        top_toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);

        //subject activity this activity displays all the content inside the specific subject
        query = FirebaseDatabase.getInstance().getReference("QuestionPost")
                .orderByChild("subject")
                .equalTo("50.001");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(posts_list!= null){
                    posts_list.clear();
                }
                if(snapshot.exists()){
                    for(DataSnapshot s : snapshot.getChildren()){
                        QuestionPost qnPost = s.getValue(QuestionPost.class);
                        posts_list.add(qnPost);
                    }
                }
                viewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TestSubjectActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        // get list of posts from Firebase
        /*
        posts_list = new ArrayList<QuestionPost>();
        QuestionPost customPost = new QuestionPost(new User("Gru", "Gru", null), false, "Hello World!", new ArrayList<byte[]>());
        customPost.addTag(new Tag("Dumb"));
        customPost.addTag(new Tag("Shhhhhhhh"));
        posts_list.add(customPost);

         */

        // set up main recycler view: linear layout manager to manage the order
        main_recycler_view = findViewById(R.id.main_menu_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        main_recycler_view.setLayoutManager(linearLayoutManager);

        // Add DividerItemDecoration to divide betewen cards in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(main_recycler_view.getContext(),
                linearLayoutManager.getOrientation());
        main_recycler_view.addItemDecoration(dividerItemDecoration);

        // Set custom adapter to inflate the recycler view
        viewAdapter = new MainRecyclerViewAdapter(this, posts_list);
        main_recycler_view.setAdapter(viewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }

}