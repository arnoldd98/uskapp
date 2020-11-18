package com.example.uskapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener{
    static final int CAMERA_REQUEST = 1;
    Button buttonTags;
    Button buttonPostAs;
    ImageButton backToHome;
    ImageButton cameraButton;
    ImageButton galleryButton;
    ConstraintLayout.LayoutParams layoutParams;
    ConstraintLayout anonymousOrNot;
    ConstraintLayout mainLayout;
    ImageView profilePic,postPicture;
    private static final int PICK_IMAGE = 2;
    TextView postText;
    Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //anonymousOrNot = findViewById(AnonymousOrNormal);
        mainLayout = findViewById(R.id.MainLayout);
        profilePic = findViewById(R.id.userProfileNewPost);
        postPicture = findViewById(R.id.postPhotoContent);
        buttonTags = findViewById(R.id.buttonTags);
        buttonTags.setOnClickListener(this);
        buttonPostAs = findViewById(R.id.buttonPostAs);
        buttonPostAs.setOnClickListener(this);
        cameraButton = findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        galleryButton = findViewById(R.id.picturesButton);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        backToHome = findViewById(R.id.backToHome);
        backToHome.setOnClickListener(this);

        postText = findViewById(R.id.post);
        postText.setOnClickListener(this);



        // setting users profile photo
        StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        imageRef.getBytes(2048*2048)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        profilePic.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePic.setImageResource(R.drawable.ic_launcher_foreground);
                e.printStackTrace();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.post:
                post();
                break;

            case R.id.backToHome:
                startActivity(new Intent(this,HomeActivity.class));
                break;

            case R.id.buttonTags:
                break;

            case R.id.buttonPostAs:
                break;
        }

    }

    private void post(){
        Date now = new Date();
        long timestamp = now.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.US);
        String dateStr = sdf.format(timestamp);
        // need get from the tag but need implement tag system first
        //String subject = subjectTextView.getText().toString();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String postID = userID+dateStr;
        String picID = postID + "pic";
        QuestionPost newPost = new QuestionPost(userID,postID, postText.getText().toString(),
                dateStr,"subject",false);


        FirebaseDatabase.getInstance().getReference("QuestionPost")
                .child(postID).setValue(newPost).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(NewPostActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewPostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                .child(picID);
        if(imageUri != null ){
            imageRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(NewPostActivity.this, "success upload image", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewPostActivity.this, "failed upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            postPicture.setImageURI(imageUri);
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //postPicture.setImageBitmap(imageBitmap);
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            postPicture.setImageURI(imageUri);
        }
    }

}


