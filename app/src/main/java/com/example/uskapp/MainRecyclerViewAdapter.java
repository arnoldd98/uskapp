package com.example.uskapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import java.util.Collections;
import java.util.Collection;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> implements Filterable {
    private ArrayList<QuestionPost> post_data;
    private ArrayList<QuestionPost> post_data_all; //all filtered question postss
    private LayoutInflater mInflater;
    private Activity activity;
    private ArrayList<Bitmap> profileBitmaps;
    private LocalUser local_user = LocalUser.getCurrentUser();
    final DatabaseReference questionPostDatabase = FirebaseDatabase.getInstance().getReference("QuestionPost");

    public MainRecyclerViewAdapter(Activity activity, ArrayList<QuestionPost> post_list
            , ArrayList<Bitmap> profileBitmaps) {
        this.post_data = post_list;
        this.post_data_all = new ArrayList<QuestionPost>();
        for (QuestionPost questionPost : post_list) {
            post_data_all.add(questionPost);
        }
        this.activity = activity;
        this.profileBitmaps = profileBitmaps;
        this.mInflater = LayoutInflater.from(activity.getApplicationContext());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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
        //THE RECYCLER LOGIC IS SCREWING IT UP NEED LOOK IT UP
        //holder.setIsRecyclable(false);
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
                questionPostDatabase.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                        String id = post_data.get(position).getPostID();
                        String userID = (String) dataSnapshot.child(id).child("userID").getValue();
                        StorageReference imageRef = FirebaseStorage.getInstance().getReference("ProfilePictures")
                                .child(userID);
                        imageRef.getBytes(2048 * 2048)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        holder.profile_image_view.setImageBitmap(Bitmap.createScaledBitmap(bitmap, holder.profile_image_view.getWidth(),
                                                holder.profile_image_view.getHeight(), false));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        holder.profile_image_view.setImageResource(R.drawable.bunny2);
                                        e.printStackTrace();
                                    }
                                    });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        holder.profile_image_view.setImageResource(R.drawable.bunny2);
                    }

                });
            }catch (Exception e){
                System.out.println(e);
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

        if (post.getPictures().size()!=0) {
            //prevents over adding
            if(holder.image_layout.getChildCount() < post.getPictures().size()){
                for(Bitmap bitmap : post.getPictures()){
                    ImageView image_view = new ImageView(holder.card_view_context);
                    image_view.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400
                            , 400 , false));
                    image_view.setMaxHeight(holder.image_layout.getHeight());

                    holder.image_layout.addView(image_view);

                }
            }





            /*
        if (questionBitmaps.get(position).size()>0) {
            if(holder.image_layout.getChildCount() == 0) {
                for (Bitmap bitmap : questionBitmaps.get(position)) {

                /*
                //firebase getting image
                StorageReference imageRef = FirebaseStorage.getInstance().getReference("QuestionPictures")
                        .child(postPicId);
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


                    try {
                        ImageView image_view = new ImageView(holder.card_view_context);
                        image_view.setImageBitmap(bitmap);
                        image_view.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 400
                                , 400, false));
                        image_view.setMaxHeight(holder.image_layout.getHeight());
                        holder.image_layout.addView(image_view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


             */



        } else {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(holder.constraint_layout_container);
            System.out.println("YES YES YES");
            cs.connect(R.id.tag_recyclerview, ConstraintSet.BOTTOM, R.id.ups_indicator_layout, ConstraintSet.TOP, 0);
            cs.connect(R.id.tag_recyclerview, ConstraintSet.TOP, R.id.question_textview, ConstraintSet.BOTTOM, 0);
            cs.applyTo(holder.constraint_layout_container);
        }

        holder.comment_indicator_textview.setText(String.valueOf(post_data.get(position).getAnswerPostIDs().size()));
        holder.ups_indicator_textview.setText(post.getUpvotes() + " ups");
        final boolean[] upvoted = {false};
        for(String upvoteIDs : post.getUsersWhoUpVoted()){
            if(upvoteIDs.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
                holder.ups_indicator_image.setImageResource(R.drawable.blue_triangle);
                upvoted[0] = true;
            }
        }

        holder.ups_indicator_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Post post = post_data.get(position);
                if(upvoted[0]){
                    holder.ups_indicator_image.setImageResource(R.drawable.empty_triangle);
                    int newUpvoteCount = post.getUpvotes()-1;
                    String id = post_data.get(position).getPostID();
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("upvotes");
                    postRef.setValue(newUpvoteCount);
                    DatabaseReference postRef2 = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("usersWhoUpVoted");
                    ArrayList<String> newUsersID = post.getUsersWhoUpVoted();
                    newUsersID.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    postRef2.setValue(newUsersID);
                    upvoted[0] = false;
                }
                else{
                    holder.ups_indicator_image.setImageResource(R.drawable.blue_triangle);
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
                    upvoted[0] = true;
                }

            }

        });

        // checks if post is favourited and toggles favourite button when pressed accordingly
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
            System.out.println(charSequence);;
            post_data.addAll((Collection<? extends QuestionPost>) filterResults.values);
            System.out.println(post_data);
            notifyDataSetChanged();

        }
    };

    // Create ViewHolder class, and specify the UI components which value are to be defined in the QuestionPost class
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView card_container;
        private ConstraintLayout constraint_layout_container;
        private ImageView profile_image_view;
        private TextView question_author_name;
        private TextView post_timestamp;
        private TextView question_textview;
        private RecyclerView tag_recyclerview;
        private LinearLayout image_layout;
        private LinearLayout ups_indicator_layout;
        private ImageView ups_indicator_image;
        private TextView ups_indicator_textview;
        private LinearLayout comment_indicator_layout;
        private TextView comment_indicator_textview;
        private Button favourite_question_button;

        private Context card_view_context;

        public ViewHolder(View postView) {
            super(postView);
            card_container = (CardView) postView.findViewById(R.id.card_container);
            constraint_layout_container = (ConstraintLayout) postView.findViewById(R.id.constraint_layout_container);
            profile_image_view = (ImageView) postView.findViewById(R.id.profile_imageview);
            question_author_name = (TextView) postView.findViewById(R.id.question_author_name);
            post_timestamp = (TextView) postView.findViewById(R.id.post_timestamp);
            question_textview = (TextView) postView.findViewById(R.id.question_textview);
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

