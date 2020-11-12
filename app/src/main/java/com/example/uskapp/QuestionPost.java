package com.example.uskapp;

import java.util.ArrayList;

public class QuestionPost extends Post{
    private ArrayList<AnswerPost> answers_list;
    private ArrayList<Tag> tags_list;

    public QuestionPost(User user, boolean toggle_anonymity, String text, ArrayList<byte[]> images) {
        super(user, toggle_anonymity, text, images);
        answers_list = new ArrayList<AnswerPost>();
        tags_list = new ArrayList<Tag>();
    }

    public void addTag(Tag tag) {
        tags_list.add(tag);
    }

    public ArrayList<AnswerPost> getAnswersList() {
        return answers_list;
    }

    public ArrayList<Tag> getTagsList() {
        return tags_list;
    }
}
