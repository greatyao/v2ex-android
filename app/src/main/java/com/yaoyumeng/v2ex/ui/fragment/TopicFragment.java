package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.HttpRequestHandler;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.database.V2EXDataSource;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.ReplyModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.model.V2EXModel;
import com.yaoyumeng.v2ex.ui.BaseActivity;
import com.yaoyumeng.v2ex.ui.NodeActivity;
import com.yaoyumeng.v2ex.ui.TopicCommentActivity;
import com.yaoyumeng.v2ex.ui.UserActivity;
import com.yaoyumeng.v2ex.ui.adapter.HeaderViewRecyclerAdapter;
import com.yaoyumeng.v2ex.ui.adapter.ReplyAdapter;
import com.yaoyumeng.v2ex.ui.social.ShareHelper;
import com.yaoyumeng.v2ex.ui.widget.EnterLayout;
import com.yaoyumeng.v2ex.ui.widget.RichTextView;
import com.yaoyumeng.v2ex.utils.InputUtils;
import com.yaoyumeng.v2ex.utils.MessageUtils;

import java.util.ArrayList;

public class TopicFragment extends BaseFragment
        implements HttpRequestHandler<ArrayList<ReplyModel>> {

    public static final int REQUEST_COMMENT = 100;
    RecyclerView mRecyclerView;
    View mHeader;
    ReplyAdapter mAdapter;
    HeaderViewRecyclerAdapter mHeaderAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;
    TopicModel mTopic;
    int mTopicId;
    MenuItem mStarItem;
    MenuItem mUnStarItem;
    MenuItem mReplyItem;
    EnterLayout mEnterLayout;
    V2EXDataSource mDataSource = Application.getDataSource();
    boolean mIsStarred;

    View.OnClickListener onClickSend = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String content = mEnterLayout.getContent();
            if (content.isEmpty()) {
                MessageUtils.showMiddleToast(getActivity(), getString(R.string.topic_comment_not_empty));
                return;
            }
            InputUtils.popSoftkeyboard(getActivity(), mEnterLayout.content, false);
            ((BaseActivity) getActivity()).showProgressBar(R.string.topic_comment_working);
            V2EXManager.replyCreateWithTopicId(getActivity(), mTopicId, content, new CommentHelper());
        }
    };

    ReplyAdapter.OnItemCommentClickListener onItemCommentClick = new ReplyAdapter.OnItemCommentClickListener() {
        @Override
        public void onItemCommentClick(ReplyModel replyObj) {
            prepareAddComment(replyObj, true);
        }
    };

    public TopicFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_topic, container, false);
        mHeader = inflater.inflate(R.layout.item_topic_more, container, false);

        mEnterLayout = new EnterLayout(getActivity(), rootView, onClickSend);
        mEnterLayout.hide();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_replies);
        mAdapter = new ReplyAdapter(getActivity(), onItemCommentClick);

        RecyclerView.LayoutParams headerLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeader.setLayoutParams(headerLayoutParams);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        mHeaderAdapter.addHeaderView(mHeader);
        mRecyclerView.setAdapter(mHeaderAdapter);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTopicData(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        mSwipeLayout.setRefreshing(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().containsKey("model")) {
            mTopic = getArguments().getParcelable("model");
            mTopicId = mTopic.id;
            setupHeaderView();
            getReplyData(false);
        } else if (getArguments().containsKey("topic_id")) {
            mTopicId = getArguments().getInt("topic_id");
            getTopicData(false);
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.topic, menu);
        mStarItem = menu.findItem(R.id.menu_topic_star);
        mUnStarItem = menu.findItem(R.id.menu_topic_unstar);
        mReplyItem = menu.findItem(R.id.menu_topic_reply);

        mIsStarred = mDataSource.isTopicFavorite(mTopicId);

        mStarItem.setVisible(mIsLogin && !mIsStarred);
        mUnStarItem.setVisible(mIsLogin && mIsStarred);
        mReplyItem.setVisible(mIsLogin);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_topic_share:
                share();
                break;
            case R.id.menu_topic_reply:
                replyToAuthor();
                break;
            case R.id.menu_topic_star:
            case R.id.menu_topic_unstar:
                favTopic();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(ArrayList<ReplyModel> data) {
        mAdapter.update(data);
        mSwipeLayout.setRefreshing(false);
        if (!mIsLogin)
            mEnterLayout.hide();
        else
            mEnterLayout.show();
    }

    @Override
    public void onSuccess(ArrayList<ReplyModel> data, int totalPages, int currentPage) {
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(false);
        MessageUtils.showErrorMessage(getActivity(), error);
    }

    @Override
    public boolean onBackPressed() {
        if (mEnterLayout.content.getText().toString().isEmpty())
            return false;
        else {
            mEnterLayout.clearContent();
            return true;
        }
    }

    //获取该话题下的所有回复
    private void getReplyData(boolean refresh) {
        prepareAddComment(mTopic, false);
        V2EXManager.getRepliesByTopicId(getActivity(), mTopicId, refresh, this);
    }

    //获取该话题内容和其所有回复
    private void getTopicData(boolean refresh) {
        prepareAddComment(mTopic, false);
        V2EXManager.getTopicByTopicId(getActivity(), mTopicId, refresh,
                new RequestTopicHelper(refresh));
    }

    private void favTopic() {
        showProgress(R.string.fav_topic_working);
        V2EXManager.favTopicWithTopicId(getActivity(), mTopicId, new FavTopicHelper());
    }

    void prepareAddComment(V2EXModel data, boolean popKeyboard) {
        final EditText content = mEnterLayout.content;
        String replyToWho = "";
        mEnterLayout.clearContent();
        if (data instanceof TopicModel) {
            TopicModel topicObj = (TopicModel) data;
            content.setHint("评论话题");
        } else if (data instanceof ReplyModel) {
            final ReplyModel replyObj = (ReplyModel) data;
            content.setHint("回复 " + replyObj.member.username);
            replyToWho = "@" + replyObj.member.username + " ";
        }

        if (popKeyboard) {
            InputUtils.popSoftkeyboard(getActivity(), content, true);
            final String replyToWho_ = replyToWho;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    content.setText(replyToWho_);
                    content.setSelection(content.getText().length());
                }
            }, 1500);
        } else {
            InputUtils.popSoftkeyboard(getActivity(), content, false);
        }
    }

    private void share() {
        ShareHelper helper = new ShareHelper(getActivity());
        helper.setContent(getString(R.string.app_name), mTopic.title, V2EXManager.getBaseUrl() + "/t/" + mTopicId);
        helper.handleShare();
    }

    //回复话题作者
    private void replyToAuthor() {
        reply(mTopic.member.username);
    }

    //回复回帖的人
    private void replyToReplier(ReplyModel model) {
        reply(model.member.username);
    }

    private void reply(String username) {
        Intent intent = new Intent(getActivity(), TopicCommentActivity.class);
        intent.putExtra("topic_id", mTopicId);
        intent.putExtra("reply_to", username);
        startActivityForResult(intent, REQUEST_COMMENT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_COMMENT) {
            if (resultCode == Activity.RESULT_OK || data != null) {
                MessageUtils.showMiddleToast(getActivity(),
                        getActivity().getString(R.string.topic_comment_succeed));
                ReplyModel reply = (ReplyModel) data.getParcelableExtra("reply_result");
                mAdapter.insert(reply);
            }
        }
    }

    /**
     * 设置话题,将其设置为ListView的HeaderView
     */
    private void setupHeaderView() {
        ImageView avatar = (ImageView) mHeader.findViewById(R.id.avatar);
        TextView titleTextView = (TextView) mHeader.findViewById(R.id.text_title);
        RichTextView contentTextView = (RichTextView) mHeader.findViewById(R.id.text_content);
        TextView authorTextView = (TextView) mHeader.findViewById(R.id.text_author);
        TextView timeTextView = (TextView) mHeader.findViewById(R.id.text_timeline);
        TextView repliesTextView = (TextView) mHeader.findViewById(R.id.text_replies);
        TextView nodeTextView = (TextView) mHeader.findViewById(R.id.text_node);

        titleTextView.setText(mTopic.title);
        authorTextView.setText(mTopic.member.username);
        String imageURL = mTopic.member.avatar;
        ImageLoader.getInstance().displayImage(imageURL, avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                intent.putExtra("model", (Parcelable) mTopic.member);
                startActivity(intent);
            }
        });

        String content = mTopic.contentRendered;
        contentTextView.setRichText(content);

        repliesTextView.setText(mTopic.replies + "个回复");

        final NodeModel node = mTopic.node;
        nodeTextView.setText(node.title);
        nodeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NodeActivity.class);
                intent.putExtra("model", (Parcelable) node);
                startActivity(intent);
            }
        });

        if (mTopic.created > 0) {
            long created = mTopic.created * 1000;
            long now = System.currentTimeMillis();
            long difference = now - created;
            CharSequence text = (difference >= 0 && difference <= DateUtils.MINUTE_IN_MILLIS) ?
                    getString(R.string.just_now) :
                    DateUtils.getRelativeTimeSpanString(
                            created,
                            now,
                            DateUtils.MINUTE_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_RELATIVE);
            timeTextView.setText(text);
        }
    }

    /**
     * 获取/刷新该话题内容的帮助类
     */
    class RequestTopicHelper implements HttpRequestHandler<ArrayList<TopicModel>> {
        boolean refresh;

        public RequestTopicHelper(boolean refresh) {
            this.refresh = refresh;
        }

        @Override
        public void onSuccess(ArrayList<TopicModel> data) {
            if (data.size() > 0) {
                mTopic = data.get(0);
                mTopicId = mTopic.id;
                setupHeaderView();
                getReplyData(refresh);
            } else {
                mSwipeLayout.setRefreshing(false);
            }
        }

        @Override
        public void onSuccess(ArrayList<TopicModel> data, int total, int current) {
        }

        @Override
        public void onFailure(String error) {
            mSwipeLayout.setRefreshing(false);
            MessageUtils.showErrorMessage(getActivity(), error);
        }
    }

    /**
     * 评论类
     */
    class CommentHelper implements HttpRequestHandler<Integer> {
        @Override
        public void onSuccess(Integer data) {
            ((BaseActivity) getActivity()).showProgressBar(false);
            String content = mEnterLayout.getContent();
            mEnterLayout.clearContent();
            mEnterLayout.hideKeyboard();

            Intent intent = new Intent();
            ReplyModel reply = new ReplyModel();
            reply.content = reply.contentRendered = content;
            reply.created = System.currentTimeMillis() / 1000;
            reply.member = mLoginProfile;
            intent.putExtra("reply_result", (Parcelable) reply);
            onActivityResult(REQUEST_COMMENT, Activity.RESULT_OK, intent);
        }

        @Override
        public void onSuccess(Integer data, int total, int current) {
        }

        @Override
        public void onFailure(String error) {
            ((BaseActivity) getActivity()).showProgressBar(false);
            MessageUtils.showErrorMessage(getActivity(), error);
        }
    }

    /**
     * 话题收藏类
     */
    class FavTopicHelper implements HttpRequestHandler<Integer> {
        @Override
        public void onSuccess(Integer data) {
            ((BaseActivity) getActivity()).showProgressBar(false);
            mIsStarred = data == 200;
            mStarItem.setVisible(!mIsStarred);
            mUnStarItem.setVisible(mIsStarred);
            mDataSource.favoriteTopic(mTopic, mIsStarred);
            MessageUtils.showMiddleToast(getActivity(),
                    getString(mIsStarred ? R.string.fav_topic_ok : R.string.unfav_topic_ok));
        }

        @Override
        public void onSuccess(Integer data, int total, int current) {

        }

        @Override
        public void onFailure(String error) {
            ((BaseActivity) getActivity()).showProgressBar(false);
            MessageUtils.showErrorMessage(getActivity(), error);
        }
    }
}
