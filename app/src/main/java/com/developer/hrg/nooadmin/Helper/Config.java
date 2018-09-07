package com.developer.hrg.nooadmin.Helper;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/**
 * Created by hamid on 6/5/2018.
 */

public class Config {
    public static final  String OFFLINE_URL = "http://192.168.1.147/noor/v1/";
    public static final  String ONLINE_URL = "http://keepwords.ir/noor/v1/";
    public static final  String ONLINE_URL_FINAL = "http://nooresalehin.ir/v1/";


    public static final String CHANEL_THUMB_BASE_OFFILNE = "http://192.168.1.147/noor/uploads/chanel_thumb/";
    public static final String CHANEL_THUMB_BASE_ONLLINE = "http://keepwords.ir/noor/uploads/chanel_thumb/";
    public static final String CHANEL_THUMB_BASE_ONLLINE_FINAL = "http://nooresalehin.ir/uploads/chanel_thumb/";



    public static final String CHANEL_PIC_BASE_OFFILNE = "http://192.168.1.147/noor/uploads/chanel_pics/";
    public static final String CHANEL_PIC_BASE_ONLLINE = "http://keepwords.ir/noor/uploads/chanel_pics/";
    public static final String CHANEL_PIC_BASE_ONLLINE_FINAL = "http://nooresalehin.ir/uploads/chanel_pics/";


    public static final String MESSAGE_MESSAGE = "message";

    public static final String MESSAGE_TYPE= "type";
    public static final String MESSAGE_ADMINID= "admin_id";

    public static final String PROFILE_PIC_THUMB_ADDRESS  = "http://192.168.1.147/noor/uploads/user_profile/thumb/";
    public static final String PROFILE_PIC_THUMB_ADDRESS_ONLINE  = "http://keepwords.ir/noor/uploads/user_profile/thumb/";
    public static final String PROFILE_PIC_THUMB_ADDRESS_ONLINE_FINAL  = "http://nooresalehin.ir/uploads/user_profile/thumb/";


    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



}
