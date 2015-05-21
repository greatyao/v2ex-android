package com.yaoyumeng.v2ex.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;
import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.database.V2EXDataSource;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.NodeActivity;
import com.yaoyumeng.v2ex.utils.SetReadTask;
import com.yaoyumeng.v2ex.ui.TopicActivity;
import com.yaoyumeng.v2ex.ui.UserActivity;

import java.util.ArrayList;

/**
 * Created by yw on 2015/4/28.
 */
public class TopicsAdapter extends RecyclerView.Adapter<TopicsAdapter.ViewHolder> {
    Context mContext;
    ArrayList<TopicModel> mTopics = new ArrayList<TopicModel>();
    V2EXDataSource mDataSource = Application.getDataSource();

    public TopicsAdapter(Context context) {
        mContext = context;
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

                if(topic.content == null || topic.contentRendered == null)
                    intent.putExtra("topic_id", topic.id);
                else
                    intent.putExtra("model", (Parcelable) topic);
                if(!mDataSource.isTopicRead(topic.id))
                    new SetReadTask(topic, TopicsAdapter.this).execute();
                mContext.startActivity(intent);
            }
        });

        ImageLoader.getInstance().displayImage(topic.member.avatar, viewHolder.avatar);
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserActivity.class);
                intent.putExtra("model", (Parcelable) topic.member);
                mContext.startActivity(intent);
            }
        });

        viewHolder.title.setText(topic.title);
        viewHolder.name.setText(topic.member.username);

        //这里设置已读未读颜色
        viewHolder.title.setTextColor(Application.getDataSource().isTopicRead(topic.id) ?
                mContext.getResources().getColor(R.color.list_item_read) :
                mContext.getResources().getColor(R.color.list_item_unread));

        if (topic.created > 0) {
            long created = topic.created * 1000;
            long now = System.currentTimeMillis();
            long difference = now - created;
            CharSequence text = (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                    mContext.getString(R.string.just_now) :
                    DateUtils.getRelativeTimeSpanString(
                            created,
                            now,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE);
            viewHolder.time.setText(text);
        }

        final NodeModel node = topic.node;
        viewHolder.nodeTitle.setText(node.title);
        viewHolder.nodeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NodeActivity.class);
                intent.putExtra("model", (Parcelable) node);
                mContext.startActivity(intent);
            }
        });

        if (topic.replies > 0) {
            viewHolder.replies.setVisibility(View.VISIBLE);
            viewHolder.replies.setText(String.valueOf(topic.replies));
        } else {
            viewHolder.replies.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mTopics.size();
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
        public CardView card;
        public ImageView avatar;
        public TextView title;
        public TextView nodeTitle;
        public TextView name;
        public TextView time;
        public BadgeView replies;

        public ViewHolder(View view) {
            super(view);

            card = (CardView) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            title = (TextView) view.findViewById(R.id.title);
            nodeTitle = (TextView) view.findViewById(R.id.node_title);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            replies = (BadgeView) view.findViewById(R.id.txt_replies);
        }
    }
}