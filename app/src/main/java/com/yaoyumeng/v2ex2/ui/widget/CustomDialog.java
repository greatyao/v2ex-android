package com.yaoyumeng.v2ex2.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.yaoyumeng.v2ex2.R;

/**
 * Created by chaochen on 14-11-10.
 */
public final class CustomDialog {

    private static void dialogTitleLineColor(Context context, Dialog dialog, int color) {
        String dividers[] = {
                "android:id/titleDividerTop", "android:id/titleDivider"
        };

        for (String divider : dividers) {
            int dividerId = context.getResources().getIdentifier(divider, null, null);
            View dividerView = dialog.findViewById(dividerId);
            if (dividerView != null) {
                dividerView.setBackgroundColor(color);
            }
        }
    }

    public static void dialogTitleLineColor(Context context, Dialog dialog) {
        if (dialog != null) {
            dialogTitleLineColor(context, dialog, context.getResources().getColor(R.color.colorPrimary));
        }
    }
}
