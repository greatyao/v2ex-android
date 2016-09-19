package com.yaoyumeng.v2ex2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.MemberModel;
import com.yaoyumeng.v2ex2.model.NotificationModel;
import com.yaoyumeng.v2ex2.model.TopicModel;
import com.yaoyumeng.v2ex2.ui.TopicActivity;
import com.yaoyumeng.v2ex2.ui.UserActivity;
import com.yaoyumeng.v2ex2.utils.OnScrollToBottomListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2015/5/15.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    List<NotificationModel> mNotifications = new ArrayList<NotificationModel>();
    private Context mContext;
    OnScrollToBottomListener mListener;

    public NotificationsAdapter(Context context, OnScrollToBottomListener listen) {
        this.mContext = context;
        this.mListener = listen;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // 给ViewHolder设置布局文件
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        // 给ViewHolder设置元素
        final NotificationModel model = mNotifications.get(i);
        final MemberModel member = model.notificationMember;
        final TopicModel topic = model.notificationTopic;

        viewHolder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TopicActivity.class);
                intent.putExtra("topic_id", topic.id);
                mContext.startActivity(intent);
            }
        });


        final String imageURL = member.avatar;
        final String username = member.username;
        ImageLoader.getInstance().displayImage(imageURL, viewHolder.avatar);
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UserActivity.class);
                intent.putExtra("username", username);
                mContext.startActivity(intent);
            }
        });

        viewHolder.title.setText(username + " " + model.notificationDescriptionBefore +
                model.notificationTopic.title + model.notificationDescriptionAfter);

        String contentTxt = topic.content;
        if (contentTxt == null || contentTxt.isEmpty()) {
            viewHolder.content.setVisibility(View.GONE);
        } else {
            viewHolder.content.setVisibility(View.VISIBLE);
            viewHolder.content.setText(Html.fromHtml(contentTxt));
        }

        String date = topic.url;
        viewHolder.time.setText(date);

        if (mNotifications.size() - i <= 1 && mListener != null) {
            mListener.onLoadMore();
        }
    }

    public void update(ArrayList<NotificationModel> data, boolean merge) {
        if (merge) {
            mNotifications.addAll(data);
        } else {
            mNotifications = data;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        // 返回数据总数
        return mNotifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View card;
        ImageView avatar;
        TextView content;
        TextView title;
        TextView time;

        public ViewHolder(View view) {
            // super这个参数一定要注意,必须为Item的根节点.否则会出现莫名的FC.
            super(view);

            card = (View) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            content = (TextView) view.findViewById(R.id.content);
            title = (TextView) view.findViewById(R.id.title);
            time = (TextView) view.findViewById(R.id.notify_time);
        }

    }

}
