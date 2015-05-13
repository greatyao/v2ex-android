package com.yaoyumeng.v2ex.ui.widget;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.NodeModel;

/**
 * Created by yw on 2015/5/11.
 */
public class NodeView extends CardView {
    public NodeView(Context context) {
        super(context);
        init();
    }

    public NodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NodeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private TextView mTitle;
    private TextView mHeader;
    private TextView mTopics;

    private int mNodeId;

    private void init(){
        inflate(getContext(), R.layout.item_node, this);
        mTitle = (TextView) findViewById(R.id.node_title);
        mHeader = (TextView) findViewById(R.id.node_summary);
        mTopics = (TextView) findViewById(R.id.node_topics);
    }

    public void parse(NodeModel model){
        mNodeId = model.id;
        mTitle.setText(model.title);
        if(model.header != null){
            mHeader.setVisibility(VISIBLE);
            mHeader.setText(Html.fromHtml(model.header));
        }else{
            mHeader.setVisibility(GONE);
        }
        mTopics.setText(model.topics + " 个话题");
    }

    public int getNodeId(){
        return mNodeId;
    }
}
