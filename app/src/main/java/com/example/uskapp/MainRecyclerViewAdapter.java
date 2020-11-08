package com.example.uskapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.TypedValue;
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
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainRecyclerViewAdapter extends RecyclerView.Adapter<MainRecyclerViewAdapter.ViewHolder> {
    private List<QuestionPost> post_data;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener post_click_listener;
    private Activity activity;

    public MainRecyclerViewAdapter(Activity activity, List<QuestionPost> post_data) {
        this.post_data = post_data;
        this.activity = activity;
        this.mInflater = LayoutInflater.from(activity.getApplicationContext());
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"ResourceAsColor", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuestionPost post = post_data.get(position);

        holder.card_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewPostIntent = new Intent(activity, PostFocusActivity.class);
                activity.startActivity(viewPostIntent);

            }
        });

        if (post.toggle_anonymity){
            holder.profile_image_view.setImageResource(R.drawable.image);
            holder.question_author_name.setText("Anonymous");
        }
        else {
            byte[] byte_array_pic = post.getUser().getProfilePic();
            if (byte_array_pic != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(byte_array_pic, 0, byte_array_pic.length);
                holder.profile_image_view.setImageBitmap(Bitmap.createScaledBitmap(bmp, holder.profile_image_view.getWidth(),
                        holder.profile_image_view.getHeight(), false));
            }
            holder.question_author_name.setText(post.getUser().getName());
        }

        if (post.getTimestamp() != null) holder.post_timestamp.setText(post.getTimestamp());
        else holder.post_timestamp.setText("Missing timestamp");

        holder.question_textview.setText(post.getText());

        if (post.getTagsList() != null) {
            for (Tag tag : post.getTagsList()) {
                // programmatically create button for each tag and add to horizontal linear layout
                Context context = holder.tag_layout.getContext();
                TextView clickable_tag = new TextView(context);
                clickable_tag.setText(tag.tag_name);
                clickable_tag.setBackground(ContextCompat.getDrawable(context, R.drawable.rounded_rectangle));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0, 0, 8, 8);
                clickable_tag.setLayoutParams(lp);
                clickable_tag.setTextSize(12);
                clickable_tag.setTextColor(Color.parseColor("#707070"));
                clickable_tag.setPadding(20, 6, 20, 6);
                clickable_tag.setClickable(true);
                TypedValue outValue = new TypedValue();
                context.getTheme().resolveAttribute(
                        android.R.attr.selectableItemBackground, outValue, true);
                clickable_tag.setForeground(context.getDrawable(outValue.resourceId));


                // add functionality to search for posts with selected tag when tag button is clicked

                holder.tag_layout.addView(clickable_tag);
            }
        }

        if (!post.getImages().isEmpty()) {
            for (byte[] image : post.getImages()) {
                ImageView image_view = new ImageView(holder.card_view_context);
                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                image_view.setImageBitmap(Bitmap.createScaledBitmap(bmp, image_view.getWidth()
                        , image_view.getHeight(), false));
                image_view.setMaxHeight(holder.image_layout.getHeight());

                holder.image_layout.addView(image_view);
            }
        } else {
            ConstraintSet cs = new ConstraintSet();
            cs.clone(holder.card_container);
            cs.connect(R.id.tag_horizontal_linear_layout, ConstraintSet.BOTTOM, R.id.ups_indicator_textview, ConstraintSet.TOP, 8);
            holder.card_container.removeView(holder.image_layout);
            cs.applyTo(holder.card_container);
        }

        holder.ups_indicator_textview.setText(post.getUpvotes() + " ups");

        if (post.getAnswersList() != null) {
            holder.comment_indicator_textview.setText(post.getAnswersList().size() + " answers");
        }
    }


    // Create ViewHolder class, and specify the UI components which value are to be defined in the QuestionPost class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout card_container;
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
            card_container = (ConstraintLayout) postView.findViewById(R.id.card_container);
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

