package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SubjectActivity extends AppCompatActivity {


    RecyclerView recycler;
    Adapter adapter;
    ArrayList<String> subjectTitle;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

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



            //connect bottons with their ids
        recycler = findViewById(R.id.recyler1);

        subjectTitle = new ArrayList<String>();

        //creating recylcerview adapter
        adapter = new Adapter(this, subjectTitle);

        //setting the adapter
        recycler.setAdapter(adapter);

        //setting the layout manager for the recyclerview
        recycler.setLayoutManager(new GridLayoutManager(this,2));

    }
}