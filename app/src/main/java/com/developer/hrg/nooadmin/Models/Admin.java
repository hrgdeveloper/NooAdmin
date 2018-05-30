package com.developer.hrg.nooadmin.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamid on 5/30/2018.
 */

public class Admin {
    @SerializedName("admin_id")
    int admin_id ;
    @SerializedName("username")
    String username ;
    @SerializedName("apikey")
    String apikey ;

    public Admin(int admin_id, String username, String apikey) {
        this.admin_id = admin_id;
        this.username = username;
        this.apikey = apikey;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
}
