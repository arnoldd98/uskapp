package com.example.uskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ActionMode;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Random;

public class SubjectActivity extends AppCompatActivity {


    RecyclerView recycler;
    SubjectAdapter adapter;
    ArrayList<String> subjectTitle;
    //ArrayList<Bitmap> subjectBitmaps= new ArrayList<Bitmap>();
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


                        //StorageReference imageRef = FirebaseStorage.getInstance().getReference("SubjectPictures")
                        //        .child(subject+".jpg");
                        //imageRef.getBytes(2048*2048)
                        //        .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        //                                  @Override
                        //                                  public void onSuccess(byte[] bytes) {
                        //                                      Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        //                                      subjectBitmaps.add(bitmap);
                        //                                      adapter.notifyDataSetChanged();
                        //                                  }
                        //                              }
                        //        ).addOnFailureListener(new OnFailureListener() {
                        //    @Override
                        //    public void onFailure(@NonNull Exception e) {
                        //        subjectBitmaps.add(null);
                        //    }
                        //});
                    }
                    //adapter.notifyDataSetChanged();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SubjectActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });



        //connect bottons with their ids
        recycler = findViewById(R.id.subjectRecyler);

        subjectTitle = new ArrayList<String>();

        int[] androidcolors = getResources().getIntArray(R.array.androidcolors);

        //creating recylcerview adapter
        adapter = new SubjectAdapter(this, subjectTitle, androidcolors);

        //setting the adapter
        recycler.setAdapter(adapter);

        //setting the layout manager for the recyclerview
        recycler.setLayoutManager(new GridLayoutManager(this,2));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);
    }
}