package com.yaoyumeng.v2ex2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.ui.NodeActivity;
import com.yaoyumeng.v2ex2.utils.PinyinAlpha;
import com.yaoyumeng.v2ex2.utils.PinyinComparator;

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
public class AllNodesAdapter extends RecyclerView.Adapter<AllNodesAdapter.ViewHolder> implements SectionIndexer {
    Context mContext;
    List<NodeModel> mNodes = new ArrayList<NodeModel>();
    List<NodeModel> mAllNodes = new ArrayList<NodeModel>();
    HashMap<String, Integer> mAlphaPosition = new HashMap<String, Integer>();
    String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public AllNodesAdapter(Context context) {
        mContext = context;
    }

    public HashMap<String, Integer> getAlphaPosition() {
        return mAlphaPosition;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_node, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final NodeModel node = mNodes.get(position);

        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NodeActivity.class);
                intent.putExtra("model", (Parcelable) node);
                mContext.startActivity(intent);
            }
        });

        viewHolder.title.setText(node.title);
        if (node.header != null) {
            viewHolder.header.setVisibility(View.VISIBLE);
            viewHolder.header.setText(Html.fromHtml(node.header));
        } else {
            viewHolder.header.setVisibility(View.GONE);
        }
        viewHolder.topics.setText(node.topics + " 个主题");
    }

    @Override
    public int getItemCount() {
        return mNodes.size();
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

        mAllNodes = mNodes;
        notifyDataSetChanged();
    }

    @Override
    public int getPositionForSection(int i) {
        return mAlphaPosition.get(mSections.substring(i, i + 1));
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        String[] chars = new String[mSections.length()];
        for (int i = 0; i < mSections.length(); i++) {
            chars[i] = String.valueOf(mSections.charAt(i));
        }
        return chars;
    }

    public void filterText(CharSequence query) {
        if (TextUtils.isEmpty(query)) {
            mNodes = mAllNodes;
            notifyDataSetChanged();
            return;
        }

        List<NodeModel> result = new ArrayList<NodeModel>();
        for (NodeModel node : mAllNodes) {
            if (node.name.contains(query) ||
                    node.title.contains(query) ||
                    (node.titleAlternative != null && node.titleAlternative.contains(query)))
                result.add(node);
        }

        mNodes = result;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView header;
        public TextView topics;
        public CardView card;

        public ViewHolder(View view) {
            super(view);

            card = (CardView) view.findViewById(R.id.card_container);
            title = (TextView) view.findViewById(R.id.node_title);
            header = (TextView) view.findViewById(R.id.node_summary);
            topics = (TextView) view.findViewById(R.id.node_topics);
        }
    }

}
