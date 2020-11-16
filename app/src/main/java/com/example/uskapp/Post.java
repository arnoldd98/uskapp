package com.example.uskapp;

import android.media.Image;

import java.util.ArrayList;

public abstract class Post implements Subject{
    private String userID;
    private String postID;
    private ArrayList<String> postImageIDs =new ArrayList<String>();
    private ArrayList<String> answerPostIDs = new ArrayList<String>(); // array lists need be created IDS added later on thru method
    public boolean toggle_anonymity;
    private String text;
    private int upvotes;
    private String timestamp;
    private String subject; // subject of post eg. 50.001


    public Post(String userID, String postID, String text, String timestamp, String subject,
                boolean toggle_anonymity) {
        this.userID = userID;
        this.postID=postID;
        this.toggle_anonymity = toggle_anonymity;
        this.text = text;
        this.upvotes = 0;
        this.timestamp = timestamp;
        this.subject = subject;
    }

    public String getUserID() {
        return userID;
    }

    public String getPostID() {
        return postID;
    }

    public ArrayList<String> getPostImageIDs() {
        return postImageIDs;
    }

    public void setPostImageIDs(ArrayList<String> postImageIDs) {
        this.postImageIDs = postImageIDs;
    }

    public ArrayList<String> getAnswerPostIDs() {
        return answerPostIDs;
    }

    public void setAnswerPostIDs(ArrayList<String> answerPostIDs) {
        this.answerPostIDs = answerPostIDs;
    }

    public boolean isToggle_anonymity() {
        return toggle_anonymity;
    }

    public void setToggle_anonymity(boolean toggle_anonymity) {
        this.toggle_anonymity = toggle_anonymity;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }


    public String getSubject() {
        return subject;
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
