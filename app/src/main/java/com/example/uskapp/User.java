package com.example.uskapp;


import java.util.ArrayList;

public class User implements Observer {
    public String name;
    public String email;
    //private String userId;
    private int rank;
    private int exp;
    private int total_posts;
    private int karma;
    private int total_answers;
    private ArrayList<String> postFollowing = new ArrayList<String>();
    private ArrayList<String> postPosted = new ArrayList<String>();
    private ArrayList<String> subjectsEnrolled = new ArrayList<String>();

    public User() {
    }

    public User(String name, String email){
        this.name = name;
        this.email = email;
        this.rank=0;
        this.exp=0;
        this.total_posts=0;
        this.karma=0;
        this.total_answers=0;
        //this.userId=userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getTotal_posts() {
        return total_posts;
    }

    public void setTotal_posts(int total_posts) {
        this.total_posts = total_posts;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getTotal_answers() {
        return total_answers;
    }

    public void setTotal_answers(int total_answers) {
        this.total_answers = total_answers;
    }

    public ArrayList<String> getPostFollowing() {
        return postFollowing;
    }

    public void setPostFollowing(ArrayList<String> postFollowing) {
        this.postFollowing = postFollowing;
    }

    public ArrayList<String> getPostPosted() {
        return postPosted;
    }

    public void setPostPosted(ArrayList<String> postPosted) {
        this.postPosted = postPosted;
    }

    public ArrayList<String> getSubjectsEnrolled() {
        return subjectsEnrolled;
    }

    public void setSubjectsEnrolled(ArrayList<String> subjectsEnrolled) {
        this.subjectsEnrolled = subjectsEnrolled;
    }

    public void addSubjectsEnrolled(String subjectName){
        this.subjectsEnrolled.add(subjectName);
    }

    public void addPostPosted(String newPostID){
        this.postPosted.add(newPostID);
    }
    public void addPostFollowing(String newPostID){
        this.postFollowing.add(newPostID);
    }
    @Override
    public void update(String updates) {
        // what to update whenever the post u are following updates
    }
}
