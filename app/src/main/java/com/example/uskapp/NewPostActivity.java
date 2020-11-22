package com.example.uskapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_IMAGE = 2;
    boolean isAnonymous=false;
    String name;
    Button buttonTags;
    Button buttonPostAs;
    ImageButton backToHome;
    ImageButton cameraButton;
    ImageButton galleryButton;
    ConstraintLayout.LayoutParams layoutParams;
    ConstraintLayout anonymousOrNot;
    ConstraintLayout mainLayout;
    ImageView profilePic,postPicture;
    TextView postText;
    EditText textOfPost;
    
    Uri imageUri;
    Context new_post_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        new_post_context = this;
        //anonymousOrNot = findViewById(AnonymousOrNormal);
        mainLayout = findViewById(R.id.MainLayout);
        profilePic = findViewById(R.id.userProfileNewPost);
        postPicture = findViewById(R.id.postPhotoContent);
        textOfPost = findViewById(R.id.textPostTv);
        buttonTags = findViewById(R.id.select_tag_button);
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

        //is the text view for posting
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
        //getting name
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.post:
                post();
                startActivity(new Intent(this, HomeActivity.class));
                break;

            case R.id.backToHome:
                startActivity(new Intent(this,HomeActivity.class));
                break;

            case R.id.select_tag_button:
                Dialog tag_options_dialog = Utils.createBottomDialog(this, getPackageManager(), R.layout.tagmenu);

                final ArrayList<String> test = new ArrayList<>();
                test.add("Yes");
                test.add("No");
                test.add("Maybe");
                RecyclerView tag_recycler_view = tag_options_dialog.findViewById(R.id.select_tag_recyclerview);

                // Use Flexbox Layout Manager (https://github.com/google/flexbox-layout)
                FlexboxLayoutManager layout_manager = new FlexboxLayoutManager(new_post_context);
                layout_manager.setJustifyContent(JustifyContent.FLEX_END);

                tag_recycler_view.setLayoutManager(
                        new FlexboxLayoutManager(new_post_context));
                tag_recycler_view.addItemDecoration(new DividerItemDecoration(new_post_context,
                        DividerItemDecoration.HORIZONTAL));
                tag_recycler_view.setAdapter(new SubjectAdapter(new_post_context, test, true));
                tag_options_dialog.show();
                break;

            case R.id.buttonPostAs:

                Dialog u = Utils.createBottomDialog(this, getPackageManager(), R.layout.choose_anonymous_options_view);
                LinearLayout ll = u.findViewById(R.id.choose_not_anonymous_option_layout);
                LinearLayout ll2 = u.findViewById(R.id.choose_anonymous_option_layout);
                u.show();
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAnonymous=false;
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
                });
                ll2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isAnonymous=true;
                        int id = getResources().getIdentifier("com.example.uskapp:mipmap/"+"anonymous_icon",null,null);
                        profilePic.setImageResource(R.drawable.anonymous_icon);
                    }
                });
                ;
                break;
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void post(){



        Date now = new Date();
        long timestamp = now.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss",Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        String dateStr = sdf.format(timestamp);
        // need get from the tag but need implement tag system first
        //String subject = subjectTextView.getText().toString();
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String postID = userID+dateStr;
        String picID = postID + "pic";
        QuestionPost newPost;
        if(!isAnonymous){
            newPost = new QuestionPost(name,userID,postID, textOfPost.getText().toString(),
                    dateStr,"50.001",false);
        } else {
            newPost = new QuestionPost(name,userID,postID, textOfPost.getText().toString(),
                    dateStr,"50.001",true);
        }

        //if there is a picture
        if(imageUri!= null){
            newPost.setPostImageID(picID);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                    .child(picID);
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

    }

    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras  = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap,400,400,true);
            postPicture.setImageBitmap(imageBitmap);
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //postPicture.setImageBitmap(imageBitmap);
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            postPicture.setImageURI(imageUri);
            postPicture.getLayoutParams().width = 400;
            postPicture.getLayoutParams().height = 400;
        }
    }
    public void popUpImageOptions() {
        final Dialog bottomDialogue = new Dialog(this, R.style.ImageDialogSheet);
        bottomDialogue.setContentView(R.layout.choose_image_options_view);
        bottomDialogue.setCancelable(true);
        bottomDialogue.show();
    }
}


