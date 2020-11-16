package com.example.uskapp;

import java.util.ArrayList;

public class AnswerPost extends Post {

    public AnswerPost(String userID, String postID, String text, String timestamp, String subject,
                      boolean toggle_anonymity) {
        super(userID, postID,text, timestamp, subject, toggle_anonymity);
    }


}
