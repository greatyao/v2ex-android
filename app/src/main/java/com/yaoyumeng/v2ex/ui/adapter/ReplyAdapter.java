package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yaoyumeng.v2ex.model.ReplyModel;
import com.yaoyumeng.v2ex.ui.widget.ReplyView;
import com.yaoyumeng.v2ex.utils.AccountUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2015/4/28.
 */
public class ReplyAdapter extends BaseAdapter {

    Context mContext;
    List<ReplyModel> mReplies = new ArrayList<ReplyModel>();
    boolean mLogin;
    OnItemCommentClickListener mListener;

    public ReplyAdapter(Context context, OnItemCommentClickListener listener) {
        mContext = context;
        mListener = listener;
        mLogin = AccountUtils.isLogined(mContext);
    }

    @Override
    public int getCount() {
        return mReplies.size();
    }

    @Override
    public Object getItem(int position) {
        return mReplies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = new ReplyView(mContext);

        ((ReplyView) convertView).parse(mReplies.get(position), mLogin, mListener);

        return convertView;
    }

    public void update(ArrayList<ReplyModel> data) {
        mReplies = data;
        notifyDataSetChanged();
    }

    public void insert(ReplyModel reply) {
        mReplies.add(reply);
        notifyDataSetChanged();
    }

    static public interface OnItemCommentClickListener {
        abstract void onItemCommentClick(ReplyModel replyObj);
    }
}
