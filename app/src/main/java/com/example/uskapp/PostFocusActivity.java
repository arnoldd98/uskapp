package com.example.uskapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.ArrayList;

public class PostFocusActivity extends AppCompatActivity {
    RecyclerView answer_recyclerview;
    ArrayList<AnswerPost> answerPosts;
    ImageButton back_to_main_button;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_focus);

        context = this;

        back_to_main_button = findViewById(R.id.back_to_main_button);
        back_to_main_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        });


        // get answers to question in view from Firebase
        answerPosts = new ArrayList<>();
        User custom_user = new User("Jeb", "JJJ", "jjj");
        for (int i=0; i<5; i++) {
            answerPosts.add(new AnswerPost(custom_user, false, "This is a sample answer", null));
        }


        answer_recyclerview = findViewById(R.id.answer_recycler_view);
        answer_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this);
        linear_layout_manager.setStackFromEnd(false);
        answer_recyclerview.setLayoutManager(linear_layout_manager);
        answer_recyclerview.setAdapter(new AnswerRecyclerViewAdapter(answerPosts));
    }

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
}