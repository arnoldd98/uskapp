package com.example.uskapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;

/* View image in full view
 Takes in Bitmap in the Intent provided and sets the current focused image to be the input Bitmap
 Used in any scenario when an image has to be viewed in full view
 Used in PostFocusActivity when user clicks to check the image added to the post
 */
public class ViewImageActivity extends AppCompatActivity {
    ImageView focused_image_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        focused_image_view = (ImageView) findViewById(R.id.focused_image_view);

        Uri image_uri = (Uri) getIntent().getParcelableExtra("ImageUri");
        focused_image_view.setImageURI(image_uri);
    }

    // set animations for transitioning (previous activity slides out left, current activity slides in right)

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
}