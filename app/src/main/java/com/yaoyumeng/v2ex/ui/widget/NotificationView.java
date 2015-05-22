package com.yaoyumeng.v2ex.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.model.NotificationModel;
import com.yaoyumeng.v2ex.ui.UserActivity;

/**
 * Created by yw on 2015/5/11.
 */
public class NotificationView extends CardView {
    ImageView avatar;
    TextView content;
    TextView title;
    TextView time;

    public NotificationView(Context context) {
        super(context);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_notification, this);
        avatar = (ImageView) findViewById(R.id.avatar);
        content = (TextView) findViewById(R.id.content);
        title = (TextView) findViewById(R.id.title);
        time = (TextView) findViewById(R.id.notify_time);
    }

    public void parse(final NotificationModel model) {

        final MemberModel member = model.notificationMember;
        final String imageURL = member.avatar;
        final String username = member.username;
        ImageLoader.getInstance().displayImage(imageURL, avatar);
        avatar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("username", username);
                getContext().startActivity(intent);
            }
        });

        title.setText(username + " " + model.notificationDescriptionBefore +
                model.notificationTopic.title + model.notificationDescriptionAfter);

        String contentTxt = model.notificationTopic.content;
        if (contentTxt == null || contentTxt.isEmpty()) {
            content.setVisibility(View.GONE);
        } else {
            content.setVisibility(View.VISIBLE);
            content.setText(Html.fromHtml(contentTxt));
        }

        String date = model.notificationTopic.url;
        time.setText(date);

    }
}
