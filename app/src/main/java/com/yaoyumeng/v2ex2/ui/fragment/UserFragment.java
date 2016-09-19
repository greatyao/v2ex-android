package com.yaoyumeng.v2ex2.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.twotoasters.jazzylistview.effects.FadeEffect;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;
import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.model.MemberModel;
import com.yaoyumeng.v2ex2.model.TopicModel;
import com.yaoyumeng.v2ex2.ui.adapter.HeaderViewRecyclerAdapter;
import com.yaoyumeng.v2ex2.ui.adapter.TopicsAdapter;
import com.yaoyumeng.v2ex2.ui.widget.DividerItemDecoration;
import com.yaoyumeng.v2ex2.ui.widget.FootUpdate;
import com.yaoyumeng.v2ex2.utils.MessageUtils;
import com.yaoyumeng.v2ex2.utils.OnScrollToBottomListener;

import java.util.ArrayList;

public class UserFragment extends BaseFragment
        implements HttpRequestHandler<ArrayList<MemberModel>>, OnScrollToBottomListener {

    private static final String FIELD_UNSET = "未设置";
    private View mHeader;
    private SwipeRefreshLayout mSwipeLayout;
    private HeaderViewRecyclerAdapter mHeaderAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private TopicsAdapter mAdapter;
    private ImageView mHeaderLogo;
    private TextView mName;
    private TextView mDescription;
    private TextView mTagline;
    private TextView mWebSite;
    private TextView mTwitter;
    private TextView mGithub;
    private TextView mLocation;
    private LinearLayout mWebSiteLayout;
    private LinearLayout mTwitterLayout;
    private LinearLayout mGithubLayout;
    private LinearLayout mLocationLayout;
    private MemberModel mMember;
    private String mUserName;
    private int mPage = 1;
    private boolean mNoMore = true;
    private Application mApp = Application.getInstance();


    private View.OnClickListener layoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mMember == null) return;
            int id = v.getId();
            String url = "";
            if (id == R.id.github_layout && mMember.github != null && !mMember.github.isEmpty()) {
                url = "https://github.com/" + mMember.github;
            } else if (id == R.id.twitter_layout && mMember.twitter != null && !mMember.twitter.isEmpty()) {
                url = "https://twitter.com/" + mMember.twitter;
            } else if (id == R.id.homepage_layout && mMember.website != null && !mMember.website.isEmpty()) {
                url = mMember.website;
            }

            if (!url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topics, container, false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_topics);
        rootView.findViewById(R.id.add_topic_button).setVisibility(View.GONE);

        mHeader = inflater.inflate(R.layout.fragment_user, container, false);
        mHeaderLogo = (ImageView) mHeader.findViewById(R.id.header_logo_fragment_user);
        mName = (TextView) mHeader.findViewById(R.id.txt_fragment_user_name);
        mTagline = (TextView) mHeader.findViewById(R.id.txt_fragment_user_tagline);
        mDescription = (TextView) mHeader.findViewById(R.id.txt_fragment_user_description);
        mWebSite = (TextView) mHeader.findViewById(R.id.title_homepage);
        mTwitter = (TextView) mHeader.findViewById(R.id.title_twitter_account);
        mGithub = (TextView) mHeader.findViewById(R.id.title_github_account);
        mLocation = (TextView) mHeader.findViewById(R.id.title_location);
        mWebSiteLayout = (LinearLayout) mHeader.findViewById(R.id.homepage_layout);
        mTwitterLayout = (LinearLayout) mHeader.findViewById(R.id.twitter_layout);
        mGithubLayout = (LinearLayout) mHeader.findViewById(R.id.github_layout);
        mLocationLayout = (LinearLayout) mHeader.findViewById(R.id.location_layout);
        mWebSiteLayout.setOnClickListener(layoutClick);
        mGithubLayout.setOnClickListener(layoutClick);
        mTwitterLayout.setOnClickListener(layoutClick);

        RecyclerView.LayoutParams headerLayoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeader.setLayoutParams(headerLayoutParams);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new TopicsAdapter(getActivity(), this);
        mHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        mHeaderAdapter.addHeaderView(mHeader);
        mRecyclerView.setAdapter(mHeaderAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mFootUpdate.init(mHeaderAdapter, LayoutInflater.from(getActivity()), new FootUpdate.LoadMore() {
            @Override
            public void loadMore() {
                getTopicsOfNextPage();
            }
        });

        if (mApp.isShowEffectFromCache()) {
            JazzyRecyclerViewScrollListener scrollListener = new JazzyRecyclerViewScrollListener();
            mRecyclerView.setOnScrollListener(scrollListener);
            scrollListener.setTransitionEffect(new FadeEffect());
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPage = 1;
                if (mMember == null)
                    V2EXManager.getMemberInfoByUsername(getActivity(), mUserName, true, UserFragment.this);
                else
                    getTopicsData(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipeLayout.setRefreshing(true);

        if (getArguments().containsKey("model")) {
            mMember = getArguments().getParcelable("model");
            showData();
        } else if (getArguments().containsKey("username")) {
            mUserName = getArguments().getString("username");
            mName.setText(mUserName);
            mDescription.setText("Loading...");
            V2EXManager.getMemberInfoByUsername(getActivity(), mUserName, false, this);
        }
    }

    @Override
    public void onSuccess(ArrayList<MemberModel> data) {
        if (data.size() > 0) {
            mMember = data.get(0);
            showData();
        } else {
            mSwipeLayout.setRefreshing(false);
        }
    }

    @Override
    public void onSuccess(ArrayList<MemberModel> data, int total, int current) {
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(false);
        MessageUtils.showErrorMessage(getActivity(), error);
    }

    @Override
    public void onLoadMore() {
        if (!mNoMore && !mApp.isJsonAPIFromCache()) {
            getTopicsOfNextPage();
        }
    }

    private void showData() {
        mName.setText(mMember.username);
        mTagline.setText(mMember.tagline);
        mDescription.setText("V2EX 第 " + mMember.id + " 号会员");
        ImageLoader.getInstance().displayImage(mMember.avatar, mHeaderLogo);
        mLocation.setText(mMember.location.isEmpty() ? FIELD_UNSET : mMember.location);
        mGithub.setText(mMember.github.isEmpty() ? FIELD_UNSET : mMember.github);
        mTwitter.setText(mMember.twitter.isEmpty() ? FIELD_UNSET : mMember.twitter);
        mWebSite.setText(mMember.website.isEmpty() ? FIELD_UNSET : mMember.website);

        getTopicsData(true);
    }

    private void getTopicsOfNextPage() {
        V2EXManager.getTopicsByUsernameThroughBrowser(getActivity(), mUserName, mPage + 1, new RequestTopicHelper());
    }

    private void getTopicsData(boolean refresh) {
        if (mApp.isJsonAPIFromCache())
            V2EXManager.getTopicsByUsername(getActivity(), mUserName, refresh, new RequestTopicHelper());
        else
            V2EXManager.getTopicsByUsernameThroughBrowser(getActivity(), mUserName, 1, new RequestTopicHelper());
    }

    class RequestTopicHelper implements HttpRequestHandler<ArrayList<TopicModel>> {
        @Override
        public void onSuccess(ArrayList<TopicModel> data) {
            onSuccess(data, 1, 1);
        }

        @Override
        public void onSuccess(ArrayList<TopicModel> data, int totalPages, int currentPage) {
            mPage = currentPage;
            mNoMore = totalPages == currentPage;
            mSwipeLayout.setRefreshing(false);

            if (!mApp.isJsonAPIFromCache()) {
                for (TopicModel topic : data)
                    topic.member = mMember;
            }

            mAdapter.insertAtBack(data, currentPage != 1);

            if (mNoMore) {
                mFootUpdate.dismiss();
            } else {
                mFootUpdate.showLoading();
            }
        }

        @Override
        public void onFailure(String error) {
            mSwipeLayout.setRefreshing(false);
            MessageUtils.showErrorMessage(getActivity(), error);

            if (mAdapter.getItemCount() > 0 && !mNoMore) {
                mFootUpdate.showFail();
            } else {
                mFootUpdate.dismiss();
            }
        }
    }
}
