package com.example.uskapp;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Tag {
    private String tag_name;

    public Tag(String tag_name) {
        // make the first character upper case and the rest lower case
        tag_name = tag_name.substring(0, 1).toUpperCase() + tag_name.substring(1).toLowerCase();
        this.tag_name = tag_name;
    }

    public String getTagName() {
        return tag_name;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj.getClass() != Tag.class) return false;
        return ((Tag) obj).getTagName().equals(this.getTagName());
    }

    @Override
    public String toString() {
        return tag_name;
    }

    public static ArrayList<String> getTagStringList(ArrayList<Tag> tag_list) {
        ArrayList<String> string_list = new ArrayList<String>();
        for (Tag tag : tag_list) {
            string_list.add(tag.getTagName());
        }
        return string_list;
    }

    public static ArrayList<Tag> getTagList(ArrayList<String> string_list) {
        ArrayList<Tag> tag_list = new ArrayList<Tag>();
        for (String str : string_list) {
            tag_list.add(new Tag(str));
        }
        return tag_list;
    }
}
