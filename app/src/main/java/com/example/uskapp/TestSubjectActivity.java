package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TestSubjectActivity extends AppCompatActivity {
    TextView currentTopic;
    RecyclerView main_recycler_view;
    Toolbar top_toolbar;
    ArrayList<QuestionPost> posts_list= new ArrayList<QuestionPost>();
    ArrayList<Bitmap> profileBitmaps = new ArrayList<Bitmap>();
    Query query;
    MainRecyclerViewAdapter viewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_subject);

        top_toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        currentTopic = findViewById(R.id.current_topic_textview);
        currentTopic.setText(getIntent().getStringExtra("indsubject"));


        //subject activity this activity displays all the content inside the specific subject
        //query to display only Question posts with matching subject name
        query = FirebaseDatabase.getInstance().getReference("QuestionPost")
                .orderByChild("subject")
                .equalTo(getIntent().getStringExtra("indsubject"));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(posts_list!= null){
                    posts_list.clear();
                }
                if(snapshot.exists()){
                    for(DataSnapshot s : snapshot.getChildren()){
                        String name = s.child("name").getValue(String.class);
                        String userID = s.child("userID").getValue(String.class);
                        String postID =s.child("postID").getValue(String.class);
                        String text = s.child("text").getValue(String.class);
                        String timestamp = s.child("timestamp").getValue(String.class);
                        boolean toggle_anonymity = s.child("toggle_anonymity").getValue(Boolean.class);
                        String subject = s.child("subject").getValue(String.class);
                        QuestionPost qnPost = new QuestionPost(name,userID,postID,text,timestamp,subject,new ArrayList<String>(), toggle_anonymity);

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
        viewAdapter = new MainRecyclerViewAdapter(this, posts_list,profileBitmaps,null);
        main_recycler_view.setAdapter(viewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.search_posts){
            Toast.makeText(this, "workls", Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}
