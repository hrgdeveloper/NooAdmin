package com.developer.hrg.nooadmin.Helper;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

/**
 * Created by hamid on 5/30/2018.
 */

public class MySnack {
    public static void showSnack(CoordinatorLayout coordinatorLayout,String message) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout,message,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
