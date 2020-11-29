package com.example.uskapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class HomeActivity extends BaseNavigationActivity {
    RecyclerView main_recycler_view;
    Toolbar top_toolbar;
    ImageButton choose_subject_button;
    TextView currentTopic;
    ArrayList<QuestionPost> posts_list = new ArrayList<QuestionPost>();
    ArrayList<Bitmap> profileBitmaps = new ArrayList<Bitmap>();
    MainRecyclerViewAdapter viewAdapter;
    Query query;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;

        // hides bottom navigation view when keyboard is called
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        top_toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        currentTopic = findViewById(R.id.current_topic_textview);
        currentTopic.setText("Feed");
        choose_subject_button = (ImageButton) top_toolbar.findViewById(R.id.choose_subject_button);
        choose_subject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent select_subject_intent = new Intent(activity, SubjectActivity.class);
                activity.startActivity(select_subject_intent);
            }
        });

        //subject activity this activity displays all the content inside the specific subject
        //query to display only Question posts with matching subject name

        mDatabase = FirebaseDatabase.getInstance().getReference("QuestionPost");
        mDatabase.addValueEventListener(new ValueEventListener() {
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

                        // TO BE UPDATED
                        ArrayList<Tag> tags = new ArrayList<Tag>();

                        int upvotes  = s.child("upvotes").getValue(Integer.class);
                        DataSnapshot arraySnapAnsID = s.child("answerPostIDs");
                        DataSnapshot arraySnapVoteID = s.child("usersWhoUpVoted");

                        QuestionPost qnPost = new QuestionPost(name,userID,postID,text,timestamp,subject,tags, toggle_anonymity,upvotes);
                        posts_list.add(qnPost);
                        for(DataSnapshot id : arraySnapAnsID.getChildren()){
                            String value = id.getValue(String.class);
                            qnPost.addAnswerPostID(value);
                        }
                        for(DataSnapshot id : arraySnapVoteID.getChildren()){
                            String value = id.getValue(String.class);
                            qnPost.addUserUpvote(value);
                        }
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


        // Set custom adapter to inflate the recycler view
        viewAdapter = new MainRecyclerViewAdapter(this, posts_list,profileBitmaps);
        main_recycler_view.setAdapter(viewAdapter);
    }

    @Override
    protected int getCurrentNavMenuId() {
        return R.id.nav_home_screen;
    }

    @Override
    protected int getCurrentContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu);
        final MenuItem search_item = menu.findItem(R.id.search_posts);
        SearchView search_view = (android.widget.SearchView) search_item.getActionView();
        search_view.setMaxWidth(android.R.attr.maxWidth);
        return true;
    }
}