package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yaoyumeng.v2ex.model.NotificationModel;
import com.yaoyumeng.v2ex.ui.widget.NotificationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2015/4/28.
 */
public class NotificationAdapter extends BaseAdapter {

    Context mContext;
    List<NotificationModel> mNotifications = new ArrayList<NotificationModel>();

    public NotificationAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mNotifications.size();
    }

    @Override
    public Object getItem(int position) {
        return mNotifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = new NotificationView(mContext);

        ((NotificationView) convertView).parse(mNotifications.get(position));

        return convertView;
    }

    public void update(ArrayList<NotificationModel> data) {
        mNotifications = data;
        notifyDataSetChanged();
    }
}
