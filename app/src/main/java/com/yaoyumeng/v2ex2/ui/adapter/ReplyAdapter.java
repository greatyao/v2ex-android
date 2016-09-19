package com.yaoyumeng.v2ex2.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.ReplyModel;
import com.yaoyumeng.v2ex2.model.V2EXDateModel;
import com.yaoyumeng.v2ex2.ui.UserActivity;
import com.yaoyumeng.v2ex2.ui.widget.RichTextView;
import com.yaoyumeng.v2ex2.utils.AccountUtils;
import com.yaoyumeng.v2ex2.utils.OnScrollToBottomListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2015/4/28.
 */
public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    Context mContext;
    List<ReplyModel> mReplies = new ArrayList<ReplyModel>();
    boolean mLogin;
    OnItemCommentClickListener mListener;
    OnScrollToBottomListener mScrollListener;

    public ReplyAdapter(Context context, OnItemCommentClickListener listener, OnScrollToBottomListener scrollListener) {
        mContext = context;
        mListener = listener;
        mScrollListener = scrollListener;
        mLogin = AccountUtils.isLogined(mContext);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_reply, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        final ReplyModel reply = mReplies.get(i);

        if (mLogin) {
            View.OnClickListener onClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null)
                        mListener.onItemCommentClick(reply);
                }
            };

            viewHolder.comment.setVisibility(View.VISIBLE);
            viewHolder.comment.setOnClickListener(onClick);

            viewHolder.card.setOnClickListener(onClick);
        } else {
            viewHolder.comment.setVisibility(View.GONE);
        }

        if (reply.contentRendered != null)
            viewHolder.content.setRichText(reply.contentRendered);

        String imageURL = reply.member.avatar;
        ImageLoader.getInstance().displayImage(imageURL, viewHolder.avatar);
        viewHolder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reply.member == null) return;
                Intent intent = new Intent(mContext, UserActivity.class);
                intent.putExtra("model", (Parcelable) reply.member);
                mContext.startActivity(intent);
            }
        });

        viewHolder.replier.setText(reply.member.username);

        viewHolder.time.setText(V2EXDateModel.toString(reply.created));

        viewHolder.floor.setText(String.format("第%d楼", i + 1));

        if (viewHolder.divide != null)
            viewHolder.divide.setVisibility(i == mReplies.size() - 1 ? View.GONE : View.VISIBLE);

        if (mReplies.size() - i <= 1 && mScrollListener != null) {
            mScrollListener.onLoadMore();
        }
    }

    @Override
    public int getItemCount() {
        return mReplies.size();
    }

    public void update(ArrayList<ReplyModel> data) {
        mReplies = data;
        notifyDataSetChanged();
    }

    public void insert(ArrayList<ReplyModel> replies) {
        mReplies.addAll(replies);
        notifyDataSetChanged();
    }

    public void insert(ReplyModel reply) {
        mReplies.add(reply);
        notifyDataSetChanged();
    }

    static public interface OnItemCommentClickListener {
        abstract void onItemCommentClick(ReplyModel replyObj);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View card;
        ImageView avatar;
        RichTextView content;
        TextView replier;
        TextView time;
        CheckBox comment;
        TextView floor;
        View divide;

        public ViewHolder(View view) {
            super(view);

            card = (View) view.findViewById(R.id.card_container);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            content = (RichTextView) view.findViewById(R.id.content);
            time = (TextView) view.findViewById(R.id.time);
            replier = (TextView) view.findViewById(R.id.replier);
            comment = (CheckBox) view.findViewById(R.id.commentBtn);
            floor = (TextView) view.findViewById(R.id.which_floor);
            divide = view.findViewById(R.id.divide_reply);
        }
    }
}
