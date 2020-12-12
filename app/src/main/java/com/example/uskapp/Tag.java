package com.example.uskapp;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class Tag {
    private String tag_name;

    public Tag(String name) {
        // remove all punctuation except hyphen '-'
        name = name.replaceAll("[^\\P{P}-]+", "");
        // make the first character upper case and the rest lower case
        if (name.contains("-")) {
            name = name.replaceAll("-", " ");
        }
        name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        name = name.trim();
        this.tag_name = name;
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
