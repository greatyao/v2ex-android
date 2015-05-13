package com.yaoyumeng.v2ex.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.ReplyModel;
import com.yaoyumeng.v2ex.ui.UserActivity;
import com.yaoyumeng.v2ex.ui.adapter.ReplyAdapter;

/**
 * Created by yw on 2015/5/11.
 */
public class ReplyView extends CardView {
    ImageView avatar;
    RichTextView content;
    TextView replier;
    TextView time;
    CheckBox comment;

    public ReplyView(Context context) {
        super(context);
        init();
    }

    public ReplyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReplyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.item_reply, this);
        avatar = (ImageView) findViewById(R.id.avatar);
        content = (RichTextView) findViewById(R.id.content);
        time = (TextView) findViewById(R.id.time);
        replier = (TextView) findViewById(R.id.replier);
        comment = (CheckBox) findViewById(R.id.commentBtn);
    }

    public void parse(final ReplyModel reply, boolean login,
                      final ReplyAdapter.OnItemCommentClickListener listener){
        if(login)  {
            comment.setVisibility(View.VISIBLE);
                comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener != null)
                            listener.onItemCommentClick(reply);
                    }
                });
        } else{
            comment.setVisibility(View.GONE);
        }

        content.setRichText(reply.contentRendered);

        String imageURL = reply.member.avatar;
        ImageLoader.getInstance().displayImage(imageURL, avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), UserActivity.class);
                intent.putExtra("model", (Parcelable)reply.member);
                getContext().startActivity(intent);
            }
        });

        replier.setText(reply.member.username);

        long created = reply.created * 1000;
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
}
