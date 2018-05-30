package com.developer.hrg.nooadmin.Models;


import com.google.gson.annotations.SerializedName;

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
