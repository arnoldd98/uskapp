//package com.example.uskapp;
//
//import android.os.Bundle;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class MainActivity extends AppCompatActivity {
//    RecyclerView main_recycler_view;
//    ArrayList<QuestionPost> posts_list;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        // get list of posts from Firebase
//        posts_list = new ArrayList<QuestionPost>();
//        QuestionPost customPost = new QuestionPost(new User("Gru", "Gru", "Gru"), false, "Hello World!", new ArrayList<byte[]>());
//        posts_list.add(customPost);
//
//        // set up main recycler view: linear layout manager to manage the order
//        main_recycler_view = findViewById(R.id.main_menu_recycler_view);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        main_recycler_view.setLayoutManager(linearLayoutManager);
//
//        // Add DividerItemDecoration to divide betewen cards in the RecyclerView
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(main_recycler_view.getContext(),
//                linearLayoutManager.getOrientation());
//        main_recycler_view.addItemDecoration(dividerItemDecoration);
//
//        // Set custom adapter to inflate the recycler view
//        MainRecyclerViewAdapter viewAdapter = new MainRecyclerViewAdapter(this, posts_list);
//        main_recycler_view.setAdapter(viewAdapter);
//
//
//    }
//
//}