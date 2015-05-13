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
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.NodeActivity;
import com.yaoyumeng.v2ex.ui.UserActivity;

/**
 * Created by yw on 2015/5/11.
 */
public class TopicMoreView extends CardView {
    ImageView avatar;
    TextView titleTextView;
    RichTextView contentTextView;
    TextView authorTextView;
    TextView timeTextView;
    TextView repliesTextView;
    TextView nodeTextView;

    public TopicMoreView(Context context) {
        super(context);
        init();
    }

    public TopicMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopicMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.item_topic_more, this);

        avatar = (ImageView) findViewById(R.id.avatar);
        titleTextView = (TextView) findViewById(R.id.text_title);
        contentTextView = (RichTextView) findViewById(R.id.text_content);
        authorTextView = (TextView) findViewById(R.id.text_author);
        timeTextView = (TextView) findViewById(R.id.text_timeline);
        repliesTextView = (TextView) findViewById(R.id.text_replies);
        nodeTextView = (TextView) findViewById(R.id.text_node);
    }

    public void parse(final TopicModel topic){
        titleTextView.setText(topic.title);
        authorTextView.setText(topic.member.username);
        String imageURL = topic.member.avatar;
        ImageLoader.getInstance().displayImage(imageURL, avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("model", (Parcelable) topic.member);
                getContext().startActivity(intent);
            }
        });

        String content = topic.contentRendered;
        contentTextView.setMaxLines(Integer.MAX_VALUE);
        contentTextView.setTextSize(16);
        contentTextView.setLineSpacing(3f, 1.2f);
        contentTextView.setRichText(content);

        repliesTextView.setText(topic.replies + "个回复");

        final NodeModel node = topic.node;
        nodeTextView.setText(node.title);
        nodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NodeActivity.class);
                intent.putExtra("model", (Parcelable)node);
                getContext().startActivity(intent);
            }
        });

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
            timeTextView.setText(text);
        }
    }
}
