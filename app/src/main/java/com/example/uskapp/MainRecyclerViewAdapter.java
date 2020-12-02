package com.example.uskapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> implements Filterable {
    private List<QuestionPost> post_data;
    private List<QuestionPost> post_data_all; //all filtered question postss

    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener post_click_listener;
    private Activity activity;
    private ArrayList<Bitmap> profileBitmaps;
    private ArrayList<String> currentUserPostFollowing;
    private Bitmap bitmap;
    private LocalUser local_user = LocalUser.getCurrentUser();


    public MainRecyclerViewAdapter(Activity activity, List<QuestionPost> post_data
            , ArrayList<Bitmap> profileBitmaps,ArrayList<String> currentUserPostFollowing) {
        this.post_data = post_data;
        this.activity = activity;
        this.profileBitmaps =profileBitmaps;
        this.mInflater = LayoutInflater.from(activity.getApplicationContext());
        this.currentUserPostFollowing=currentUserPostFollowing;

    }

    public MainRecyclerViewAdapter(Activity activity, List<QuestionPost> post_data
            , ArrayList<Bitmap> profileBitmaps) {
        this.post_data = post_data;
        this.post_data_all = new ArrayList<>(post_data);

        this.activity = activity;
        this.profileBitmaps = profileBitmaps;
        this.mInflater = LayoutInflater.from(activity.getApplicationContext());
        this.currentUserPostFollowing=currentUserPostFollowing;

    }


    @Override
    public int getItemCount() {
        if(post_data==null){
            return 0;
        }
        return post_data.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.question_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    // set widget components according to post content
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final QuestionPost post = post_data.get(position);
        holder.card_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Post postToSend = post_data.get(position);
                String postIDToSend = postToSend.getPostID();
                Intent viewPostIntent = new Intent(activity, PostFocusActivity.class);
                viewPostIntent.putExtra("postID",postIDToSend);
                activity.startActivity(viewPostIntent);
            }
        });

        if (post.toggle_anonymity){
            holder.profile_image_view.setImageResource(R.drawable.image);
            holder.question_author_name.setText("Anonymous");
        }
        else {

            //bitmap tends not to be properly loaded
            try{
                Bitmap bmp = profileBitmaps.get(position);
                holder.profile_image_view.setImageBitmap(Bitmap.createScaledBitmap(bmp, holder.profile_image_view.getWidth(),
                        holder.profile_image_view.getHeight(), false));
            } catch (Exception e){
                holder.profile_image_view.setImageResource(R.drawable.ic_launcher_foreground);
            }



            holder.question_author_name.setText(post.getName());


        }

        if (post.getTimestamp() != null) holder.post_timestamp.setText(post.getTimestamp());
        else holder.post_timestamp.setText("Missing timestamp");

        holder.question_textview.setText(post.getText());
        // set recyclerview showing tags of post under question text
        if (post.getTagsList() != null) {
            FlexboxLayoutManager layout_manager = new FlexboxLayoutManager(activity);
            layout_manager.setJustifyContent(JustifyContent.FLEX_START);
            holder.tag_recyclerview.setLayoutManager(layout_manager);
            TagAdapter tag_adapter = new TagAdapter(activity, post.getTagsList(), true);
            holder.tag_recyclerview.setAdapter(tag_adapter);
        } else {
            holder.card_container.removeView(holder.tag_recyclerview);
        }

        //if there are
        if (post.getPostImageIDs().size()!=0) {

            ImageView image_view = new ImageView(holder.card_view_context);

            //firebase getting image
            StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            imageRef.getBytes(2048*2048)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
            if(bitmap != null){
                image_view.setImageBitmap(Bitmap.createScaledBitmap(bitmap, image_view.getWidth()
                        , image_view.getHeight(), false));
                image_view.setMaxHeight(holder.image_layout.getHeight());

                holder.image_layout.addView(image_view);
            }

        } else {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(holder.constraint_layout_container);
            cs.connect(R.id.tag_recyclerview, ConstraintSet.BOTTOM, R.id.ups_indicator_layout, ConstraintSet.TOP, 8);
            holder.constraint_layout_container.removeView(holder.image_layout);
            cs.applyTo(holder.constraint_layout_container);
        }

        holder.comment_indicator_textview.setText(String.valueOf(post_data.get(position).getAnswerPostIDs().size()));
        holder.ups_indicator_textview.setText(post.getUpvotes() + " ups");
        for(String upvoteIDs : post.getUsersWhoUpVoted()){
            if(upvoteIDs.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                holder.ups_indicator_image.setImageResource(R.drawable.blue_triangle);
            }
        }

        holder.ups_indicator_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = post_data.get(position);
                holder.ups_indicator_image.setImageResource(R.drawable.blue_triangle);
                Toast.makeText(activity, String.valueOf(position), Toast.LENGTH_SHORT).show();
                boolean valid = true;
                for(String upvoteIDs : post.getUsersWhoUpVoted()){
                    if(upvoteIDs.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                        valid=false;
                    }
                }

                if(valid){
                    post.increaseUpVote();
                    givePosterKarma(post.getUserID());
                    int i = post.getUpvotes();
                    String id = post_data.get(position).getPostID();
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("upvotes");
                    postRef.setValue(i);
                    DatabaseReference postRef2 = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("usersWhoUpVoted");
                    ArrayList<String> newUsersID = post.getUsersWhoUpVoted();
                    newUsersID.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    postRef2.setValue(newUsersID);
                } else {
                    Toast.makeText(activity, "already voted", Toast.LENGTH_SHORT).show();
                }
            }

        });

        final boolean[] is_favourited = {false};
        if (local_user.getFollowingPostIDs().contains(post.getPostID())) {
            holder.favourite_question_button.setBackgroundResource(R.drawable.star_favourited);
            is_favourited[0] = true;
        } else {
            holder.favourite_question_button.setBackgroundResource(R.drawable.star_unselected);
            is_favourited[0] = false;
        }

        holder.favourite_question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(local_user.getFollowingPostIDs());
                if (is_favourited[0]) {
                    holder.favourite_question_button.setBackgroundResource(R.drawable.star_unselected);
                    local_user.unfavouritePost(post.getPostID());
                    is_favourited[0] = false;
                } else {
                    holder.favourite_question_button.setBackgroundResource(R.drawable.star_favourited);
                    local_user.favouritePost(post.getPostID());
                    is_favourited[0] = true;
                }
            }
        });

