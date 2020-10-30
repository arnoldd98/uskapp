package com.example.uskapp;

import android.media.Image;

import java.util.ArrayList;

public abstract class Post {
    public User user;
    public boolean toggle_anonymity;
    public String text;
    public ArrayList<byte[]> images;
    public int upvotes;
    public String timestamp;

    public Post(User user, boolean toggle_anonymity, String text, ArrayList<byte[]> images) {
        this.user = user;
        this.toggle_anonymity = toggle_anonymity;
        this.text = text;
        this.images = images;
    }
}
