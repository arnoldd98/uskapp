package com.example.uskapp;

import java.util.ArrayList;

public class QuestionPost extends Post{
    private ArrayList<String> answerPostIDs = new ArrayList<String>(); // array lists need be created IDS added later on thru method
    //private ArrayList<AnswerPost> answers_list;
    private ArrayList<String> tags_string_list;
    private ArrayList<String> post_followers = new ArrayList<>();

    public QuestionPost(String name, String userID, String postID, String text, String timestamp,
                        String subject, ArrayList<String> tags_list, boolean toggle_anonymity) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity);
        this.tags_string_list = tags_list;
    }

    public QuestionPost(String name, String userID, String postID, String text, String timestamp,
                        String subject, ArrayList<String> tags_list, boolean toggle_anonymity,int upvotes) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity,upvotes);
        //answers_list = new ArrayList<AnswerPost>();
        this.tags_string_list = tags_list;
    }


    public void addTag(String string) {
        tags_string_list.add(string);
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
        return Tag.getTagList(tags_string_list);
    }

    public ArrayList<String> getTagsStringList() {
        return tags_string_list;
    }



}
