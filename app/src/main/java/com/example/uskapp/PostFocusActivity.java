package com.example.uskapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostFocusActivity extends AppCompatActivity {
    RecyclerView answer_recyclerview;
    static ArrayList<AnswerPost> answerPosts;
    ImageButton back_to_main_button;

    EditText user_answer_edittext;
    ImageButton send_answer_button;
    ImageButton get_image_button;
    TextView view_added_image_selector;
    Context context;

    Uri image_uri;

    static final int CAMERA_REQUEST = 1;
    static final int PICK_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_focus);
        context = this;

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        // initialize back button, on click it returns back to the home activity
        back_to_main_button = (ImageButton) findViewById(R.id.back_to_main_button);
        back_to_main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, HomeActivity.class));
            }
        });

        // get answers to question in view from Firebase
        answerPosts = new ArrayList<>();
        User custom_user = new User("Jeb", "JJJ", null);
        for (int i=0; i<5; i++) {
            answerPosts.add(new AnswerPost(custom_user, false, "This is a sample answer", null));
        }

        answer_recyclerview = (RecyclerView) findViewById(R.id.answer_recycler_view);
        answer_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this);
        linear_layout_manager.setStackFromEnd(false);
        answer_recyclerview.setLayoutManager(linear_layout_manager);
        answer_recyclerview.setAdapter(new AnswerRecyclerViewAdapter(answerPosts));

        user_answer_edittext = (EditText) findViewById(R.id.user_answer_edittext);
        send_answer_button = (ImageButton) findViewById(R.id.send_answer_button);
        send_answer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answer_text = user_answer_edittext.getText().toString();
                /*
                - Create new post with current user, and add it to QuestionPost.answers_list
                *** create seperate function for this ***
                 */
            }
        });

        view_added_image_selector = (TextView) findViewById(R.id.view_added_image_indicator);
        view_added_image_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri image_uri = Utils.getImageUri();
                Intent view_pic_intent = new Intent(context, ViewImageActivity.class);
                view_pic_intent.putExtra("ImageUri", image_uri);
                context.startActivity(view_pic_intent);

            }
        });

        get_image_button = (ImageButton) findViewById(R.id.get_image_button);
        get_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.popUpImageOptions(PostFocusActivity.this, getPackageManager());
            }
        });
    }


    // set animations for transition between HomeActivity and PostFocusActivity
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        super.startActivity(intent, options);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Utils.CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    view_added_image_selector.setVisibility(View.VISIBLE);
                }
                break;
            case Utils.PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    image_uri = data.getData();
                    view_added_image_selector.setVisibility(View.VISIBLE);
                }
                break;

        }
    }
}

