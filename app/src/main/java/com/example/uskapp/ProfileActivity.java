package com.example.uskapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {
    User myUser;
    ImageView profilePicIV;
    TextView nameView,rankView,karmaView;
    ProgressBar expBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Bundle bundle = getIntent().getExtras();
        myUser =bundle.getParcelable("user");

        profilePicIV =findViewById(R.id.imageView);
        nameView=findViewById(R.id.nameTv);
        rankView=findViewById(R.id.rankTv);
        karmaView = findViewById(R.id.karmaTv);
        expBar = findViewById(R.id.expProgressBar);

        if(myUser.getProfilePic()==null){
            profilePicIV.setImageResource(R.drawable.ic_launcher_foreground);
        } else {
            byte[] byteArray = myUser.getProfilePic();

            Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            RoundedBitmapDrawable d = RoundedBitmapDrawableFactory.create(getResources(), bmp);
            d.setCircular(true);
            profilePicIV.setImageDrawable(d);
        }
        nameView.setText(myUser.getName());
        rankView.setText(String.valueOf(myUser.getRank()));
        karmaView.setText(String.valueOf(myUser.getKarma()));

        expBar.setMax(100);
        expBar.setProgress(myUser.getExp());
        expBar.setProgress(50);
    }
}