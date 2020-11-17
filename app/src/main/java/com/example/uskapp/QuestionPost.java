package com.example.uskapp;

import java.util.ArrayList;

public class QuestionPost extends Post implements Subject{
    private ArrayList<String> answerPostIDs = new ArrayList<String>(); // array lists need be created IDS added later on thru method
    //private ArrayList<AnswerPost> answers_list;
    private ArrayList<Tag> tags_list;
    ArrayList<Observer> followersOfPost = new ArrayList<>();
    public QuestionPost(String userID, String postID, String text, String timestamp, String subject,
                        boolean toggle_anonymity) {
        super(userID, postID,text, timestamp, subject, toggle_anonymity);
        //answers_list = new ArrayList<AnswerPost>();
        tags_list = new ArrayList<Tag>();
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


    @Override
    public void register(Observer o) {
        followersOfPost.add(o);
    }

    @Override
    public void unregister(Observer o) {
        followersOfPost.remove(o);
    }

    @Override
    public void notifyObservers() {
        for (Observer o : followersOfPost){
            o.update("updates blah blah");
        }
    }
}
