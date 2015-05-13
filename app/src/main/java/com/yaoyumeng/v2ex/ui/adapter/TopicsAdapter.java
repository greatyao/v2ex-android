package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.widget.TopicView;

import java.util.ArrayList;

/**
 * Created by yw on 2015/4/28.
 */
public class TopicsAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<TopicModel> mTopics = new ArrayList<TopicModel>();

    public TopicsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mTopics.size();
    }

    @Override
    public Object getItem(int position) {
        return mTopics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TopicModel topic = mTopics.get(position);
        if (convertView == null)
            convertView = new TopicView(mContext);

        ((TopicView) convertView).parse(topic);

        return convertView;
    }

    public void update(ArrayList<TopicModel> data, boolean merge) {
        if (merge && mTopics.size() > 0) {
            for (int i = 0; i < mTopics.size(); i++) {
                TopicModel obj = mTopics.get(i);
                boolean exist = false;
                for (int j = 0; j < data.size(); j++) {
                    if (data.get(j).id == obj.id) {
                        exist = true;
                        break;
                    }
                }
                if (exist) continue;
                data.add(obj);
            }
        }
        mTopics = data;

        notifyDataSetChanged();
    }
}