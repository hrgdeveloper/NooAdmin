package com.developer.hrg.nooadmin.Helper;

import android.content.Context;
import android.net.ConnectivityManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by hamid on 5/28/2018.
 */

public class InternetCheck {
private static boolean isOnline = true;
    public static boolean isOnline (Context context) {
        if (isConnected(context)) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Socket sock = new Socket();
                        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                        sock.close();
                        isOnline =true;
                    } catch (IOException e) { isOnline =false; } }

            }.start();
            return isOnline;
        }else {
            return false;
        }
    }

  public static boolean isConnected(Context context) {
      ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
      return cm.getActiveNetworkInfo() != null &&
              cm.getActiveNetworkInfo().isConnectedOrConnecting();
  }

//    @Override
//    protected Boolean doInBackground(Void... voids) {
//        try {
//        Socket sock = new Socket();
//        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
//        sock.close();
//        return true;
//    } catch (IOException e) { return false; } }


}