//        holder.favourite_question_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Post post = post_data.get(position);
//                holder.favourite_question_button.setBackgroundResource(R.drawable.star_favourited);
//                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").
//                        child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String email = snapshot.child("email").getValue(String.class);
//                        String name = snapshot.child("name").getValue(String.class);
//                        int karma = snapshot.child("karma").getValue(Integer.class);
//                        int total_posts = snapshot.child("total_posts").getValue(Integer.class);
//                        int total_answers = snapshot.child("total_answers").getValue(Integer.class);
//                        DataSnapshot arrayFollowingPosts = snapshot.child("postFollowing");
//
//                        ArrayList<String> tempPostFollowing = new ArrayList<String>();
//
//                        for(DataSnapshot s : arrayFollowingPosts.getChildren()){
//                            String postfollowingId = s.getValue(String.class);
//                            tempPostFollowing.add(postfollowingId);
//                        }
//
//                        User currentUser = new User(name,email,karma,total_posts,total_answers);
//                        //currentUser.setPostFollowing(tempPostFollowing);
//                        boolean valid = true;
//                        for(String postsFollowed : tempPostFollowing){
//                            if(post_data.get(position).getPostID().equals(postsFollowed)){
//                                valid = false;
//                                //Toast.makeText(view.getContext(), "follow failed", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        //if current post is not followed
//                        if(valid){
//                            holder.favourite_question_button.setBackgroundResource(R.drawable.star_red);
//                            tempPostFollowing.add(post_data.get(position).getPostID());
//                            currentUser.setPostFollowing(tempPostFollowing);
//                            FirebaseDatabase.getInstance().getReference("Users")
//                                    .child(FirebaseAuth.getInstance().getUid()).setValue(currentUser)
//                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                        @Override
//                                        public void onComplete(@NonNull Task<Void> task) {
//                                        }
//                                    });
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//
//            }
//        });


        /*
        // set on click listener on star button to toggle if post should be favourited by user
        holder.favourite_question_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    FirebaseMessaging.getInstance().subscribeToTopic(post.getPostID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String subscribed = "Successfully subscribed!";
                            if (!task.isSuccessful()) {
                                subscribed = "Failed to subscribe";
                            }
                            Log.d(TAG, subscribed);
                            Toast.makeText(activity, subscribed, Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(post.getPostID()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            String unsubscribed = "Successfully unsubscribed!";
                            if (!task.isSuccessful()) {
                                unsubscribed = "Failed to unsubscribe";
                            }
                            Log.d(TAG, unsubscribed);
                            Toast.makeText(activity, unsubscribed, Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.favourite_question_button.setBackgroundResource(R.drawable.star_unselected);
                }
            }
        });
        */

    }

    @Override
    public Filter getFilter() { //New Method, use list of all questionposts
        return filter;
    }

    Filter filter = new Filter() {
        //run on background thread
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) { //logic for filtering
            List<QuestionPost> filteredList = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filteredList.addAll(post_data_all);
            } else{
                for (QuestionPost questionpost: post_data_all){
                    if (questionpost.getText().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filteredList.add(questionpost);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }
        //runs on an UI thread
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            post_data.clear();
            System.out.println("SearchBar updated");
            System.out.println(charSequence);
            post_data.addAll((Collection<? extends QuestionPost>) filterResults.values);
            System.out.println(post_data);
            notifyDataSetChanged();

        }
    };

    // Create ViewHolder class, and specify the UI components which value are to be defined in the QuestionPost class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card_container;
        public ConstraintLayout constraint_layout_container;
        public ImageView profile_image_view;
        public TextView question_author_name;
        public TextView post_timestamp;
        public TextView question_textview;
        public Toolbar options_menu_toolbar;
        public RecyclerView tag_recyclerview;
        public LinearLayout image_layout;
        public LinearLayout ups_indicator_layout;
        public ImageView ups_indicator_image;
        public TextView ups_indicator_textview;
        public LinearLayout comment_indicator_layout;
        public TextView comment_indicator_textview;
        public Button favourite_question_button;

        public Context card_view_context;

        public ViewHolder(View postView) {
            super(postView);
            card_container = (CardView) postView.findViewById(R.id.card_container);
            constraint_layout_container = (ConstraintLayout) postView.findViewById(R.id.constraint_layout_container);
            profile_image_view = (ImageView) postView.findViewById(R.id.profile_imageview);
            question_author_name = (TextView) postView.findViewById(R.id.question_author_name);
            post_timestamp = (TextView) postView.findViewById(R.id.post_timestamp);
            question_textview = (TextView) postView.findViewById(R.id.question_textview);
            options_menu_toolbar = (Toolbar) postView.findViewById(R.id.options_menu_toolbar);
            tag_recyclerview = (RecyclerView) postView.findViewById(R.id.question_tag_recyclerview);
            image_layout = (LinearLayout) postView.findViewById(R.id.image_horizontal_linear_layout);
            ups_indicator_layout = (LinearLayout) postView.findViewById(R.id.ups_indicator_layout);
            ups_indicator_image = (ImageView) postView.findViewById(R.id.ups_indicator_imageview);
            ups_indicator_textview = (TextView) postView.findViewById(R.id.ups_indicator_textview);
            comment_indicator_layout = (LinearLayout) postView.findViewById(R.id.comment_indicator__layout);
            comment_indicator_textview = (TextView) postView.findViewById(R.id.comment_indicator__textview);
            favourite_question_button = (Button) postView.findViewById(R.id.star_question_button);

            card_view_context = postView.getContext();
        }
    }
    private void followPost(String postID){
        final DatabaseReference posterRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(postID);
        posterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int karma = snapshot.child("karma").getValue(Integer.class);
                karma +=1;
                posterRef.child("karma").setValue(karma);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void givePosterKarma(String posterID){
        final DatabaseReference posterRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(posterID);
        posterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int karma = snapshot.child("karma").getValue(Integer.class);
                karma +=1;
                posterRef.child("karma").setValue(karma);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

