package com.example.uskapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity {


    RecyclerView recycler;
    Adapter adapter;
    ArrayList<String> subjectTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        //connect bottons with their ids
        recycler = findViewById(R.id.recyler1);

        subjectTitle = new ArrayList<String>();
        subjectTitle.add("50.001");
        subjectTitle.add("50.002");
        subjectTitle.add("50.004");
        subjectTitle.add("02.110");
        subjectTitle.add("01.011");
        //creating recylcerview adapter
        adapter = new Adapter(this, subjectTitle);

        //setting the adapter
        recycler.setAdapter(adapter);

        //setting the layout manager for the recyclerview
        recycler.setLayoutManager(new GridLayoutManager(this,2));

    }
}