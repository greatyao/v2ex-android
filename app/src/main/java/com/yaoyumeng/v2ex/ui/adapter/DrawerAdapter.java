package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaoyumeng.v2ex.R;

/**
 * Created by yugy on 14-3-15.
 */
public class DrawerAdapter extends BaseAdapter {

    private Context mContext;
    private String[] mTitles;

    public DrawerAdapter(Context context) {
        mContext = context;
        mTitles = new String[]{
                context.getResources().getString(R.string.title_drawer_latest),
                context.getResources().getString(R.string.title_drawer_hot),
                context.getResources().getString(R.string.title_drawer_all_nodes),
                context.getResources().getString(R.string.title_drawer_fav_nodes),
                context.getResources().getString(R.string.title_drawer_notification),
                context.getResources().getString(R.string.title_drawer_settings)
        };
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public String getItem(int position) {
        return mTitles[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getIconId(int position) {
        switch (position) {
            case 0:
                return R.drawable.ic_news;
            case 1:
                return R.drawable.ic_hot;
            case 2:
                return R.drawable.ic_nodes;
            case 3:
                return R.drawable.ic_favourite;
            case 4:
                return R.drawable.ic_notify;
            case 5:
                return R.drawable.ic_settings;
            default:
                return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView item = (TextView) convertView;
        if (item == null) {
            item = (TextView) LayoutInflater.from(mContext).inflate(R.layout.view_drawer_item, null);
        }
        item.setText(getItem(position));
        item.setCompoundDrawablesWithIntrinsicBounds(getIconId(position), 0, 0, 0);
        return item;
    }
}
