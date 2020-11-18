package com.example.uskapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import java.io.File;
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
                //anonymousOptions();
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

    public static void anonymousOptions(final Activity activity, final PackageManager packageManager) {
        final Dialog bottomDialogue = new Dialog(activity, R.style.ImageDialogSheet);
        bottomDialogue.setContentView(R.layout.choose_anonymous_options_view);

        WindowManager.LayoutParams dialog_params = new WindowManager.LayoutParams();
        dialog_params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog_params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog_params.gravity = Gravity.BOTTOM;
        dialog_params.windowAnimations = R.style.SlideDialogAnimation;
        bottomDialogue.getWindow().setAttributes(dialog_params);
        bottomDialogue.getWindow().setBackgroundDrawableResource(R.drawable.holo_gray_btn);

        bottomDialogue.setCanceledOnTouchOutside(true);
        bottomDialogue.setCancelable(true);
        bottomDialogue.show();

        LinearLayout camera_option_layout = (LinearLayout) bottomDialogue.findViewById(R.id.open_camera_selectable_layout);
        camera_option_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Robloks");
            }
        });
    }

}


