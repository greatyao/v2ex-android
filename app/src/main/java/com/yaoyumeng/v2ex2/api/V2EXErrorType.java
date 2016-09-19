package com.yaoyumeng.v2ex2.api;

import android.content.Context;

import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.utils.NetWorkHelper;

/**
 * Created by yw on 2015/5/22.
 */
public enum V2EXErrorType {
    ErrorSuccess,
    ErrorApiForbidden,
    ErrorNoOnceAndNext,
    ErrorLoginFailure,
    ErrorCommentFailure,
    ErrorGetTopicListFailure,
    ErrorGetTopicDetailsFailure,
    ErrorGetNotificationFailure,
    ErrorCreateNewFailure,
    ErrorFavNodeFailure,
    ErrorCheckInFailure,
    ErrorFavTopicFailure,
    ErrorGetProfileFailure;

    public static String errorMessage(Context cxt, V2EXErrorType type) {
        if(cxt == null)
            cxt = Application.getContext();
        boolean isNetAvailable = NetWorkHelper.isNetAvailable(cxt);
        if (!isNetAvailable)
            return cxt.getResources().getString(R.string.error_network_disconnect);

        switch (type) {
            case ErrorApiForbidden:
                return cxt.getResources().getString(R.string.error_network_exception);

            case ErrorNoOnceAndNext:
                return cxt.getResources().getString(R.string.error_obtain_once);

            case ErrorLoginFailure:
                return cxt.getResources().getString(R.string.error_login);

            case ErrorCommentFailure:
                return cxt.getResources().getString(R.string.error_reply);

            case ErrorGetNotificationFailure:
                return cxt.getResources().getString(R.string.error_get_notification);

            case ErrorCreateNewFailure:
                return cxt.getResources().getString(R.string.error_create_topic);

            case ErrorFavNodeFailure:
                return cxt.getResources().getString(R.string.error_fav_nodes);


            case ErrorGetTopicListFailure:
                return  cxt.getResources().getString(R.string.error_get_topic_list);

            case ErrorGetTopicDetailsFailure:
                return  cxt.getResources().getString(R.string.error_get_topic_details);

            case ErrorFavTopicFailure:
                return cxt.getResources().getString(R.string.error_fav_topic);

            case ErrorGetProfileFailure:
                return cxt.getResources().getString(R.string.error_get_profile);

            case ErrorCheckInFailure:
                return cxt.getResources().getString(R.string.error_check_in);

            default:
                return cxt.getResources().getString(R.string.error_unknown);
        }

    }
}
