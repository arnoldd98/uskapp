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
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AnswerRecyclerViewAdapter extends RecyclerView.Adapter<AnswerRecyclerViewAdapter.ViewHolder> {
    private List<AnswerPost> answer_data;
    private ArrayList<Bitmap> answerProfileBitmaps;

    public AnswerRecyclerViewAdapter(List<AnswerPost> answer_data,ArrayList<Bitmap> answerProfileBitmaps) {
        this.answer_data = answer_data;
        this.answerProfileBitmaps=answerProfileBitmaps;
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnswerPost answer = answer_data.get(position);

        if (answer.toggle_anonymity){
            holder.answerer_profile_iv.setImageResource(R.drawable.image);
            holder.answer_author_name_tv.setText("Anonymous");
        }
        else {
            if(answerProfileBitmaps.size() ==answer_data.size()){
                Bitmap bitmap = answerProfileBitmaps.get(position);
                holder.answerer_profile_iv.setImageBitmap(Bitmap.createScaledBitmap(bitmap, holder.answerer_profile_iv.getWidth(),
                        holder.answerer_profile_iv.getHeight(), false));
            }
            holder.answer_author_name_tv.setText(answer.getName());
        }

        if (answer.getTimestamp() != null) holder.answer_post_timestamp_tv.setText(answer.getTimestamp());

        holder.up_answer_btn.setText(answer.getUpvotes() + " ups");
        holder.answer_tv.setText(answer.getText());


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
