package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity {


    RecyclerView recycler;
    SubjectAdapter adapter;
    ArrayList<String> subjectTitle;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);
        subjectTitle = new ArrayList<String>();

        //gets information from firebase and displays it
        mDatabase = FirebaseDatabase.getInstance().getReference("Subject");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (subjectTitle != null) {
                    subjectTitle.clear();
                }
                if (snapshot.exists()) {
                    for (DataSnapshot s : snapshot.getChildren()) {
                        String subject = s.getValue(String.class);
                        subjectTitle.add(subject);
                    }
                }
                System.out.println(subjectTitle);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubjectActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        subjectTitle.add("a");
//        subjectTitle.add("b");
        System.out.println("hahah:" + subjectTitle);



        //connect bottons with their ids
        recycler = findViewById(R.id.subjectRecyler);

        //creating recylcerview adapter
        adapter = new SubjectAdapter(this, subjectTitle);

        //setting the adapter
        recycler.setAdapter(adapter);

        //setting the layout manager for the recyclerview
        recycler.setLayoutManager(new GridLayoutManager(this,2));

    }
}