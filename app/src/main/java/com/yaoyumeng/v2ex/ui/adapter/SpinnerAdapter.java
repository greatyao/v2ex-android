package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yaoyumeng.v2ex.R;

/**
 * Created by yw on 2015/5/21.
 */
public class SpinnerAdapter extends BaseAdapter {

    private String[] mTabList;
    Context mContext;

    public SpinnerAdapter(Context cxt, String[] titles) {
        this.mContext = cxt;
        this.mTabList = titles;
    }

    int checkPos = 0;

    public void setCheckPos(int pos) {
        checkPos = pos;
    }

    @Override
    public int getCount() {
        return mTabList.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_layout_head, parent, false);
        }

        ((TextView) convertView).setText(mTabList[position]);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_layout_item, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(mTabList[position]);

        if (checkPos == position) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.divide_15_e5));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        return convertView;
    }
}