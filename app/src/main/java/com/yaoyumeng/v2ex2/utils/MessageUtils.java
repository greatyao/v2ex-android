package com.yaoyumeng.v2ex2.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.yaoyumeng.v2ex2.Application;

/**
 * Created by yw on 2015/5/5.
 */
public class MessageUtils {

    public static void showErrorMessage(Context cxt, String errorString) {
        Activity activity = (Activity) cxt;
        if (activity == null)
            Toast.makeText(Application.getContext(), errorString, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(cxt, errorString, Toast.LENGTH_LONG).show();
    }

    public static void showToast(Context cxt, String msg) {
        if(cxt == null)
            cxt = Application.getContext();
        Toast toast = Toast.makeText(cxt, msg, Toast.LENGTH_SHORT);
        toast.show();
    }
}
