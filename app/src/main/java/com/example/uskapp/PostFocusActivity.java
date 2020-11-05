package com.example.uskapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PostFocusActivity extends AppCompatActivity {
    RecyclerView answer_recyclerview;
    ArrayList<AnswerPost> answerPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_focus);
        answerPosts = new ArrayList<>();

        answer_recyclerview = findViewById(R.id.answer_recycler_view);
        answer_recyclerview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this);
        linear_layout_manager.setStackFromEnd(false);
        answer_recyclerview.setLayoutManager(linear_layout_manager);
        answer_recyclerview.setAdapter(new AnswerRecyclerViewAdapter(answerPosts));


    }
}