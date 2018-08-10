package com.developer.hrg.nooadmin.Models;


import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hamid on 5/25/2018.
 */

public class SimpleResponse {
    @SerializedName("error")
    boolean error ;
    @SerializedName("message")
    String message ;
    @SerializedName("admin")
    Admin admin;
    @SerializedName("user_list")
    ArrayList<User> user_list ;

    @SerializedName("comments")
    ArrayList<Comment> comments ;
    @SerializedName("pic_name")
    String  pic_name ;

    public ArrayList<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(ArrayList<Profile> profiles) {
        this.profiles = profiles;
    }

    @SerializedName("photos")
    ArrayList<Profile> profiles;


    public String getPic_name() {
        return pic_name;
    }

    public void setPic_name(String pic_name) {
        this.pic_name = pic_name;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Chanel> getChanels() {
        return chanels;
    }

    public void setChanels(ArrayList<Chanel> chanels) {
        this.chanels = chanels;
    }

    @SerializedName("chanels")
    ArrayList<Chanel> chanels ;


    public ArrayList<User> getUser_list() {
        return user_list;
    }

    public void setUser_list(ArrayList<User> user_list) {
        this.user_list = user_list;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SimpleResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
    }
}
