package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.ui.widget.NodeView;
import com.yaoyumeng.v2ex.utils.PinyinAlpha;
import com.yaoyumeng.v2ex.utils.PinyinComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by yw on 2015/4/28.
 */
public class AllNodesAdapter extends BaseAdapter {
    Context mContext;
    List<NodeModel> mNodes = new ArrayList<NodeModel>();
    HashMap<String, Integer> mAlphaPosition = new HashMap<String, Integer>();

    public AllNodesAdapter(Context context) {
        mContext = context;
    }

    public HashMap<String, Integer> getAlphaPosition() {
        return mAlphaPosition;
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = new NodeView(mContext);

        ((NodeView) convertView).parse(mNodes.get(position));

        return convertView;
    }

    public void update(ArrayList<NodeModel> data) {
        TreeMap<String, List<NodeModel>> lists = new TreeMap<String, List<NodeModel>>();
        for (int i = 0; i < data.size(); i++) {
            NodeModel node = data.get(i);
            String alpha = PinyinAlpha.getFirstChar(node.title);
            if (!lists.containsKey(alpha)) {
                List<NodeModel> list = new ArrayList<NodeModel>();
                list.add(node);
                lists.put(alpha, list);
            } else {
                lists.get(alpha).add(node);
            }
        }

        PinyinComparator comparator = new PinyinComparator();
        mNodes.clear();
        Iterator iter = lists.entrySet().iterator();
        int offset = 0;
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            List<NodeModel> val = (List<NodeModel>) entry.getValue();
            Collections.sort(val, comparator);
            mNodes.addAll(val);
            mAlphaPosition.put(key, offset);
            offset += val.size();
        }

        notifyDataSetChanged();
    }

}
