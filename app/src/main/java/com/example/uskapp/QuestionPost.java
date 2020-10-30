package com.example.uskapp;

import java.util.ArrayList;

public class QuestionPost extends Post{
    public ArrayList<AnswerPost> answers_list;
    public ArrayList<Tag> tags_list;

    public QuestionPost(User user, boolean toggle_anonymity, String text, ArrayList<byte[]> images) {
        super(user, toggle_anonymity, text, images);
    }
}
