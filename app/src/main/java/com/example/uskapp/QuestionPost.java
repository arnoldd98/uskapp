package com.example.uskapp;

import java.util.ArrayList;

public class QuestionPost extends Post{
    private ArrayList<String> answerPostIDs = new ArrayList<String>(); // array lists need be created IDS added later on thru method
    //private ArrayList<AnswerPost> answers_list;
    private ArrayList<Tag> tags_list;
    private ArrayList<String> post_followers = new ArrayList<>();

    public QuestionPost(String name, String userID, String postID, String text, String timestamp,
                        String subject, ArrayList<Tag> tags_list, boolean toggle_anonymity) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity);
        this.tags_list = tags_list;
    }

    public QuestionPost(String name, String userID, String postID, String text, String timestamp,
                        String subject, ArrayList<Tag> tags_list, boolean toggle_anonymity,int upvotes) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity,upvotes);
        //answers_list = new ArrayList<AnswerPost>();
        this.tags_list = tags_list;
    }


    public void addTag(Tag tag) {
        tags_list.add(tag);
    }
/*
    public ArrayList<AnswerPost> getAnswersList() {
        return answers_list;
    }
*/

    public ArrayList<String> getAnswerPostIDs() {
        return answerPostIDs;
    }

    public void setAnswerPostIDs(ArrayList<String> answerPostIDs) {
        this.answerPostIDs = answerPostIDs;
    }

    public void addAnswerPostID(String answerPostID){
        this.answerPostIDs.add(answerPostID);
    }

    public ArrayList<Tag> getTagsList() {
        return tags_list;
    }



}
