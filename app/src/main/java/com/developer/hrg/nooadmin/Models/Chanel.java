package com.developer.hrg.nooadmin.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamid on 6/5/2018.
 */

public class Chanel implements Parcelable {

    @SerializedName("chanel_id")
    String chanel_id ;
    @SerializedName("name")
    String name ;
    @SerializedName("description")
    String description;
    @SerializedName("updated_at")
    String updated_at;
    @SerializedName("created_at")
    String created_at;
    @SerializedName("username")
    String username;
    @SerializedName("thumb")
    String thumb ;
    @SerializedName("last_message")
    String last_message ;
    @SerializedName("type")
    Integer type ;

    @SerializedName("cm_count")
    int cm_count ;

    public int getCm_count() {
        return cm_count;
    }

    public void setCm_count(int cm_count) {
        this.cm_count = cm_count;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

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



    public Chanel(String chanel_id, String name, String description, String thumb, String updated_at, String created_at, String username ,
                  String lase_message , Integer type) {
        this.chanel_id = chanel_id;
        this.name = name;
        this.description = description;
        this.thumb = thumb;
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.username = username;
        this.last_message=lase_message;
        this.type=type;
    }
    // in mothod vase ine ke ba tavajoh be meghdare type yek string ke message az che noie bar migardone

    public String getStringFromType() {
        String typee ;
        switch (type) {
            case 1 :
                typee= "متن";
                break;
            case 2  :
                typee=  "عکس" ;
                break;
            case 3 :
                typee=   "فیلم" ;
                break;
            case 4 :
                typee=  "فایل صوتی" ;
                break;
            case 5 :
                typee=  "تصویر متحرک" ;
                break;
            case 6 :
                typee=  "فایل" ;
                default:
                    typee = "فرمت ناشناس";

        }

        return typee ;

    }

    protected Chanel(Parcel in) {
        chanel_id = in.readString();
        name = in.readString();
        description = in.readString();
        updated_at = in.readString();
        created_at = in.readString();
        username = in.readString();
        thumb = in.readString();
        last_message = in.readString();
        type = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chanel_id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(updated_at);
        dest.writeString(created_at);
        dest.writeString(username);
        dest.writeString(thumb);
        dest.writeString(last_message);
        if (type == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(type);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Chanel> CREATOR = new Parcelable.Creator<Chanel>() {
        @Override
        public Chanel createFromParcel(Parcel in) {
            return new Chanel(in);
        }

        @Override
        public Chanel[] newArray(int size) {
            return new Chanel[size];
        }
    };
}
