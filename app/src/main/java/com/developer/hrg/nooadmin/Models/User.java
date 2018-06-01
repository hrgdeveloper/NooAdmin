package com.developer.hrg.nooadmin.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamid on 6/1/2018.
 */

public class User {
    @SerializedName("id")
    int id;
    @SerializedName("mobile")
    String mobile;
    @SerializedName("apikey")
    String apikey;
    @SerializedName("created_at")
    String created_at;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    @SerializedName("status")

    int status ;
    @SerializedName("active")
    int active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public User(int id, String mobile, String apikey, String created_at, int status, int active) {
        this.id = id;
        this.mobile = mobile;
        this.apikey = apikey;
        this.created_at = created_at;
        this.status = status;
        this.active = active;
    }
}