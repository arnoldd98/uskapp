package com.example.uskapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;

public class NewPostActivity extends AppCompatActivity {
    static final int CAMERA_REQUEST = 1;
    Button buttonTags;
    Button buttonPostAs;
    ImageButton cameraButton;
    ImageButton picturesButton;
    ConstraintLayout.LayoutParams layoutParams;
    ConstraintLayout anonymousOrNot;
    ConstraintLayout mainLayout;
    ImageView imageView;
    private static final int PICK_IMAGE = 2;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        //anonymousOrNot = findViewById(AnonymousOrNormal);
        mainLayout = findViewById(R.id.MainLayout);

        buttonTags = findViewById(R.id.buttonTags);

        imageView = findViewById(R.id.postPicture);

        buttonTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonTags.setText("Clicked");
            }
        });

        buttonPostAs = findViewById(R.id.buttonPostAs);

        buttonPostAs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPostAs.setText("Clicked");
                //Snackbar.make(findViewById(R.id.AnonymousOrNormal),Snackbar.LENGTH_SHORT).show();
            }
        });

        cameraButton = findViewById(R.id.cameraButton);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        picturesButton = findViewById(R.id.picturesButton);

        picturesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
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
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        } else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //image = stream.toByteArray();
                //profilePicIV.setImageDrawable(d);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


