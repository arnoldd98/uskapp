package com.example.uskapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    RecyclerView main_recycler_view;
    Toolbar top_toolbar;
    ArrayList<QuestionPost> posts_list = new ArrayList<QuestionPost>();
    ArrayList<Bitmap> profileBitmaps = new ArrayList<Bitmap>();
    MainRecyclerViewAdapter viewAdapter;
    Query query;
    BottomNavigationView bottom_navigation_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Activity activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        top_toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);

        //subject activity this activity displays all the content inside the specific subject
        //query to display only Question posts with matching subject name
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

                        String userID = s.child("userID").getValue(String.class);
                        String postID =s.child("postID").getValue(String.class);
                        String text = s.child("text").getValue(String.class);
                        String timestamp = s.child("timestamp").getValue(String.class);
                        boolean toggle_anonymity = s.child("toggle_anonymity").getValue(Boolean.class);
                        String subject = s.child("subject").getValue(String.class);
                        QuestionPost qnPost = new QuestionPost(userID,postID,text,timestamp,subject,toggle_anonymity);
                        posts_list.add(qnPost);

                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                                .child(userID);
                        imageRef.getBytes(2048*2048)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                          @Override
                                                          public void onSuccess(byte[] bytes) {
                                                              Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                              profileBitmaps.add(bitmap);
                                                              viewAdapter.notifyDataSetChanged();
                                                          }
                                                      }
                                );
                    }
                }
                viewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // set up main recycler view: linear layout manager to manage the order
        main_recycler_view = findViewById(R.id.main_menu_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        main_recycler_view.setLayoutManager(linearLayoutManager);

        // Add DividerItemDecoration to divide between cards in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(main_recycler_view.getContext(),
                linearLayoutManager.getOrientation());
        main_recycler_view.addItemDecoration(dividerItemDecoration);

        // Set custom adapter to inflate the recycler view
        viewAdapter = new MainRecyclerViewAdapter(this, posts_list,profileBitmaps);
        main_recycler_view.setAdapter(viewAdapter);

        bottom_navigation_view = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottom_navigation_view.setSelectedItemId(R.id.homeScreen);
        bottom_navigation_view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.subject:
                        break;
                    case R.id.homeScreen:
                        break;
                    case R.id.addPost:
                        Intent add_post_intent = new Intent(activity, NewPostActivity.class);
                        Utils.updateNavigationBarState(R.id.homeScreen, bottom_navigation_view);
                        activity.startActivity(add_post_intent);
                        break;
                    case R.id.profile:
                        Intent profile_intent = new Intent(activity, ProfileActivity.class);
                        Utils.updateNavigationBarState(R.id.homeScreen, bottom_navigation_view);
                        activity.startActivity(profile_intent);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }


}