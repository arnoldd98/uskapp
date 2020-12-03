package com.example.uskapp;

import android.media.Image;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class Post implements Comparable<Post> {
    private String userID;
    private String postID;
    private ArrayList<String> postImageIDs = new ArrayList<>();
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

    // allows sorting of posts based on time submitted
    @Override
    public int compareTo(Post o) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));

        try {
            Date date1 = sdf.parse(this.timestamp);
            Date date2 = sdf.parse(o.timestamp);
            System.out.println("Date1: " + date1);
            System.out.println("Date2: " + date2);
            if (date1.after(date2)) return -1;
            else if (date1.equals(date2)) return 0;
            else if (date1.before(date2)) return 1;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
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

    public ArrayList<String> getPostImageIDs() {
        return postImageIDs;
    }

    public void setPostImageIDs(ArrayList<String> postImageIDs) {
        this.postImageIDs = postImageIDs;
    }

    public void addPostImageIDs(String id){
        this.postImageIDs.add(id);
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

    public void setUsersWhoUpVoted(ArrayList<String> usersWhoUpVoted) {
        this.usersWhoUpVoted = usersWhoUpVoted;
    }

    public ArrayList<String> getUsersWhoUpVoted() {
        return usersWhoUpVoted;
    }

    public void addUserUpvote(String userID){
        this.usersWhoUpVoted.add(userID);
    }
    public void removeUserUpvote(String userID) {
        this.usersWhoUpVoted.remove(userID);
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
