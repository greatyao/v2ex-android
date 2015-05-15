package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.ui.NodeActivity;
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
public class AllNodesAdapter extends RecyclerView.Adapter<AllNodesAdapter.ViewHolder> {
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
        viewHolder.topics.setText(node.topics + " 个话题");
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
