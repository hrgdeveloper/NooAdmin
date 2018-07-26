package com.developer.hrg.nooadmin.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamid on 7/26/2018.
 */

public class Profile {
    @SerializedName("chanel_photo_id")
    int photo_id ;
    @SerializedName("chanel_id")
    int chanel_id ;
    @SerializedName("photo")
    String photo ;

    public Profile(int photo_id, int chanel_id, String photo) {
        this.photo_id = photo_id;
        this.chanel_id = chanel_id;
        this.photo = photo;
    }

    public int getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(int photo_id) {
        this.photo_id = photo_id;
    }

    public int getChanel_id() {
        return chanel_id;
    }

    public void setChanel_id(int chanel_id) {
        this.chanel_id = chanel_id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
