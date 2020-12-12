package com.example.uskapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

// singleton class which acts as a wrapper for current user
// stores any local data in device

public class LocalUser {
    private static LocalUser current_user = null;
    private static final String current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private ArrayList<String> followed_post_ids = new ArrayList<String>();
    private ArrayList<String> users_who_upvoted_ids = new ArrayList<String>();
    private HashMap<String, Bitmap> subjectImageHashmap = new HashMap<String, Bitmap>();

    public LocalUser() {
        getSubjectRef();
        fetchFollowedPostIDsFromFirebase();
        listenFollowedPosts();
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

    public ArrayList<String> getFollowingPostIDs() {
        return followed_post_ids;
    }
    public ArrayList<String> getUserWhoUpvotedIDs() {
        return users_who_upvoted_ids;
    }

    public void setFollowingPostIDs(ArrayList<String> followed_post_ids) {
        this.followed_post_ids = followed_post_ids;
    }
    public void setUserWhoUpvotedIDs(ArrayList<String> users_who_upvoted_ids) {
        this.users_who_upvoted_ids = users_who_upvoted_ids;
    }

    public void unfavouritePost(String removed_post_id) {
        followed_post_ids.remove(removed_post_id);
        listenFollowedPosts();
    }

    public boolean favouritePost(String favourite_id) {
        if (followed_post_ids.contains(favourite_id)) return false;

        followed_post_ids.add(favourite_id);
        listenFollowedPosts();
        return true;
    }



    // singleton function to get the current working LocalUser
    public static LocalUser getCurrentUser() {
        if (current_user == null) {
            current_user = new LocalUser();
        }
        return current_user;
    }

     // updates the firebase user whenever the list of followed post is changed locally
    private void listenFollowedPosts() {
        DatabaseReference userRef = getUserRef();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String email = snapshot.child("email").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                int karma = snapshot.child("karma").getValue(Integer.class);
                int total_posts = snapshot.child("total_posts").getValue(Integer.class);
                int total_answers = snapshot.child("total_answers").getValue(Integer.class);
                DataSnapshot arrayFollowingPosts = snapshot.child("postFollowing");

                ArrayList<String> fb_following_posts_id = new ArrayList<String>();
                for(DataSnapshot s : arrayFollowingPosts.getChildren()){
                    String postfollowingId = s.getValue(String.class);
                    fb_following_posts_id.add(postfollowingId);
                }

                if (!listEqualsIgnoreOrder(getFollowingPostIDs(), fb_following_posts_id)) {
                    User currentUser = new User(name,email,karma,total_posts,total_answers);
                    currentUser.setPostFollowing(getFollowingPostIDs());

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getUid()).setValue(currentUser)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // fetch list of followed posts from firebase
    private void fetchFollowedPostIDsFromFirebase() {
        getUserRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot postFollowingID = snapshot.child("postFollowing");
                for(DataSnapshot id : postFollowingID.getChildren()){
                    String str = id.getValue(String.class);
                    favouritePost(str);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // simple helper function to check if two arraylists have the same elements (in any order)
    private static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
        return new HashSet<>(list1).equals(new HashSet<>(list2));
    }
}
