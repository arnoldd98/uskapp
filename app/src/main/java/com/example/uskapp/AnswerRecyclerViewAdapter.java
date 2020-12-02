package com.example.uskapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AnswerRecyclerViewAdapter extends RecyclerView.Adapter<AnswerRecyclerViewAdapter.ViewHolder> {
    private List<AnswerPost> answer_data;
    private ArrayList<Bitmap> answerProfileBitmaps;
    private ArrayList<Bitmap> ArrayListAnswerImages;
    private Context ctx;

    public AnswerRecyclerViewAdapter(Context ctx, List<AnswerPost> answer_data,ArrayList<Bitmap> answerProfileBitmaps, ArrayList<Bitmap> ArrayListAnswerImages) {
        this.answer_data = answer_data;
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

        if (answer.toggle_anonymity==true){
            holder.answerer_profile_iv.setImageResource(R.drawable.image);
            holder.answer_author_name_tv.setText("Anonymous");
        }
        else {
            //loading of replier's profile pic

            //bitmap tends to not be properly loaded
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

        if(ArrayListAnswerImages != null){
            ImageView imageView = new ImageView(ctx);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            try {
                imageView.setImageBitmap(Bitmap.createScaledBitmap(ArrayListAnswerImages.get(position),600,600,true));
                holder.clickable_to_images_layout1.addView(imageView);
            } catch (Exception e){

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
                    if(upvoteIDs.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) ){
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
                    newUsersID.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    postRef2.setValue(newUsersID);
                } else {
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
        });



        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(holder.clickable_to_images_layout1.getHeight(),
                holder.clickable_to_images_layout1.getHeight());
        int image_count = 0;
        /*
        if (answer.getImages() != null) {
            for (byte[] image : answer.getImages()) {
                ImageView image_view = new ImageView(holder.answer_card_context);
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                image_view.setImageBitmap(Bitmap.createScaledBitmap(bmp, image_view.getWidth()
                        , image_view.getHeight(), false));
                image_view.setId(image_count);
                if (image_count == 0) {
                    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    image_view.setLayoutParams(lp);
                    holder.clickable_to_images_layout.addView(image_view);
                    lp.removeRule(RelativeLayout.ALIGN_LEFT);
                } else {
                    lp.addRule(RelativeLayout.RIGHT_OF, image_count - 1);
                    image_view.setLayoutParams(lp);
                    holder.clickable_to_images_layout.addView(image_view);
                    lp.removeRule(RelativeLayout.RIGHT_OF);
                }
                image_count++;

                holder.clickable_to_images_layout.addView(image_view);
            }
            holder.image_count_textview.setText("View " + image_count + " images");
        } else {
            holder.answer_container_layout.removeView(holder.clickable_to_images_layout);
        }

         */
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout answer_container_layout1;
        public ImageView answerer_profile_iv;
        public TextView answer_author_name_tv;
        public TextView answer_post_timestamp_tv;
        public Button up_answer_btn;
        public TextView answer_tv;
        public RelativeLayout clickable_to_images_layout1;
        public TextView image_count_textview;

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
