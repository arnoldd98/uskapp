package com.example.uskapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

// singleton class which acts as a wrapper for current user
// stores any local data in device

public class LocalUser {
    private static LocalUser current_user = null;
    private static final String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> postFollowing = new ArrayList<String>();
    private HashMap<String, Bitmap> subjectImageHashmap = new HashMap<String, Bitmap>();

    public LocalUser() {
        super();
        getSubjectRef();
    }

    public DatabaseReference getSubjectRef() {
        DatabaseReference mSubjectDatabase = FirebaseDatabase.getInstance().getReference("Subject");
        mSubjectDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (subjectImageHashmap != null) {
                    subjectImageHashmap.clear();
                }
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        final String subject = s.getValue(String.class);

                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("SubjectPictures")
                                .child(subject + ".jpg");
                        imageRef.getBytes(2048 * 2048)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                          @Override
                                                          public void onSuccess(byte[] bytes) {
                                                              Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                              subjectImageHashmap.put(subject, bitmap);
                                                          }
                                                      }
                                ).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                subjectImageHashmap.put(subject, null);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mSubjectDatabase;
    }

    public ArrayList<String> getSubjectList() {
        ArrayList<String> subject_list = new ArrayList<String>();
        for (String subj : subjectImageHashmap.keySet()) {
            subject_list.add(subj);
        }
        return subject_list;
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

    public ArrayList<String> getPostFollowing() {
        return postFollowing;
    }

    public void setPostFollowing(ArrayList<String> postFollowing) {
        this.postFollowing = postFollowing;
    }

    // singleton function to get the current working LocalUser
    public static LocalUser getCurrentUser() {
        if (current_user == null) {
            current_user = new LocalUser();
        }
        return current_user;
    }
}
