package com.example.uskapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAMERA_REQUEST = 1;
    private static final int PICK_IMAGE = 2;

    boolean isAnonymous=false;
    String current_subject;
    String name;

    Spinner select_subject_spinner;
    Button buttonPostAs;
    ImageButton backToHome;
    ImageButton cameraButton;
    ImageButton galleryButton;
    LinearLayout pictureLayout;
    ConstraintLayout mainLayout;
    ImageView profilePic;
    TextView postText;
    EditText post_edit_text;
    RecyclerView tag_recyclerview;

    ArrayList<Tag> associated_tags_list = new ArrayList<Tag>();
    ArrayList<Uri> imageUriArray = new ArrayList<Uri>();

    
    Uri imageUri;
    Context new_post_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        new_post_context = this;
        //anonymousOrNot = findViewById(AnonymousOrNormal);
        mainLayout = findViewById(R.id.MainLayout);
        pictureLayout = findViewById(R.id.picturePostHorizontalLayout);
        profilePic = findViewById(R.id.userProfileNewPost);
        post_edit_text = findViewById(R.id.textPostTv);
        select_subject_spinner = findViewById(R.id.select_subject_spinner);
        buttonPostAs = findViewById(R.id.buttonPos);
        buttonPostAs.setOnClickListener(this);

        // Initialize singleton LocalUser to use local data fetched from firebase
        LocalUser local = LocalUser.getCurrentUser();


        // Initialize tag_recyclerview with FlexboxLayout and TagAdapter
        tag_recyclerview = findViewById(R.id.tag_recyclerview);
        FlexboxLayoutManager layout_manager = new FlexboxLayoutManager(new_post_context);
        layout_manager.setJustifyContent(JustifyContent.FLEX_START);
        tag_recyclerview.setLayoutManager(layout_manager);
        final TagAdapter tag_adapter = new TagAdapter(this, associated_tags_list);
        tag_recyclerview.setAdapter(tag_adapter);

        // Listener to post_edit_text which checks for any hashtags entered by the user
        // and adjusts the list of tags accordingly (live updates)
        post_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                String[] words = text.split("\\s+");

                ArrayList<Tag> prev_tags_list = (ArrayList<Tag>) associated_tags_list.clone();

                for (Tag tag : prev_tags_list) {
                    boolean contain_tag = false;
                    for (String word : words) {
                        if (word.startsWith("#")) {
                            if (word.equals("#")) continue;
                            word = word.substring(1);
                            Tag existing_tag = new Tag(word);
                            if (existing_tag.equals(tag)) {
                                contain_tag = true;
                            }
                        }
                    }
                    if (!contain_tag) {
                        associated_tags_list.remove(tag);
                        tag_adapter.notifyDataSetChanged();
                    }
                }

                // check for new tag entered under #... format, cleans it up and adds it to tag list
                for (String word : words) {
                    // is the last word a hashtag
                    if (word.startsWith("#") && word.equals(words[words.length - 1])) {
                        if (word.substring(1).equals("")) return;
                        if (word.charAt(word.length() - 1) == '#') continue;

                        // clean up word, remove all punctuations except hyphen
                        Tag new_tag = new Tag(word.substring(1));
                        if (!associated_tags_list.contains(new_tag)) {
                            associated_tags_list.add(new_tag);
                        }
                    }
                    if (word.startsWith("#")) {
                        if (word.substring(1).isEmpty()) return;
                        if (word.charAt(word.length() - 1) == '#') continue;
                        Tag new_tag = new Tag(word.substring(1));
                        char last_char = s.charAt(s.length() - 1);
                        if (!associated_tags_list.contains(new_tag) && Character.isWhitespace(last_char)) {
                            associated_tags_list.add(new_tag);
                        }
                    }
                }

                tag_adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // update drop down spinner with current available list of subjects from firebase server
        ArrayAdapter<String> subject_select_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, local.getSubjectList());
        select_subject_spinner.setAdapter(subject_select_adapter);
        select_subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_subject = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                current_subject = parent.getItemAtPosition(0).toString();
            }
        });


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
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
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

                profilePic.setImageResource(R.drawable.bunny2);
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

            case R.id.buttonPos:

                Dialog u = Utils.createBottomDialog(this, getPackageManager(), R.layout.choose_anonymous_options_view);
                LinearLayout ll = u.findViewById(R.id.choose_not_anonymous_option_layout);
                LinearLayout ll2 = u.findViewById(R.id.choose_anonymous_option_layout);
                u.show();
                ll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAnonymous=false;
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
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
                                profilePic.setImageResource(R.drawable.bunny2);
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
                        profilePic.setImageResource(R.mipmap.anonymous_icon);
                    }
                });
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
////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FUNCTION THAT HANDLES POSTING, DATA WILL BE SENT TO FIREBASE
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
        ArrayList<String> associated_tags_string_list = Tag.getTagStringList(associated_tags_list);
        QuestionPost newPost;

        String question_text = post_edit_text.getText().toString().replace("#" ,"");


        if(!isAnonymous){
            newPost = new QuestionPost(name,userID,postID, question_text,
                    dateStr,current_subject, associated_tags_string_list,false);
        } else {
            newPost = new QuestionPost(name,userID,postID, question_text,
                    dateStr,current_subject, associated_tags_string_list,true);
        }

        //if there is a picture
        int i=0;
        for(Uri u : imageUriArray){
            newPost.addPostImageIDs(picID+i);
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                    .child(picID+i);
            imageRef.putFile(u).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(NewPostActivity.this, "success upload image", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(NewPostActivity.this, "failed upload image", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            i++;
        }

        //creates the post
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
            imageUriArray.add(getImageUri(new_post_context,imageBitmap));
          
            ImageView imagePost = new ImageView(NewPostActivity.this);
            imagePost.setImageURI(imageUri);
            imagePost.setImageBitmap(imageBitmap);

            imagePost.setMaxHeight(400);
            imagePost.setMaxWidth(400);
            pictureLayout.addView(imagePost);

        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Bitmap bitmap=null;
            imageUri = data.getData();
            imageUriArray.add(imageUri);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageView imagePost = new ImageView(NewPostActivity.this);
            imagePost.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            if(bitmap!=null){
                imagePost.setImageBitmap(Bitmap.createScaledBitmap(bitmap,600,600,true));
            } else {
                imagePost.setImageURI(imageUri);
            }
            imagePost.setScaleType(ImageView.ScaleType.CENTER);
            //imagePost.setImageBitmap(bitmapv2);
            imagePost.setMaxHeight(600);
            imagePost.setMaxWidth(600);
            pictureLayout.addView(imagePost);
        }
    }

    public void popUpImageOptions() {
        final Dialog bottomDialogue = new Dialog(this, R.style.ImageDialogSheet);
        bottomDialogue.setContentView(R.layout.choose_image_options_view);
        bottomDialogue.setCancelable(true);
        bottomDialogue.show();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}


