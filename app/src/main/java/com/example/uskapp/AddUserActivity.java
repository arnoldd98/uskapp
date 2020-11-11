package com.example.uskapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

public class AddUserActivity extends AppCompatActivity {
    EditText firstName,lastName,password,email;
    ImageView profilePicIV;
    Button doneBtn,addProfilePic;
    Uri imageUri;
    byte[] image=null;

    private static final int PICK_IMAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        doneBtn = findViewById(R.id.submitBtn);
        profilePicIV =findViewById(R.id.profilepicIV);
        addProfilePic = findViewById(R.id.addPicture);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                try {
//
//
//                    if( image != null){
//
//                        //Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
//                        User myUser = new User(firstName.getText().toString()+" "+lastName.getText().toString(),email.getText().toString(),password.getText().toString(),image);
//                        Intent intent = new Intent(AddUserActivity.this,ProfileActivity.class);
//                        intent.putExtra("user",myUser);
//                        intent.putExtra("key","value");
//                        startActivity(intent);
//                    }
//                } catch (Exception e){
//                    e.printStackTrace();
//                }

//                User myUser = new User(firstName.getText().toString()+" "+lastName.getText().toString(),email.getText().toString(),password.getText().toString());
//                Intent intent = new Intent(AddUserActivity.this,ProfileActivity.class);
//                intent.putExtra("user",myUser);
//                startActivity(intent);
            }
        });

        addProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(intent,PICK_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== PICK_IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();

            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                image = stream.toByteArray();

                RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                d.setCircular(true);
                profilePicIV.setImageDrawable(d);
                //profilePicIV.setImageBitmap(bitmap);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}