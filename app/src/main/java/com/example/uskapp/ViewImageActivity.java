package com.example.uskapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ortiz.touchview.TouchImageView;

import java.io.FileNotFoundException;

/* View image in full view
 Takes in Bitmap in the Intent provided and sets the current focused image to be the input Bitmap
 Used in any scenario when an image has to be viewed in full view
 Used in PostFocusActivity when user clicks to check the image added to the post

 Specify "ImageUri" in intent for incoming image URI
 Specify "ImageBitmap" in intent for incoming image bitmap
 Specify "PostText" in intent for post content
 */
public class ViewImageActivity extends AppCompatActivity {
    RelativeLayout image_focus_container_layout;
    TouchImageView focused_image_view;
    ImageButton exit_image_focus_button;
    LinearLayout show_associated_post_layout;
    TextView associated_post_text;
    boolean options_visible;

    final public static String BITMAP_PATH_KEY = "BitmapPath";
    final public static String URI_KEY = "ImageUri";
    final public static String TEXT_KEY = "PostText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        image_focus_container_layout = (RelativeLayout) findViewById(R.id.image_focus_container_layout);

        exit_image_focus_button = (ImageButton) findViewById(R.id.exit_image_focus_button);
        exit_image_focus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        show_associated_post_layout = (LinearLayout) findViewById(R.id.show_associated_post_layout);

        // get associated post text from intent
        // if called from PostFocusActivity (when answering questions),
        // set the post text to whatever was written in the EditText before sending
        associated_post_text = (TextView) findViewById(R.id.associated_post_text);
        String post_text = getIntent().getStringExtra(TEXT_KEY);
        if (!post_text.isEmpty()) associated_post_text.setText(post_text);
        else {
            image_focus_container_layout.removeView(show_associated_post_layout);
            show_associated_post_layout = null;
        }

        focused_image_view = (TouchImageView) findViewById(R.id.focused_image_view);

        // set image to URI or Bitmap, depending on which one is included in intent
        Uri image_uri = (Uri) getIntent().getParcelableExtra(URI_KEY);
        String bitmap_path = (String) getIntent().getStringExtra(BITMAP_PATH_KEY);
        if (image_uri != null) focused_image_view.setImageURI(image_uri);
        else {
            try {
                Bitmap bitmap = Utils.loadTempBitmapFromStorage(bitmap_path, getApplicationContext());
                focused_image_view.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Toast.makeText(ViewImageActivity.this, "Image file cannot be found!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


        // tap on screen to bring up image options
        // can view post content, or go back
        focused_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (options_visible) {
                    fadeAnimate(exit_image_focus_button, false);
                    if (show_associated_post_layout != null) {
                        fadeAnimate(show_associated_post_layout, false);
                    }
                    options_visible = false;
                } else {
                    fadeAnimate(exit_image_focus_button, true);
                    if (show_associated_post_layout != null) {
                        fadeAnimate(show_associated_post_layout, true);
                    }
                    options_visible = true;
                }
            }
        });
    }

    public void fadeAnimate(final View view, final boolean setVisible) {
        Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });


        Animation fade_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
        fade_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        if (setVisible) view.startAnimation(fade_in);
        else view.startAnimation(fade_out);
    }

    // set animations for transitioning (slide direction depending on "SlideDir" extra in intent)
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}