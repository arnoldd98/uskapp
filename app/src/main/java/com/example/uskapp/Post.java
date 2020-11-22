package com.example.uskapp;

import android.media.Image;

import java.util.ArrayList;

public abstract class Post{
    private String userID;
    private String postID;
    private String postImageID;
    private ArrayList<String> usersWhoUpVoted = new ArrayList<String>(); // keeps track of the ids of user
    //prevents double voting
    private String name;
    public boolean toggle_anonymity;
    private String text;
    private int upvotes;
    private String timestamp;
    private String subject; // subject of post eg. 50.001


    public Post(String name,String userID, String postID, String text, String timestamp, String subject,
                     boolean toggle_anonymity) {
        this.name = name;
        this.userID = userID;
        this.postID=postID;
        this.toggle_anonymity = toggle_anonymity;
        this.text = text;
        this.upvotes = 0;
        this.timestamp = timestamp;
        this.subject = subject;
    }

    public Post(String name,String userID, String postID, String text, String timestamp, String subject,
                boolean toggle_anonymity, int upvotes) {
        this.name = name;
        this.userID = userID;
        this.postID=postID;
        this.toggle_anonymity = toggle_anonymity;
        this.text = text;
        this.upvotes = 0;
        this.timestamp = timestamp;
        this.subject = subject;
        this.upvotes = upvotes;
    }

    public String getName() {
        return name;
    }

    public String getUserID() {
        return userID;
    }

    public String getPostID() {
        return postID;
    }

    public String getPostImageID() {
        return postImageID;
    }

    public void setPostImageID(String postImageID) {
        this.postImageID = postImageID;
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

    public ArrayList<String> getUsersWhoUpVoted() {
        return usersWhoUpVoted;
    }

    public void addUserUpvote(String userID){
        this.usersWhoUpVoted.add(userID);
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

    public void increaseUpVote(){
        this.upvotes+=1;
    }

    public String getTimestamp() {
        return timestamp;
    }

}
