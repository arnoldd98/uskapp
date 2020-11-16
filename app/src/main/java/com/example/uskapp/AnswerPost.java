package com.example.uskapp;

import java.util.ArrayList;

public class AnswerPost extends Post {
    ArrayList<Observer> followersOfPost = new ArrayList<>();
    public AnswerPost(String userID, String postID, String text, String timestamp, String subject,
                      boolean toggle_anonymity) {
        super(userID, postID,text, timestamp, subject, toggle_anonymity);
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
