package com.example.uskapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import java.util.Collections;

public class HomeActivity extends BaseNavigationActivity {
    private ConstraintLayout home_container;
    private RecyclerView main_recycler_view;
    private Toolbar top_toolbar;
    private ImageButton choose_subject_button;
    private TextView current_topic_textview;
    private RelativeLayout indicate_search_term_layout;
    private Button close_search_result_button;

    private MainRecyclerViewAdapter viewAdapter;
    private DatabaseReference mDatabase;

    // set arraylists to hold posts and profile images
    protected static ArrayList<QuestionPost> posts_list = new ArrayList<QuestionPost>();

    // define which subjects posts are shown from. If "home", show the home feed
    private static String current_subject;

    // variables to check if search is on
    // helper variables for indicateCurrentSearchTermFunction
    boolean is_search;

    public LocalUser local_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        local_user = LocalUser.getCurrentUser();
        final Activity activity = this;

        home_container = (ConstraintLayout) findViewById(R.id.home_container);
        is_search = false;

        // hides bottom navigation view when keyboard is called
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        top_toolbar = findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);

        choose_subject_button = (ImageButton) top_toolbar.findViewById(R.id.choose_subject_button);
        choose_subject_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent select_subject_intent = new Intent(activity, SubjectActivity.class);
                activity.startActivity(select_subject_intent);
            }
        });

        indicate_search_term_layout = (RelativeLayout) findViewById(R.id.indicate_search_term_layout);
        close_search_result_button = (Button) findViewById(R.id.close_search_result_button);

        // check for current subject to set topics for
        current_subject = "Home";
        if (getIntent().getStringExtra("indsubject") != null) {
            current_subject = getIntent().getStringExtra("indsubject");
        }

        current_topic_textview = findViewById(R.id.current_topic_textview);
        // set query according to current topic
        Query query = getCurrentQuery();
        setListenerForPostsList(query);


        // set up main recycler view: linear layout manager to manage the order
        main_recycler_view = findViewById(R.id.main_menu_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        main_recycler_view.setLayoutManager(linearLayoutManager);

        // close search indicator and revert back to main
        close_search_result_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset back to main feed (if search is called)
                viewAdapter = new MainRecyclerViewAdapter(HomeActivity.this, posts_list);
                main_recycler_view.setAdapter(viewAdapter);

                // shift recyclerview down to accomodate for search indicator layout
                ConstraintSet cs = new ConstraintSet();
                cs.clone(home_container);
                cs.connect(R.id.main_menu_recycler_view, ConstraintSet.TOP, R.id.top_toolbar, ConstraintSet.BOTTOM, 12);
                cs.applyTo(home_container);
                if (is_search) {
                    Animation fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                    fade_out.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            indicate_search_term_layout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });

                    indicate_search_term_layout.startAnimation(fade_out);

                    is_search = false;
                }
            }
        });

        // if tag is searched (clicked from elsewhere other than HomeActivity)
        if (getIntent().getStringExtra("searchtag") != null) {
            indicateCurrentSearchTerm(getIntent().getStringExtra("searchtag"), true);
        }


        // Set custom adapter to inflate the recycler view
        if (!is_search) {
            viewAdapter = new MainRecyclerViewAdapter(this, posts_list);
            main_recycler_view.setAdapter(viewAdapter);
        }
    }

    // Set current topic
    public Query getCurrentQuery() {
        mDatabase = FirebaseDatabase.getInstance().getReference("QuestionPost");
        Query query;
        if (current_subject == "Home") {
            current_topic_textview.setText("Usk");
            query = mDatabase;
        } else {
            current_topic_textview.setText(current_subject);
            query = mDatabase.orderByChild("subject").equalTo(current_subject);
        }

        return query;
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
        getMenuInflater().inflate(R.menu.top_toolbar_menu, menu); //inflates the top toolbar menu which holds the search bar function
        final MenuItem search_item = menu.findItem(R.id.search_posts);
        SearchView search_view = (android.widget.SearchView) search_item.getActionView();
        search_view.setMaxWidth(android.R.attr.maxWidth);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { //This method is invoked when the user submits the query text on the search bar
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) { //This method is invoked when there is a change detected in the query text within the search bar
                viewAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }


    // create a layout which indicates the current search term when a tag button is pressed
    @SuppressLint("ResourceAsColor")
    public void indicateCurrentSearchTerm(String term, boolean is_tag) {
        current_subject = "Home";
        Query query = getCurrentQuery();
        setListenerForPostsList(query);

        // toasts error message and returns if indicate_search_term_layout does not exist
        if (indicate_search_term_layout == null) {
            Toast.makeText(this, "Indicate search term layout has not been intialized!", Toast.LENGTH_SHORT).show();
            return;
        }
        View search_term_view;
        if (is_tag) {
            search_term_view = LayoutInflater.from(this).inflate(R.layout.tag_selector_view, null);
            TextView text = search_term_view.findViewById(R.id.tag_name_textview);
            text.setText(term);
        } else {
            // create text if search term is entered in search bar
            search_term_view = new TextView(this);
            ((TextView) search_term_view).setText(term);
            ((TextView) search_term_view).setTextColor(R.color.colorPrimary);
            ((TextView) search_term_view).setTextSize(18);
            search_term_view.setPadding(8, 0, 0, 0);
        }

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.RIGHT_OF, R.id.hc_search_for_textview);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);

        if (is_search) {
            // remove current search view if tag was clicked again
            indicate_search_term_layout.removeViewAt(2);
        }

        if (!is_search) is_search = true;

        indicate_search_term_layout.addView(search_term_view, lp);
        indicate_search_term_layout.setVisibility(View.VISIBLE);

        ArrayList<QuestionPost> searched_posts = new ArrayList<QuestionPost>();
        ArrayList<Bitmap> search_profile_bitmaps = new ArrayList<Bitmap>();
        for (QuestionPost post : posts_list) {
            if (post.getTagsStringList() == null) continue;
            for (String tag_string : post.getTagsStringList()) {
                if (tag_string.equals(term)) {
                    searched_posts.add(post);
                    int position = posts_list.indexOf(post);
                }
            }
        }

        // shift recyclerview down to accomodate for search indicator layout
        ConstraintSet cs = new ConstraintSet();
        cs.clone(home_container);
        cs.connect(R.id.main_menu_recycler_view, ConstraintSet.TOP, R.id.indicate_search_term_layout, ConstraintSet.BOTTOM, 5);
        cs.applyTo(home_container);

        viewAdapter = new MainRecyclerViewAdapter(this, searched_posts);
        main_recycler_view.setAdapter(viewAdapter);

    }

    public void setListenerForPostsList(Query query) {
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
                        String postID = s.child("postID").getValue(String.class);
                        String text = s.child("text").getValue(String.class);
                        String timestamp = s.child("timestamp").getValue(String.class);
                        boolean toggle_anonymity = s.child("toggle_anonymity").getValue(Boolean.class);
                        String subject = s.child("subject").getValue(String.class);
                        int upvotes  = s.child("upvotes").getValue(Integer.class);
                        DataSnapshot arraySnapTagsID = s.child("tagsList");
                        DataSnapshot arraySnapAnsID = s.child("answerPostIDs");
                        DataSnapshot arraySnapVoteID = s.child("usersWhoUpVoted");
                        DataSnapshot arraySnapPicID = s.child("postImageIDs");

                        ArrayList<Tag> tags = new ArrayList<Tag>();
                        for (DataSnapshot id : arraySnapTagsID.getChildren()) {
                            String value = id.child("tagName").getValue(String.class);
                            tags.add(new Tag(value));
                        }
                        ArrayList<String> tag_strings = Tag.getTagStringList(tags);

                        final ArrayList<Bitmap> qnPics = new ArrayList<Bitmap>();
                        for(DataSnapshot id : arraySnapPicID.getChildren()){
                            String value = id.getValue(String.class);
                            StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                                    .child(value);
                            imageRef.getBytes(2048 * 2048)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                              @Override
                                                              public void onSuccess(byte[] bytes) {
                                                                  Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                                  qnPics.add(bitmap);
                                                                  viewAdapter.notifyDataSetChanged();
                                                              }
                                                          }
                                    );
                        }

                        QuestionPost qnPost = new QuestionPost(name,userID,postID,text,timestamp,subject, tag_strings, toggle_anonymity,upvotes);
                        qnPost.setPictures(qnPics);
                        posts_list.add(qnPost);
                        for(DataSnapshot id : arraySnapAnsID.getChildren()){
                            String value = id.getValue(String.class);
                            qnPost.addAnswerPostID(value);
                        }
                        for(DataSnapshot id : arraySnapVoteID.getChildren()){
                            String value = id.getValue(String.class);
                            qnPost.addUserUpvote(value);
                        }
                        for(DataSnapshot id : arraySnapPicID.getChildren()){
                            String value = id.getValue(String.class);
                            qnPost.addPostImageIDs(value);
                        }
                        viewAdapter.notifyDataSetChanged();
                    }

                }
                Collections.sort(posts_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, SubjectActivity.class);
        this.startActivity(intent);
    }
}