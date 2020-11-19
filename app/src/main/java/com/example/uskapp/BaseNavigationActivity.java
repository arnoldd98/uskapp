package com.example.uskapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseNavigationActivity extends AppCompatActivity {
    protected BottomNavigationView bottom_navigation_view;
    private Activity activity = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getCurrentContentViewId());
        bottom_navigation_view = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        bottom_navigation_view.setOnNavigationItemSelectedListener(new Listener());
    }

    @Override
    protected void onStart() {
        super.onStart();
        MenuItem item = bottom_navigation_view.getMenu().findItem(getCurrentNavMenuId());
        item.setChecked(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    protected abstract int getCurrentNavMenuId();

    protected abstract int getCurrentContentViewId();

    private class Listener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_home_screen:
                    Intent home_intent = new Intent(activity, HomeActivity.class);
                    System.out.println("Start home intent");
                    activity.startActivity(home_intent);
                    break;
                case R.id.nav_add_post:
                    Intent add_post_intent = new Intent(activity, NewPostActivity.class);
                    System.out.println("Start add post intent");
                    activity.startActivity(add_post_intent);
                    break;
                case R.id.nav_profile:
                    Intent profile_intent = new Intent(activity, ProfileActivity.class);
                    System.out.println("Start profile intent");
                    activity.startActivity(profile_intent);
                    break;
            }
            return true;
        }
    }
}
