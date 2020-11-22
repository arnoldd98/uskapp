package com.example.uskapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

// singleton class which acts as a wrapper for current user
// stores any local data in device

public class LocalUser implements Observer {
    private static LocalUser current_user = null;
    private static final String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> postFollowing = new ArrayList<String>();

    public LocalUser() {
        super();
    }


    public DatabaseReference getUserRef() {
        return FirebaseDatabase.getInstance().getReference("Users").child(current_user_id);
    }

    public StorageReference getUserProfilePicRef() {
        return FirebaseStorage.getInstance()
                .getReference("ProfilePictures").child(current_user_id);
    }

    public String getCurrentUserId() {
        return current_user_id;
    }

    public static LocalUser getCurrentUser() {
        if (current_user == null) {
            current_user = new LocalUser();
        }
        return current_user;
    }

    public ArrayList<String> getPostFollowing() {
        return postFollowing;
    }

    public void setPostFollowing(ArrayList<String> postFollowing) {
        this.postFollowing = postFollowing;
    }

    @Override
    public void update(String updates) {
        // what to update whenever the post u are following updates
    }
}
