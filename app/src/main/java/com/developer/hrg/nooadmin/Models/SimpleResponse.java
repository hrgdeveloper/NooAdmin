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
