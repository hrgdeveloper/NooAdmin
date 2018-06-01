package com.developer.hrg.nooadmin.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.developer.hrg.nooadmin.Models.Admin;
import com.google.gson.Gson;

/**
 * Created by hamid on 5/25/2018.
 */

public class AdminInfo {

    // vase moshakhas karde inke aya user sms dade . ya logine va ...
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor ;

    public static final String ISLOGGEDIN= "isLoggedIn";
    public static final String ADMIN_JSON="admin_json";

    public AdminInfo(Context context) {
        this.context=context;
        sharedPreferences=context.getSharedPreferences("adminInfo",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
    }


    public void set_IsLogged_in(boolean situation) {
        editor.putBoolean(ISLOGGEDIN,situation);
        editor.apply();
    }
    public boolean get_IsLOGGEDIN() {
        return  sharedPreferences.getBoolean(ISLOGGEDIN,false);
    }

    public void setAdmin(Admin admin) {
        Gson gson = new Gson();
        String adminJson = gson.toJson(admin);
        editor.putString(ADMIN_JSON,adminJson);
        editor.commit();
    }

   public Admin getAdmin() {
       String admin_json =  sharedPreferences.getString(ADMIN_JSON,"noAdmin");
       Gson gson = new Gson();
       return  gson.fromJson(admin_json,Admin.class);
   }

}
