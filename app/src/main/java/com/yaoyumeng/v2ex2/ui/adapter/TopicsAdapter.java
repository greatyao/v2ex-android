package com.yaoyumeng.v2ex2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;
import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.database.V2EXDataSource;
import com.yaoyumeng.v2ex2.model.MemberModel;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.model.TopicModel;
import com.yaoyumeng.v2ex2.model.V2EXDateModel;
import com.yaoyumeng.v2ex2.ui.NodeActivity;
import com.yaoyumeng.v2ex2.ui.TopicActivity;
import com.yaoyumeng.v2ex2.ui.UserActivity;
import com.yaoyumeng.v2ex2.utils.OnScrollToBottomListener;
import com.yaoyumeng.v2ex2.utils.SetReadTask;

import java.util.ArrayList;

/**
 * Created by yw on 2015/4/28.
 */
public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    Context mContext;
    OnScrollToBottomListener mListener;
    ArrayList<TopicModel> mTopics = new ArrayList<TopicModel>();
    V2EXDataSource mDataSource = Application.getDataSource();

    public TopicsAdapter(Context context, OnScrollToBottomListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_topic, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final TopicModel topic = mTopics.get(i);
        final MemberModel member = topic.member;

        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TopicActivity.class);

                if (topic.content == null || topic.contentRendered == null)
                    intent.putExtra("topic_id", topic.id);
                else
                    intent.putExtra("model", (Parcelable) topic);
                if (!mDataSource.isTopicRead(topic.id))
                    new SetReadTask(topic, TopicsAdapter.this).execute();
                mContext.startActivity(intent);
            }
        });

        if (member != null) {
            ImageLoader.getInstance().displayImage(member.avatar, viewHolder.avatar);
            viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, UserActivity.class);
                    intent.putExtra("model", (Parcelable) member);
                    mContext.startActivity(intent);
                }
            });

            viewHolder.name.setText(member.username);
        }

        viewHolder.title.setText(topic.title);

        //这里设置已读未读颜色
        boolean read = Application.getDataSource().isTopicRead(topic.id);
        viewHolder.title.setTextColor( read ?
                mContext.getResources().getColor(R.color.list_item_read) :
                mContext.getResources().getColor(R.color.list_item_unread));

        viewHolder.time.setText(V2EXDateModel.toString(topic.created));

        final NodeModel node = topic.node;
        viewHolder.nodeTitle.setText(node.title);
        viewHolder.nodeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (node == null) return;
                Intent intent = new Intent(mContext, NodeActivity.class);
                intent.putExtra("model", (Parcelable) node);
                mContext.startActivity(intent);
            }
        });

        if (topic.replies > 0) {
            int count_color = read ?
                    mContext.getResources().getColor(R.color.topic_count_read) :
                    mContext.getResources().getColor(R.color.topic_count_unread);
            viewHolder.replies.setVisibility(View.VISIBLE);
            viewHolder.replies.setText(String.valueOf(topic.replies));
            viewHolder.replies.setBadgeBackgroundColor(count_color);

            //由于上面的setBadgeBackgroundColor没起作用
            //以下代码组合了BadgeView类的getDefaultBackground和show两个函数
            Resources res = viewHolder.replies.getResources();
            int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, res.getDisplayMetrics());
            float[] outerR = new float[] {px, px, px, px, px, px, px, px};
            RoundRectShape rr = new RoundRectShape(outerR, null, null);
            ShapeDrawable drawable = new ShapeDrawable(rr);
            drawable.getPaint().setColor(count_color);
            viewHolder.replies.setBackgroundDrawable(drawable);
        } else {
            viewHolder.replies.setVisibility(View.INVISIBLE);
        }

        if (mTopics.size() - i <= 1 && mListener != null) {
            mListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mTopics.size();
    }

    public void insertAtBack(ArrayList<TopicModel> data, boolean merge) {
        if (merge)
            mTopics.addAll(data);
        else
            mTopics = data;
        notifyDataSetChanged();
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

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View card;
        public ImageView avatar;
        public TextView title;
        public TextView nodeTitle;
        public TextView name;
        public TextView time;
        public BadgeView replies;

        public ViewHolder(View view) {
            super(view);

            card = (View) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            title = (TextView) view.findViewById(R.id.title);
            nodeTitle = (TextView) view.findViewById(R.id.node_title);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            replies = (BadgeView) view.findViewById(R.id.txt_replies);
        }
    }
}