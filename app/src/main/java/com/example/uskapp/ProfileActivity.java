package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

public class ProfileActivity extends BaseNavigationActivity {
    private String TAG = "PROFILE";
    ImageView profilePicIV;
    TextView nameView, rankView, karmaView;
    RecyclerView favorited_post_recyclerview;
    ProgressBar expBar;
    Button signOutBtn;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private String name;
    private int rank, exp, karma;
    private static final int PICK_IMAGE = 1;
    Uri imageUri;

    private  ArrayList<String> favourited_post_ids = new ArrayList<>();
    private ArrayList<QuestionPost> favourited_posts = new ArrayList<>();
    private ArrayList<Bitmap> profileBitmaps = new ArrayList<>();
    MainRecyclerViewAdapter favoritedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //gets information from firebase and displays it
        mDatabase = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("email").getValue(String.class);
                karma = snapshot.child("karma").getValue(Integer.class);

                // get list of IDs of posts followed
                DataSnapshot postFollowingID = snapshot.child("postFollowing");
                for (DataSnapshot id: postFollowingID.getChildren()) {
                    String post_id = id.getValue(String.class);
                    favourited_post_ids.add(post_id);
                }

                nameView.setText(name);
                karmaView.setText(String.valueOf(karma));
                String rank = convertKaramaToRank(karma);
                int exp = convertKaramaToExp(karma);
                rankView.setText(String.valueOf(rank));
                expBar.setProgress(exp);
                expBar.setMax(100);
                expBar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffeb3b")));

                getFavoritedPostsFromFirebase();
                favoritedAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String failed = "ERROR";
                nameView.setText(failed);
                rankView.setText(failed);
                karmaView.setText(failed);
                expBar.setProgress(0);
                Log.w(TAG, "onCancelled: ", error.toException());
            }
        });

        //for the profile picture
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        imageRef.getBytes(2048*2048)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        profilePicIV.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePicIV.setImageResource(R.drawable.bunny2);
                e.printStackTrace();
            }
        });
        //setting data to UI
        profilePicIV = findViewById(R.id.userProfile);
        nameView = findViewById(R.id.nameTv);
        rankView = findViewById(R.id.rankTv);
        karmaView = findViewById(R.id.karmaTv);
        expBar = findViewById(R.id.expProgressBar);
        signOutBtn = findViewById(R.id.signOutBtn);
        //expBar.setMax(100);
        //expBar.setProgress(50);

        favorited_post_recyclerview = findViewById(R.id.favorited_post_recyclerview);
        LinearLayoutManager layout_manager = new LinearLayoutManager(this);
        layout_manager.setStackFromEnd(false);
        layout_manager.setOrientation(RecyclerView.VERTICAL);
        favorited_post_recyclerview.setLayoutManager(layout_manager);
        favoritedAdapter = new MainRecyclerViewAdapter(this, favourited_posts, profileBitmaps);
        favorited_post_recyclerview.setAdapter(favoritedAdapter);


        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                if(FirebaseAuth.getInstance().getCurrentUser()==null){
                    Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        profilePicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, PICK_IMAGE);
            }
        });


    }

    private String convertKaramaToRank(int karma) {
        if(karma <=10){
            return "noob";
        } else if (karma<=20){
            return "average joe";
        } else if (karma <= 50){
            return "boss";
        } else if (karma <=100){
            return  "Big Boss";
        } else if (karma <=200){
            return "god level";
        } else {
            return "arnold level";
        }
    }

    private int convertKaramaToExp(int karma) {
        if(karma <=10){
            return (int)(100*karma/10);
        } else if (karma<=20){
            return (int)(100*(karma-10)/(20-10));
        } else if (karma <= 50){
            return (int)(100*(karma-20)/(50-20));
        } else if (karma <=100){
            return  (int)(100*(karma-50)/(100-50));
        } else if (karma <=200){
            return (int)(100*(karma-100)/(200-100));
        } else {
            return 100;
        }
    }

    @Override
    protected int getCurrentNavMenuId() {
        return R.id.nav_profile;
    }

    @Override
    protected int getCurrentContentViewId() {
        return R.layout.activity_profile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();

            try {
                mStorageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                mStorageRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                        Toast.makeText(ProfileActivity.this, "Profile Picture Changed", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = null;
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        profilePicIV.setImageBitmap(bitmap);
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Toast.makeText(ProfileActivity.this, "Failed tt upload", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void getFavoritedPostsFromFirebase() {
        System.out.println("Favorited" + favourited_post_ids);
        for (String id : favourited_post_ids) {
            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("QuestionPost").child(id);
            postRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    String userID = snapshot.child("userID").getValue(String.class);
                    String postID = snapshot.child("postID").getValue(String.class);
                    String text = snapshot.child("text").getValue(String.class);
                    String timestamp = snapshot.child("timestamp").getValue(String.class);
                    boolean toggle_anonymity = snapshot.child("toggle_anonymity").getValue(Boolean.class);
                    String subject = snapshot.child("subject").getValue(String.class);
                    DataSnapshot arraySnapTagsID = snapshot.child("tagsList");
                    ArrayList<Tag> tags = new ArrayList<Tag>();
                    for (DataSnapshot id : arraySnapTagsID.getChildren()) {
                        String value = id.child("tagName").getValue(String.class);
                        tags.add(new Tag(value));
                    }

                    int upvotes  = snapshot.child("upvotes").getValue(Integer.class);
                    DataSnapshot arraySnapAnsID = snapshot.child("answerPostIDs");
                    DataSnapshot arraySnapVoteID = snapshot.child("usersWhoUpVoted");

                    QuestionPost qnPost = new QuestionPost(name,userID,postID,text,timestamp,subject,tags, toggle_anonymity,upvotes);
                    favourited_posts.add(qnPost);
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
                                                      }
                                                  }
                            );
                    favoritedAdapter.notifyDataSetChanged();
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "failed db", Toast.LENGTH_SHORT).show();
                }
            });
        }
        favoritedAdapter.notifyDataSetChanged();
    }
}