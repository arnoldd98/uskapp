package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FakeAddPostActivity extends AppCompatActivity {
    Button submit,nextActivity;
    EditText text,subjectTextView;
    ImageView image;
    ImageButton chooseFromGallery;
    static final int PICK_IMAGE=1;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_add_post);
        submit = findViewById(R.id.submitPostBtn);
        text = findViewById(R.id.textTv);
        subjectTextView = findViewById(R.id.subjectTV);
        image = findViewById(R.id.addedPicture);
        chooseFromGallery = findViewById(R.id.galleryButton);
        nextActivity = findViewById(R.id.nextActBtn);

        nextActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FakeAddPostActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now = new Date();
                long timestamp = now.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                String dateStr = sdf.format(timestamp);
                String name = "no reply tester";
                String subject = subjectTextView.getText().toString();
                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                String postID = userID+dateStr;
                String picID = postID + "pic";
                QuestionPost newPost = new QuestionPost(name,userID,postID, text.getText().toString(),
                        dateStr,subject, new ArrayList<String>(), false);
                //newPost.addAnswerPostID("2qxNidevHRMeUklVFU4nemLqSV7218 Nov 2020 15:41:58");
                //newPost.addAnswerPostID("2qxNidevHRMeUklVFU4nemLqSV7218 Nov 2020 15:42:03");
                //newPost.addAnswerPostID("2qxNidevHRMeUklVFU4nemLqSV7218 Nov 2020 15:42:06");

                FirebaseDatabase.getInstance().getReference("QuestionPost")
                        .child(postID).setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(FakeAddPostActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FakeAddPostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //Toast.makeText(FakeAddPostActivity.this, dateStr, Toast.LENGTH_SHORT).show();
                StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                        .child(picID);
                if(imageUri != null ){
                    imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                Toast.makeText(FakeAddPostActivity.this, "success upload image", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FakeAddPostActivity.this, "failed upload image", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }

        });

        chooseFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent, PICK_IMAGE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            imageUri = data.getData();
            image.setImageURI(imageUri);

        }
    }
}