package com.yaoyumeng.v2ex.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;
import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.NodeActivity;
import com.yaoyumeng.v2ex.ui.UserActivity;

/**
 * Created by yw on 2015/5/11.
 */
public class TopicView extends CardView {
    public ImageView avatar;
    public TextView title;
    public TextView nodeTitle;
    public TextView name;
    public TextView time;
    public BadgeView replies;

    public TopicView(Context context) {
        super(context);
        init();
    }

    public TopicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_topic, this);

        avatar = (ImageView) findViewById(R.id.avatar);
        title = (TextView) findViewById(R.id.title);
        nodeTitle = (TextView) findViewById(R.id.node_title);
        name = (TextView) findViewById(R.id.name);
        time = (TextView) findViewById(R.id.time);
        replies = (BadgeView) findViewById(R.id.txt_replies);
    }

    public void parse(final TopicModel topic){
        ImageLoader.getInstance().displayImage(topic.member.avatar, avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("model", (Parcelable)topic.member);
                getContext().startActivity(intent);
            }
        });

        title.setText(topic.title);
        name.setText(topic.member.username);

        //这里设置已读未读颜色
        title.setTextColor(Application.getDataSource().isTopicRead(topic.id) ?
                getContext().getResources().getColor(R.color.list_item_read) :
               getContext().getResources().getColor(R.color.list_item_unread));

        if (topic.created > 0) {
            long created = topic.created * 1000;
            long now = System.currentTimeMillis();
            long difference = now - created;
            CharSequence text = (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                    getContext().getString(R.string.just_now) :
                    DateUtils.getRelativeTimeSpanString(
                            created,
                            now,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE);
            time.setText(text);
        }

        final NodeModel node = topic.node;
        nodeTitle.setText(node.title);
        nodeTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NodeActivity.class);
                intent.putExtra("model", (Parcelable)node);
                getContext().startActivity(intent);
            }
        });

        if(topic.replies > 0) {
            replies.setVisibility(View.VISIBLE);
            replies.setText(String.valueOf(topic.replies));
        }else {
            replies.setVisibility(View.INVISIBLE);
        }
    }

}