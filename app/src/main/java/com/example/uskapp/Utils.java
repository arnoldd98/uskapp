package com.example.uskapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.core.content.FileProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final int CAMERA_REQUEST = 1;
    public static final int PICK_IMAGE = 2;
    private static Uri image_uri;

    //// SELECTING IMAGE OPTIONS ////
    /* Create a dialog which pops up at the bottom of the screen, asking user to select images from
       camera or gallery.
       Order of use:
       1. call popUpImageOptions(...) when the dialog is needed to pop up
       2. getImageUri() to get Uri of image taken (from camera). Have a Uri variable in your activity
          to store this imageUri, as the assignment differes between camera and gallery
       Activity is responsible for dealing with activity result. Example for onActivityResult() below

       Inputs: activity (use ActivityName.this), package manager (call getPackageManager())
       Example:
            get_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.popUpImageOptions(PostFocusActivity.this, getPackageManager());
            }
        });

        Refer to PostFocusActivity for use case
     */

    public static void popUpImageOptions(final Activity activity, final PackageManager packageManager) {
        final Dialog bottomDialogue = new Dialog(activity, R.style.ImageDialogSheet);
        bottomDialogue.setContentView(R.layout.choose_image_options_view);

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
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (camera_intent.resolveActivity(packageManager) != null) {
                    bottomDialogue.cancel();

                File out_image = null;
                try {
                    out_image = createImageFile(activity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (out_image != null) {
                    image_uri = FileProvider.getUriForFile(activity, "com.example.android.provider", out_image);
                    camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                }
                System.out.println("Image URI: " + image_uri);
                activity.startActivityForResult(camera_intent, CAMERA_REQUEST);
            }
            }
        });


        LinearLayout gallery_option_layout = (LinearLayout) bottomDialogue.findViewById(R.id.choose_from_gallery_selectable_layout);
        gallery_option_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialogue.cancel();
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);

                activity.startActivityForResult(gallery_intent, PICK_IMAGE);
            }
        });

    }

    // Assign image_uri to variable in your activity
    public static Uri getImageUri() {
        return image_uri;
    }

    private static File createImageFile(final Activity activity) throws IOException {
        // get image timestamp
        Date date = new Date();
        DateFormat date_format = new SimpleDateFormat("MMdd_HHmm");

        String new_pic_path = date_format.format(date) + "-uskapp";
        File image = File.createTempFile(new_pic_path, ".jpg", activity.getFilesDir());

        return image;
    }

    /*
        Example of onActivityResult(...) which calls after you finish taking image using camera or from gallery.
        image_uri refers to class variable in Activity storing the Uri of image taken.

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Utils.CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    view_added_image_selector.setVisibility(View.VISIBLE);
                }
                break;
            case Utils.PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    image_uri = data.getData();
                    view_added_image_selector.setVisibility(View.VISIBLE);
                }
                break;
            }
        }

    */
    //// END OF SELECTING IMAGE OPTIONS ////


    //// Updating Navigation Bar state when back button is pressed ////
    public static void updateNavigationBarState(int selectedId, BottomNavigationView bottomNavigationView) {
        Menu menu = bottomNavigationView.getMenu();
        for (int i = 0, size = menu.size(); i<size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == selectedId);
        }
    }
    /// END ///

}
