package com.example.uskapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
    private List<QuestionPost> post_data;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener post_click_listener;

    public MainRecyclerViewAdapter(Context context, List<QuestionPost> post_data) {
        this.post_data = post_data;
        this.mInflater = LayoutInflater.from(context);
        this.post_click_listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // open up focus post activity
            }
        };
    }

    @Override
    public int getItemCount() {
        return post_data.size();
    }

    @NonNull
    @Override
    public MainRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.question_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    // set widget components according to post content
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionPost post = post_data.get(position);

        ImageView profile_image_view = holder.profile_image_view;
        TextView question_author_name = holder.question_author_name;
        if (post.toggle_anonymity){
            profile_image_view.setImageResource(R.drawable.image);
            question_author_name.setText("Anonymous");
        }
        else {
            byte[] byte_array_pic = post.user.getProfilePic();
            if (byte_array_pic != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(byte_array_pic, 0, byte_array_pic.length);
                profile_image_view.setImageBitmap(Bitmap.createScaledBitmap(bmp, profile_image_view.getWidth(),
                        profile_image_view.getHeight(), false));
            }
            question_author_name.setText(post.user.getName());
        }

        TextView post_timestamp = holder.post_timestamp;
        if (post.timestamp != null) post_timestamp.setText(post.timestamp);
        else post_timestamp.setText("Missing timestamp");

        TextView question_textview = holder.question_textview;
        question_textview.setText(post.text);

        LinearLayout tag_layout = holder.tag_layout;
        if (post.tags_list != null) {
            for (Tag tag : post.tags_list) {
                Button button = new Button(holder.card_view_context);
                button.setText(tag.tag_name);
                // add functionality to search for posts with selected tag when tag button is clicked

                tag_layout.addView(button);
            }
        }

        LinearLayout image_layout = holder.image_layout;
        for (byte[] image : post.images) {
            ImageView image_view = new ImageView(holder.card_view_context);
            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
            image_view.setImageBitmap(Bitmap.createScaledBitmap(bmp, image_view.getWidth()
                    , image_view.getHeight(), false));

            image_layout.addView(image_view);
        }

        TextView ups_indicator_textview = holder.ups_indicator_textview;
        ups_indicator_textview.setText(post.upvotes + " ups");

        TextView comment_indicator_textview = holder.comment_indicator_textview;
        if (post.answers_list != null) {
            comment_indicator_textview.setText(post.answers_list.size() + " answers");
        }
    }


    // Create ViewHolder class, and specify the UI components which value are to be defined in the QuestionPost class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView profile_image_view;
        public TextView question_author_name;
        public TextView post_timestamp;
        public TextView question_textview;
        public Toolbar options_menu_toolbar;
        public LinearLayout tag_layout;
        public LinearLayout image_layout;
        public TextView ups_indicator_textview;
        public TextView comment_indicator_textview;

        public Context card_view_context;

        public ViewHolder(View postView) {
            super(postView);
            profile_image_view = (ImageView) postView.findViewById(R.id.profile_imageview);
            question_author_name = (TextView) postView.findViewById(R.id.question_author_name);
            post_timestamp = (TextView) postView.findViewById(R.id.post_timestamp);
            question_textview = (TextView) postView.findViewById(R.id.question_textview);
            options_menu_toolbar = (Toolbar) postView.findViewById(R.id.options_menu_toolbar);
            tag_layout = (LinearLayout) postView.findViewById(R.id.tag_horizontal_linear_layout);
            image_layout = (LinearLayout) postView.findViewById(R.id.image_horizontal_linear_layout);
            ups_indicator_textview = (TextView) postView.findViewById(R.id.ups_indicator_textview);
            comment_indicator_textview = (TextView) postView.findViewById(R.id.comment_indicator__textview);

            card_view_context = postView.getContext();
        }
    }
}

