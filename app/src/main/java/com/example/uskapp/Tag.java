package com.example.uskapp;

import androidx.annotation.Nullable;

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
}
