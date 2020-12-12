package com.example.uskapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


// RecyclerView Adapter used in PostFocusActivity showing all the answers to the current post

public class AnswerRecyclerViewAdapter extends RecyclerView.Adapter<AnswerRecyclerViewAdapter.ViewHolder> {
    private List<AnswerPost> answer_data;
    private ArrayList<Bitmap> answerProfileBitmaps;
    private ArrayList<Bitmap> ArrayListAnswerImages;
    private Context ctx;

    public AnswerRecyclerViewAdapter(Context ctx, List<AnswerPost> answer_data,ArrayList<Bitmap> answerProfileBitmaps, ArrayList<Bitmap> ArrayListAnswerImages) {
        this.answer_data = answer_data;
        for (AnswerPost answerPost : answer_data) {
            System.out.println("HERE IS IT");
            System.out.println(answerPost.getPostID());
        }
        this.answerProfileBitmaps=answerProfileBitmaps;
        this.ArrayListAnswerImages = ArrayListAnswerImages;
        this.ctx = ctx;
    }

    @Override
    public int getItemCount() {
        if(answer_data==null){
            return 0;
        }
        return answer_data.size();

    }


    @NonNull
    @Override
    public AnswerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View answerView = inflater.inflate(R.layout.answer_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(answerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,final int position) {
        AnswerPost answer = answer_data.get(position);
        // ANONYMITY HANDLING
        if (answer.toggle_anonymity){
            holder.answerer_profile_iv.setImageResource(R.drawable.image);
            holder.answer_author_name_tv.setText("Anonymous");
        }
        else {
            try{
                Bitmap bitmap = answerProfileBitmaps.get(position);
                holder.answerer_profile_iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, holder.answerer_profile_iv.getWidth(),
                        holder.answerer_profile_iv.getHeight(), false));
            } catch (Exception e){
                holder.answerer_profile_iv.setImageResource(R.drawable.ic_launcher_foreground);
            }
            holder.answer_author_name_tv.setText(answer.getName());
        }

        if (answer.getTimestamp() != null) holder.answer_post_timestamp_tv.setText(answer.getTimestamp());
        holder.up_answer_btn.setText(answer.getUpvotes() + " ups");
        holder.answer_tv.setText(answer.getText());
        //  ADDING IMAGES OF REPLIES
        if(ArrayListAnswerImages != null){
            ImageView imageView = new ImageView(ctx);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            try {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(ArrayListAnswerImages.get(position),600,600,true));
                holder.clickable_to_images_layout1.addView(imageView);
            } catch (Exception e){
                e.printStackTrace();
            }
        }



        //upvoting button
        // checks if valid
        // updates AnswerPost data
        holder.up_answer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnswerPost post = answer_data.get(position);
                boolean valid=true;
                for(String upvoteIDs : post.getUsersWhoUpVoted()){
                    if(upvoteIDs.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) ){
                        valid=false;
                    }
                }

                if(valid){
                    post.increaseUpVote();
                    int i = post.getUpvotes();
                    givePosterKarma(post.getUserID());
                    String id = answer_data.get(position).getPostID();
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("AnswerPost")
                            .child(id).child("upvotes");
                    postRef.setValue(i);
                    DatabaseReference postRef2 = FirebaseDatabase.getInstance().getReference("AnswerPost")
                            .child(id).child("usersWhoUpVoted");
                    ArrayList<String> newUsersID = post.getUsersWhoUpVoted();
                    newUsersID.add(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                    postRef2.setValue(newUsersID);
                } else {
                    minusPosterKarma(post.getUserID());
                    String id = answer_data.get(position).getPostID();
                    int i = post.getUpvotes()-1;
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("upvotes");
                    postRef.setValue(i);
                    DatabaseReference postRef2 = FirebaseDatabase.getInstance().getReference("QuestionPost")
                            .child(id).child("usersWhoUpVoted");
                    ArrayList<String> newUsersID = post.getUsersWhoUpVoted();
                    newUsersID.remove(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    postRef2.setValue(newUsersID);
                    Toast.makeText(ctx, "already voted", Toast.LENGTH_SHORT).show();
                }
            }

            private void givePosterKarma(String posterID) {
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
            private void minusPosterKarma(String posterID) {
                final DatabaseReference posterRef = FirebaseDatabase.getInstance().getReference("Users")
                        .child(posterID);
                posterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int karma = snapshot.child("karma").getValue(Integer.class);
                        karma -=1;
                        posterRef.child("karma").setValue(karma);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.clickable_to_images_layout1.getHeight(),
                holder.clickable_to_images_layout1.getHeight());

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout answer_container_layout1;
        public ImageView answerer_profile_iv;
        public TextView answer_author_name_tv;
        public TextView answer_post_timestamp_tv;
        public Button up_answer_btn;
        public TextView answer_tv;
        public RelativeLayout clickable_to_images_layout1;
        public Context answer_card_context;

        public ViewHolder(@NonNull View answerView) {
            super(answerView);
            answer_container_layout1 = (ConstraintLayout) answerView.findViewById(R.id.answer_container_layout) ;
            answerer_profile_iv = (ImageView) answerView.findViewById(R.id.answerer_profile_imageview);
            answer_author_name_tv = (TextView) answerView.findViewById(R.id.answer_author_name);
            answer_post_timestamp_tv = (TextView) answerView.findViewById(R.id.answer_post_timestamp);
            up_answer_btn = (Button) answerView.findViewById(R.id.up_answer_button);
            answer_tv = (TextView) answerView.findViewById(R.id.answer_textview);
            clickable_to_images_layout1 = (RelativeLayout) answerView.findViewById(R.id.clickable_to_images_layout);
            answer_card_context = answerView.getContext();
        }
    }

}
