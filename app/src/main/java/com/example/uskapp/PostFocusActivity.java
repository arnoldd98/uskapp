package com.example.uskapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnCompleteListener;
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

public class PostFocusActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_IMAGE = 2;
    RecyclerView answer_recyclerview;
    EditText user_answer_edit_text;
    ImageButton send_answer_button,get_image_button,back_to_main_button;
    ImageView upVoteIv,profilePicIv,replyIv;
    ToggleButton favourite_button;
    AnswerRecyclerViewAdapter answerAdapter;
    TextView nameTv,timeStampTv,postTextTv,upVoteTv,commentTv;
    Context context;

    String currentPostID,name,replyPostID;
    Uri imageUri;
    QuestionPost currentPost;
    ArrayList<AnswerPost> answerPostArrayList = new ArrayList<AnswerPost>();
    ArrayList<String> answerPostIDs = new ArrayList<String>();
    ArrayList<Bitmap> answerProfilePhotos = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_focus);
        context = this;
        View qnPostLayout = findViewById(R.id.post_card_view);
        currentPostID = getIntent().getStringExtra("postID");

        profilePicIv = (ImageView)qnPostLayout.findViewById(R.id.profile_imageview);
        favourite_button = (ToggleButton)qnPostLayout.findViewById(R.id.star_question_button);
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
                context.startActivity(new Intent(context, HomeActivity.class));
            }
        });

        answer_recyclerview = (RecyclerView) findViewById(R.id.answer_recycler_view);
        answer_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this);
        linear_layout_manager.setStackFromEnd(false);
        answer_recyclerview.setLayoutManager(linear_layout_manager);
        answerAdapter = new AnswerRecyclerViewAdapter(this,answerPostArrayList,answerProfilePhotos);
        answer_recyclerview.setAdapter(answerAdapter);

        //getting name of current user
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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
                            String postID =s.child("postID").getValue(String.class);
                            String text = s.child("text").getValue(String.class);
                            String timestamp = s.child("timestamp").getValue(String.class);
                            Integer upvotes = s.child("upvotes").getValue(Integer.class);
                            boolean toggle_anonymity = s.child("toggle_anonymity").getValue(Boolean.class);
                            String subject = s.child("subject").getValue(String.class);
                            DataSnapshot arraySnapAnsID = s.child("answerPostIDs");
                            DataSnapshot arraySnapVoteID = s.child("usersWhoUpVoted");

                            currentPost = new QuestionPost(name,userID,postID,text,timestamp,subject,toggle_anonymity,upvotes);
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
                            }
                            //currentPost.

                            StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                                    .child(userID);
                            imageRef.getBytes(2048*2048)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                              @Override
                                                              public void onSuccess(byte[] bytes) {
                                                                  Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                                  profilePicIv.setImageBitmap(bitmap);
                                                              }
                                                          }
                                    );


                            timeStampTv.setText(timestamp);
                            nameTv.setText(name);
                            postTextTv.setText(text);
                            upVoteTv.setText(String.valueOf(upvotes));
                            commentTv.setText(String.valueOf(currentPost.getAnswerPostIDs().size()));
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
                    Toast.makeText(context, "db failed", Toast.LENGTH_SHORT).show();
                }
            });

        }





        //UI FOR REPLYING
        user_answer_edit_text = (EditText) findViewById(R.id.user_answer_edittext);
        send_answer_button = (ImageButton) findViewById(R.id.send_answer_button);
        //SENDS A REPLY
        send_answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                AnswerPost ansPost = new AnswerPost(name,userID,postID,answer_text,dateStr,subject,false);
                //adds a new answer post
                FirebaseDatabase.getInstance().getReference("AnswerPost")
                        .child(postID).setValue(ansPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //adding the reply post to currentpost arraylist
                            //currentPost.addAnswerPostID(replyPostID);
                            //getRepliesFromFirebase();
                            //answerAdapter.notifyDataSetChanged();
                            currentPost.addAnswerPostID(replyPostID);
                            updateCurrentPost();
                            Toast.makeText(PostFocusActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PostFocusActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
        });
        replyIv = findViewById(R.id.replyImageView);
        get_image_button = (ImageButton) findViewById(R.id.get_image_button);
        get_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpImageOptions(context);
            }
        });

        // upvote click
        //checks if valid then updates the question post data
        upVoteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid=true;
                for(String upvoteIDs : currentPost.getUsersWhoUpVoted()){
                    if(upvoteIDs == FirebaseAuth.getInstance().getCurrentUser().getUid()){
                        valid=false;
                    }
                }

                if(valid){
                    currentPost.increaseUpVote();
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
                } else {
                    Toast.makeText(PostFocusActivity.this, "already voted", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // set animations for transition between HomeActivity and PostFocusActivity
    /*
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
*/
    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void popUpImageOptions(Context context) {
        final Dialog bottomDialogue = new Dialog(context, R.style.ImageDialogSheet);
        bottomDialogue.setContentView(R.layout.choose_image_options_view);
        bottomDialogue.setCancelable(true);
        bottomDialogue.show();

        TextView galleryBtn = (TextView)bottomDialogue.findViewById(R.id.choose_from_gallery_textview);
        TextView cameraBtn = (TextView)bottomDialogue.findViewById(R.id.open_camera_textview);

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    private void getRepliesFromFirebase(){
        answerProfilePhotos.clear();
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
                    boolean toggle_anonymity = snapshot.child("toggle_anonymity").getValue(Boolean.class);
                    String subject = snapshot.child("subject").getValue(String.class);
                    AnswerPost currentReply = new AnswerPost(name,userID,postID,text,timestamp,subject,toggle_anonymity);
                    answerPostArrayList.add(currentReply);

                    StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                            .child(userID);
                    imageRef.getBytes(2048*2048)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                      @Override
                                                      public void onSuccess(byte[] bytes) {
                                                          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                          answerProfilePhotos.add(bitmap);
                                                      }
                                                  }
                            );
                    answerAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "failed db", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void updateCurrentPost(){
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
            imageUri = data.getData();
            replyIv.setImageURI(imageUri);
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            replyIv.setImageBitmap(imageBitmap);
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            replyIv.setImageURI(imageUri);
        }
    }
}