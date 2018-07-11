package com.developer.hrg.nooadmin.Models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamid on 7/7/2018.
 */

public class Comment {
    @SerializedName("text")
    String text ;
    @SerializedName("created_at")
    String created_at ;
    @SerializedName("username")
    String username ;
    @SerializedName("pic_thumb")
    String pic_thumb ;
    @SerializedName("comment_id")
    int  comment_id ;

    @SerializedName("visible")
    int visible;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getPic_thumb() {
        return pic_thumb;
    }

    public void setPic_thumb(String pic_thumb) {
        this.pic_thumb = pic_thumb;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public Comment(String text, String created_at, String username, String pic_thumb, int comment_id, int visible) {
        this.text = text;
        this.created_at = created_at;
        this.username = username;
        this.pic_thumb = pic_thumb;
        this.comment_id = comment_id;
        this.visible = visible;
    }
}
