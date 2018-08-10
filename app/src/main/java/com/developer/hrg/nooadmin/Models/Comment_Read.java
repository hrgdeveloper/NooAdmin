package com.developer.hrg.nooadmin.Models;

/**
 * Created by hamid on 8/9/2018.
 */

public class Comment_Read {
    int chanel_id ;
    int read_cout ;

    public Comment_Read(int chanel_id, int read_cout) {
        this.chanel_id = chanel_id;
        this.read_cout = read_cout;
    }

    public int getChanel_id() {
        return chanel_id;
    }

    public void setChanel_id(int chanel_id) {
        this.chanel_id = chanel_id;
    }

    public int getRead_cout() {
        return read_cout;
    }

    public void setRead_cout(int read_cout) {
        this.read_cout = read_cout;
    }
}
