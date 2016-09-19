package com.yaoyumeng.v2ex2.model;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yw on 2015/5/27.
 */
public class V2EXDateModel {

    public static long toLong(String dateString) {
        String[] stringArray = dateString.split(" ");
        long created = System.currentTimeMillis() / 1000;
        int how = 0;
        try {
            how = Integer.parseInt(stringArray[0]);
        } catch (Exception e) {

        }
        String subString = stringArray[1].substring(0, 1);
        if (subString.equals("分")) {
            created -= 60 * how;
        } else if (subString.equals("小")) {
            created -= 3600 * how;
        } else if (subString.equals("天")) {
            created -= 24 * 3600 * how;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date date = sdf.parse(dateString);
                created = date.getTime() / 1000;
            } catch (Exception e) {
            }
        }

        return created;
    }

    public static String toString(long ts) {
        if(ts == -1) return "";
        long created = ts * 1000;
        long now = System.currentTimeMillis();
        long difference = now - created;
        CharSequence text = (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                "刚刚" :
                DateUtils.getRelativeTimeSpanString(
                        created,
                        now,
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
        return text.toString();
    }
}
