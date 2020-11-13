package com.example.uskapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class PostFocusActivity extends AppCompatActivity {
    RecyclerView answer_recyclerview;
    static ArrayList<AnswerPost> answerPosts;
    ImageButton back_to_main_button;

    EditText user_answer_edittext;
    ImageButton send_answer_button;
    ImageButton get_image_button;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_focus);
        context = this;

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

        get_image_button = (ImageButton) findViewById(R.id.get_image_button);
        get_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpImageOptions(context);
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

    public void popUpImageOptions(Context context) {
        final Dialog bottomDialogue = new Dialog(context, R.style.ImageDialogSheet);
        bottomDialogue.setContentView(R.layout.choose_image_options_view);
        bottomDialogue.setCancelable(true);
        bottomDialogue.show();

    }
}