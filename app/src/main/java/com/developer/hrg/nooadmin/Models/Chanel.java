package com.developer.hrg.nooadmin.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamid on 6/5/2018.
 */

public class Chanel {

    @SerializedName("chanel_id")
    String chanel_id ;
    @SerializedName("name")
    String name ;
    @SerializedName("description")
    String description;

    public String getChanel_id() {
        return chanel_id;
    }

    public void setChanel_id(String chanel_id) {
        this.chanel_id = chanel_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @SerializedName("thumb")

    String thumb ;

    public Chanel(String chanel_id, String name, String description, String thumb, String updated_at, String created_at, String username) {
        this.chanel_id = chanel_id;
        this.name = name;
        this.description = description;
        this.thumb = thumb;
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.username = username;
    }

    @SerializedName("updated_at")

    String updated_at;
    @SerializedName("created_at")
    String created_at;
    @SerializedName("username")
    String username;
}
