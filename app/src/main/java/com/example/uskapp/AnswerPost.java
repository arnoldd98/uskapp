package com.example.uskapp;

import java.util.ArrayList;

public class AnswerPost extends Post {

    public AnswerPost(String name, String userID, String postID, String text, String timestamp, String subject,
                      boolean toggle_anonymity) {
        super(name,userID, postID,text, timestamp, subject, toggle_anonymity);
    }


}
