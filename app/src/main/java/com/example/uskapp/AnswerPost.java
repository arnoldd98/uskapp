package com.example.uskapp;

import java.util.ArrayList;

public class AnswerPost extends Post {
    public AnswerPost(User user, boolean toggle_anonymity, String text, ArrayList<byte[]> images) {
        super(user, toggle_anonymity, text, images);
    }
}
