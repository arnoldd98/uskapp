package com.example.uskapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PostFocusActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_IMAGE = 2;
    RecyclerView answer_recyclerview;
    EditText user_answer_edit_text;
    ImageButton send_answer_button,get_image_button,back_to_main_button;
    ImageView upVoteIv,profilePicIv,replyIv;
    Button favourite_button;
    AnswerRecyclerViewAdapter answerAdapter;
    TextView view_added_image_selector;
    TextView nameTv,timeStampTv,postTextTv,upVoteTv,commentTv;
    Activity activity;
    LinearLayout horizontalImageLayout;
    RecyclerView tag_recyclerview;

    String currentPostID,name,replyPostID;
    Uri imageUri;
    QuestionPost currentPost;
    ArrayList<AnswerPost> answerPostArrayList = new ArrayList<AnswerPost>();
    ArrayList<String> answerPostIDs = new ArrayList<String>();
    ArrayList<Bitmap> answerProfilePhotos = new ArrayList<Bitmap>();
    ArrayList<Tag> tagsList = new ArrayList<Tag>();
    ArrayList<Bitmap> answerPictures = new ArrayList<>();
    ArrayList<Bitmap> currentReplyPictures = new ArrayList();
    private LocalUser local_user = LocalUser.getCurrentUser();
    private boolean is_favourited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_focus);
        activity = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        final View qnPostLayout = findViewById(R.id.post_card_view);
        currentPostID = getIntent().getStringExtra("postID");

        horizontalImageLayout = (LinearLayout)qnPostLayout.findViewById(R.id.image_horizontal_linear_layout);
        tag_recyclerview = (RecyclerView) qnPostLayout.findViewById(R.id.question_tag_recyclerview);

        profilePicIv = (ImageView)qnPostLayout.findViewById(R.id.profile_imageview);
        favourite_button = (Button)qnPostLayout.findViewById(R.id.star_question_button);
        timeStampTv = (TextView)qnPostLayout.findViewById(R.id.post_timestamp);
        nameTv = (TextView)qnPostLayout.findViewById(R.id.question_author_name);
        postTextTv = (TextView)qnPostLayout.findViewById(R.id.question_textview);
        upVoteTv = (TextView)qnPostLayout.findViewById(R.id.ups_indicator_textview);
        upVoteIv = (ImageView)qnPostLayout.findViewById(R.id.ups_indicator_imageview);
        commentTv = (TextView)qnPostLayout.findViewById(R.id.comment_indicator__textview);

        // initialize back button, on click it returns back to the home activity
        back_to_main_button = (ImageButton) findViewById(R.id.back_to_main_button);
        back_to_main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(activity, HomeActivity.class));
            }
        });

        answer_recyclerview = (RecyclerView) findViewById(R.id.answer_recycler_view);
        answer_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this);
        linear_layout_manager.setStackFromEnd(false);
        answer_recyclerview.setLayoutManager(linear_layout_manager);
        answerAdapter = new AnswerRecyclerViewAdapter(this,answerPostArrayList,answerProfilePhotos,answerPictures);
        answer_recyclerview.setAdapter(answerAdapter);

        //getting name of current user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        //DATA FOR THE CURRENT QUESTION POST
        if(currentPostID != null){
            Query query = FirebaseDatabase.getInstance().getReference("QuestionPost")
                    .orderByChild("postID")
                    .equalTo(currentPostID);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot s : snapshot.getChildren()){
                            String name = s.child("name").getValue(String.class);
                            String userID = s.child("userID").getValue(String.class);
                            //String postImageID = s.child("postImageID").getValue(String.class);
                            String postID =s.child("postID").getValue(String.class);
                            final String text = s.child("text").getValue(String.class);
                            String timestamp = s.child("timestamp").getValue(String.class);
                            Integer upvotes = s.child("upvotes").getValue(Integer.class);
                            final boolean toggle_anonymity = s.child("toggle_anonymity").getValue(Boolean.class);
                            String subject = s.child("subject").getValue(String.class);

                            DataSnapshot arraySnapTagsID = s.child("tagsList");
                            for (DataSnapshot id : arraySnapTagsID.getChildren()) {
                                String value = id.child("tagName").getValue(String.class);
                                tagsList.add(new Tag(value));
                            }

                            DataSnapshot arraySnapAnsID = s.child("answerPostIDs");
                            DataSnapshot arraySnapVoteID = s.child("usersWhoUpVoted");
                            DataSnapshot arraySnapPicID = s.child("postImageIDs");

                            currentPost = new QuestionPost(name,userID,postID,text,timestamp,subject,Tag.getTagStringList(tagsList),toggle_anonymity,upvotes);
                           // currentPost.setPostImageID(postImageID);
                            answerPostIDs.clear();
                            for(DataSnapshot id : arraySnapAnsID.getChildren()){
                                String value = id.getValue(String.class);
                                answerPostIDs.add(value);
                            }
                            currentPost.setAnswerPostIDs(answerPostIDs);

                            ArrayList<String> upVoteIds = new ArrayList<String>();
                            for(DataSnapshot id : arraySnapVoteID.getChildren()){
                                String value = id.getValue(String.class);
                                currentPost.addUserUpvote(value);
                                if(value.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                                    upVoteIv.setImageResource(R.drawable.blue_triangle);
                                }
                            }

                            if (local_user.getFollowingPostIDs().contains(currentPost.getPostID())) {
                                favourite_button.setBackgroundResource(R.drawable.star_favourited);
                                is_favourited = true;
                            } else {
                                favourite_button.setBackgroundResource(R.drawable.star_unselected);
                                is_favourited = true;
                            }

                            StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                                    .child(userID);
                            imageRef.getBytes(2048*2048)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                              @Override
                                                              public void onSuccess(byte[] bytes) {
                                                                  Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                                  RoundedBitmapDrawable roundBitmap = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                                                  roundBitmap.setCircular(true);
                                                                  if(toggle_anonymity==false){
                                                                      profilePicIv.setImageDrawable(roundBitmap);
                                                                  }
                                                                  else {
                                                                      profilePicIv.setImageResource(R.drawable.anonymous_icon);
                                                                  }

                                                              }
                                                          }
                                    );

                            ArrayList<String> picIDArray = new ArrayList<String>();
                            for(DataSnapshot snap : arraySnapPicID.getChildren()){
                                picIDArray.add(snap.getValue(String.class));
                            }
                            currentPost.setPostImageIDs(picIDArray);
                            for(String picID : picIDArray){
                                StorageReference postImageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                                        .child(picID);
                                postImageRef.getBytes(2048*2048)
                                        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                ImageView qnImageView = new ImageView(PostFocusActivity.this);
                                                qnImageView.setScaleType(ImageView.ScaleType.CENTER);
                                                qnImageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

                                                qnImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,600,600,true));
                                                horizontalImageLayout.addView(qnImageView);

                                                qnPostLayout.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent image_intent = new Intent(PostFocusActivity.this, ViewImageActivity.class);
                                                        image_intent.putExtra("PostText", text);
                                                        image_intent.putExtra("ImageBitmap", bitmap);
                                                        startActivity(image_intent);
                                                    }
                                                });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(activity, "picture error", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


                            timeStampTv.setText(timestamp);
                            if(toggle_anonymity==false){
                                nameTv.setText(name);
                            } else {
                                nameTv.setText("Anonymous");
                            }

                            postTextTv.setText(text);
                            upVoteTv.setText(String.valueOf(upvotes));
                            commentTv.setText(String.valueOf(currentPost.getAnswerPostIDs().size()));

                            // set recyclerview showing tags of post under question text
                            if (tagsList != null) {
                                FlexboxLayoutManager layout_manager = new FlexboxLayoutManager(activity);
                                layout_manager.setJustifyContent(JustifyContent.FLEX_START);
                                tag_recyclerview.setLayoutManager(layout_manager);
                                TagAdapter tag_adapter = new TagAdapter(activity, tagsList, true);
                                tag_recyclerview.setAdapter(tag_adapter);
                            }
                        }
                    }
                    //GETTING DATA OF THE REPLIES WHICH WILL BE PASSED INTO THE ADAPTER
                    if(currentPost.getAnswerPostIDs()!= null ){
                        getRepliesFromFirebase();
                    }
                    answerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(activity, "db failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // UI FOR REPLYING
        user_answer_edit_text = (EditText) findViewById(R.id.user_answer_edittext);
        send_answer_button = (ImageButton) findViewById(R.id.send_answer_button);
        // SENDS A REPLY
        send_answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog anon_options = Utils.createBottomDialog(PostFocusActivity.this, getPackageManager(), R.layout.choose_anonymous_options_view);
                anon_options.show();
                LinearLayout not_anon_layout = anon_options.findViewById(R.id.choose_not_anonymous_option_layout);
                final LinearLayout anon_layout = anon_options.findViewById(R.id.choose_anonymous_option_layout);

                not_anon_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postAnswer(false);
                    }});

                anon_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postAnswer(true);
                    }});

            }
        });

        // click to check image added
        view_added_image_selector = (TextView) findViewById(R.id.view_added_image_indicator);
        view_added_image_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent view_pic_intent = new Intent(activity, ViewImageActivity.class);
                view_pic_intent.putExtra("ImageUri", imageUri);

                view_pic_intent.putExtra("PostText", user_answer_edit_text.getText().toString());
                activity.startActivity(view_pic_intent);

            }
        });

        get_image_button = (ImageButton) findViewById(R.id.get_image_button);
        get_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.popUpImageOptions(PostFocusActivity.this, getPackageManager());
            }
        });

        // upvote click
        // checks if valid then updates the question post data
        upVoteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid=true;
                for(String upvoteIDs : currentPost.getUsersWhoUpVoted()){
                    if(upvoteIDs.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                        valid=false;
                    }
                }

                if(valid){
                    currentPost.increaseUpVote();
                    givePosterKarma(currentPost.getUserID());
                    int i = currentPost.getUpvotes();
                    String id = currentPost.getPostID();
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("upvotes");
                    postRef.setValue(i);
                    DatabaseReference postRef2 = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("usersWhoUpVoted");
                    ArrayList<String> newUsersID = currentPost.getUsersWhoUpVoted();
                    newUsersID.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    postRef2.setValue(newUsersID);
                    upVoteIv.setImageResource(R.drawable.blue_triangle);
                } else {
                    Toast.makeText(PostFocusActivity.this, "already voted", Toast.LENGTH_SHORT).show();
                }
            }

            private void givePosterKarma(String posterID) {
                final DatabaseReference posterRef = FirebaseDatabase.getInstance().getReference("Users")
                        .child(posterID);
                posterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int karma = snapshot.child("karma").getValue(Integer.class);
                        karma +=1;
                        posterRef.child("karma").setValue(karma);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        favourite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_favourited) {
                    favourite_button.setBackgroundResource(R.drawable.star_unselected);
                    local_user.unfavouritePost(currentPost.getPostID());
                    is_favourited = false;
                } else {
                    favourite_button.setBackgroundResource(R.drawable.star_favourited);
                    local_user.favouritePost(currentPost.getPostID());
                    is_favourited = true;
                }
            }
        });

    }

    // set animations for transition between HomeActivity and PostFocusActivity
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private void postAnswer(boolean is_anon) {
        Date now = new Date();
        long timestamp = now.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String dateStr = sdf.format(timestamp);
        String subject = currentPost.getSubject();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String postID = userID+dateStr;
        // in the case where duplicate id sent
        if(replyPostID!= postID){
            replyPostID = postID;
        }
        String picID = postID + "pic";
        String answer_text = user_answer_edit_text.getText().toString();

        AnswerPost ansPost = new AnswerPost(name,userID,postID,answer_text,dateStr,subject,is_anon);

        ansPost.setPicId(picID);
        //adds a new answer post
        FirebaseDatabase.getInstance().getReference("AnswerPost")
                .child(postID).setValue(ansPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    currentPost.addAnswerPostID(replyPostID);
                    updateCurrentPost();
                    Toast.makeText(PostFocusActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PostFocusActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //adding answer picture to firebase
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("AnswerPictures")
                .child(picID);
        if(imageUri != null ){
            imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PostFocusActivity.this, "success upload image", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostFocusActivity.this, "failed upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    //getting reply data and storing it in an array
    private void getRepliesFromFirebase(){
        //answerProfilePhotos.clear();
        for (String id : currentPost.getAnswerPostIDs() ){
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("AnswerPost")
                    .child(id);
            postRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    String userID = snapshot.child("userID").getValue(String.class);
                    String postID =snapshot.child("postID").getValue(String.class);
                    String text = snapshot.child("text").getValue(String.class);
                    String timestamp = snapshot.child("timestamp").getValue(String.class);
                    int upvotes = snapshot.child("upvotes").getValue(Integer.class);
                    boolean toggle_anonymity = snapshot.child("toggle_anonymity").getValue(Boolean.class);
                    String subject = snapshot.child("subject").getValue(String.class);
                    String picId = snapshot.child("picId").getValue(String.class);
                    DataSnapshot arrayOfUsersUpvote = snapshot.child("usersWhoUpVoted");



                    AnswerPost currentReply = new AnswerPost(name,userID,postID,text,timestamp,subject,toggle_anonymity,upvotes);
                    currentReply.setPicId(picId);
                    for(DataSnapshot s : arrayOfUsersUpvote.getChildren()){
                        String id = s.getValue(String.class);
                        currentReply.addUserUpvote(id);
                    }

                    boolean valid=true;
                    for(AnswerPost a : answerPostArrayList){
                        if(a.getPostID().equals(postID)){
                            valid=false;
                        }
                    }
                    if(valid){
                      answerPostArrayList.add(currentReply);
                    }

                    //getting profile pictures
                    StorageReference profileRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                            .child(userID);
                    profileRef.getBytes(2048*2048)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                      @Override
                                                      public void onSuccess(byte[] bytes) {
                                                          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                          answerProfilePhotos.add(bitmap);
                                                          answerAdapter.notifyDataSetChanged();
                                                      }
                                                  }
                            );

                    //getting reply pictures
                    StorageReference imageRef = FirebaseStorage.getInstance().getReference("AnswerPictures")
                            .child(picId);
                    imageRef.getBytes(2048*2048)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                      @Override
                                                      public void onSuccess(byte[] bytes) {
                                                          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                          answerPictures.add(bitmap);
                                                          answerAdapter.notifyDataSetChanged();
                                                      }
                                                  }
                            );

                    answerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(activity, "failed db", Toast.LENGTH_SHORT).show();
                }
            });
        }
        answerAdapter.notifyDataSetChanged();
    }

    public void updateCurrentPost(){
        //currentPost.setPostImageIDs();
        currentPost.setAnswerPostIDs(answerPostIDs); //maybe need comment out
        FirebaseDatabase.getInstance().getReference("QuestionPost")
                .child(currentPostID).setValue(currentPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PostFocusActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(PostFocusActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            imageUri = Utils.getImageUri();
            view_added_image_selector.setVisibility(View.VISIBLE);
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            view_added_image_selector.setVisibility(View.VISIBLE);
            imageUri = data.getData();
            System.out.println("Gallery image uri: " + imageUri);
        }
    }

}