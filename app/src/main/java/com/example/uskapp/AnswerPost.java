package com.example.uskapp;

public class AnswerPost extends Post {
    String picId;
    public AnswerPost(String name, String userID, String postID, String text, String timestamp, String subject,
                      boolean toggle_anonymity) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity);
    }

    public AnswerPost(String name, String userID, String postID, String text, String timestamp, String subject,
                      boolean toggle_anonymity,int upvotes) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity, upvotes);
    }

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }
}
