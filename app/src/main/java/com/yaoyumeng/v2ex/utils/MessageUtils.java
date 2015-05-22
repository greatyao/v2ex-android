package com.yaoyumeng.v2ex.utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.yaoyumeng.v2ex.R;

/**
 * Created by yw on 2015/5/5.
 */
public class MessageUtils {

    public static void showErrorMessage(Context cxt, String errorString){
        Activity activity = (Activity)cxt;
        if(activity != null)
            new SnackBar.Builder(activity)
                    .withMessage(errorString)
                    .withActionMessageId(R.string.error_title)
                    .withStyle(SnackBar.Style.ALERT)
                    .withDuration(SnackBar.LONG_SNACK).show();
        else
            Toast.makeText(cxt, errorString, Toast.LENGTH_LONG).show();
    }

    public static void showMiddleToast(Context cxt, String msg) {
        Toast toast = Toast.makeText(cxt, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
