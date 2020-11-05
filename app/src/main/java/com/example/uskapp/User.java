package com.example.uskapp;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable {
    private String name;
    private String password;
    private String email;
    private int rank;
    private int exp;
    private int total_posts;
    private int karma;
    private int total_answers;
    private int userId;
    private byte[] profilePic;
    static int count;

    public User(String name,String email,String password){
        this.name = name;
        this.email = email;
        this.password = password;
        this.rank=0;
        this.exp=0;
        this.total_posts=0;
        this.karma=0;
        this.total_answers=0;
        this.userId=count;
        this.count+=1;
        this.profilePic=null;
    }
    public User(String name,String email,String password,byte[] profilePic){
        this.name = name;
        this.email = email;
        this.password = password;
        this.rank=0;
        this.exp=0;
        this.total_posts=0;
        this.karma=0;
        this.total_answers=0;
        this.profilePic=profilePic;
        this.userId=count;
        this.count+=1;
    }

    protected User(Parcel in) {
        name = in.readString();
        password = in.readString();
        email = in.readString();
        rank = in.readInt();
        exp = in.readInt();
        total_posts = in.readInt();
        karma = in.readInt();
        total_answers = in.readInt();
        userId = in.readInt();
        profilePic = in.createByteArray();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(password);
        parcel.writeString(email);
        parcel.writeInt(rank);
        parcel.writeInt(exp);
        parcel.writeInt(total_posts);
        parcel.writeInt(karma);
        parcel.writeInt(total_answers);
        parcel.writeInt(userId);
        parcel.writeByteArray(profilePic);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getTotal_posts() {
        return total_posts;
    }

    public void setTotal_posts(int total_posts) {
        this.total_posts = total_posts;
    }

    public int getKarma() {
        return karma;
    }

    public void setKarma(int karma) {
        this.karma = karma;
    }

    public int getTotal_answers() {
        return total_answers;
    }

    public void setTotal_answers(int total_answers) {
        this.total_answers = total_answers;
    }


}
