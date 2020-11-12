package com.example.uskapp;

import android.media.Image;

import java.util.ArrayList;

public abstract class Post {
    private User user;
    public boolean toggle_anonymity;
    private String text;
    private ArrayList<byte[]> images;
    private int upvotes;
    private String timestamp;

    public Post(User user, boolean toggle_anonymity, String text, ArrayList<byte[]> images) {
        this.user = user;
        this.toggle_anonymity = toggle_anonymity;
        this.text = text;
        this.images = images;
    }

    public ArrayList<byte[]> getImages() {
        return images;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
