package com.yaoyumeng.v2ex.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.SetReadTask;
import com.yaoyumeng.v2ex.ui.TopicActivity;
import com.yaoyumeng.v2ex.ui.adapter.TopicsAdapter;
import com.yaoyumeng.v2ex.utils.ScreenUtils;

import java.util.ArrayList;

public class UserFragment extends BaseFragment implements V2EXManager.HttpRequestHandler<ArrayList<MemberModel>> {

    private static final String FIELD_UNSET = "未设置";
    private int mHeaderHeight;
    private int mMinHeaderTranslation;
    private View mHeader;
    private View mPlaceHolderView;
    private TopicsAdapter mAdapter;
    private SwipeRefreshLayout mSwipeLayout;
    private ListView mListView;
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
    private ArrayList<TopicModel> mTopics;
    private String mUserName;
    private String TAG = "UserFragment";
    private View.OnClickListener layoutClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            String url = "";
            if (id == R.id.github_layout && !mMember.github.isEmpty()) {
                url = "https://github.com/" + mMember.github;
            } else if (id == R.id.twitter_layout && !mMember.twitter.isEmpty()) {
                url = "https://twitter.com/" + mMember.twitter;
            } else if (id == R.id.homepage_layout && !mMember.website.isEmpty()) {
                url = mMember.website;
            }

            if (!url.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        }
    };

    public static float clamp(float value, float max, float min) {
        return Math.max(Math.min(value, min), max);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHeaderHeight = ScreenUtils.dp(getActivity(), 250);
        mMinHeaderTranslation = -mHeaderHeight;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSwipeLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_user, container, false);
        mListView = (ListView) mSwipeLayout.findViewById(R.id.list_fragment_user);
        mHeader = mSwipeLayout.findViewById(R.id.header_fragment_user);
        mHeaderLogo = (ImageView) mSwipeLayout.findViewById(R.id.header_logo_fragment_user);
        mName = (TextView) mSwipeLayout.findViewById(R.id.txt_fragment_user_name);
        mTagline = (TextView) mSwipeLayout.findViewById(R.id.txt_fragment_user_tagline);
        mDescription = (TextView) mSwipeLayout.findViewById(R.id.txt_fragment_user_description);
        mWebSite = (TextView) mSwipeLayout.findViewById(R.id.title_homepage);
        mTwitter = (TextView) mSwipeLayout.findViewById(R.id.title_twitter_account);
        mGithub = (TextView) mSwipeLayout.findViewById(R.id.title_github_account);
        mLocation = (TextView) mSwipeLayout.findViewById(R.id.title_location);
        mWebSiteLayout = (LinearLayout) mSwipeLayout.findViewById(R.id.homepage_layout);
        mTwitterLayout = (LinearLayout) mSwipeLayout.findViewById(R.id.twitter_layout);
        mGithubLayout = (LinearLayout) mSwipeLayout.findViewById(R.id.github_layout);
        mLocationLayout = (LinearLayout) mSwipeLayout.findViewById(R.id.location_layout);
        mWebSiteLayout.setOnClickListener(layoutClick);
        mGithubLayout.setOnClickListener(layoutClick);
        mTwitterLayout.setOnClickListener(layoutClick);

        mPlaceHolderView = getActivity().getLayoutInflater().inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(mPlaceHolderView, null, false);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mTopics.size() > 0) {
                    final TopicModel topic = mTopics.get(position - 1);
                    if(!Application.getDataSource().isTopicRead(topic.id))
                        new SetReadTask(topic, mAdapter).execute();
                    Intent intent = new Intent(getActivity(), TopicActivity.class);
                    intent.putExtra("model", (Parcelable)topic );
                    startActivity(intent);
                }
            }
        });
        mAdapter = new TopicsAdapter(getActivity());
        return mSwipeLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTopicsData(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        setupActionBar();

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
    public void onFailure(int reason, String error) {
        mSwipeLayout.setRefreshing(false);
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

        setupListView();
    }

    private void setupListView() {
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int scrollY = getScrollY();
                if (firstVisibleItem == 0 && scrollY == 0)
                    mSwipeLayout.setEnabled(true);
                else
                    mSwipeLayout.setEnabled(false);
                mHeader.setTranslationY(Math.max(-scrollY, mMinHeaderTranslation));
                float ratio = clamp(mHeader.getTranslationY() / mMinHeaderTranslation, 0.0f, 1.0f);
                setTitleAlpha(clamp(5.0F * ratio - 4.0F, 0.0F, 1.0F));
            }
        });
        getTopicsData(true);
    }

    private void getTopicsData(boolean refresh) {
        V2EXManager.getTopicsByUsername(getActivity(), mUserName, refresh, new RequestTopicHelper());
    }

    private void setTitleAlpha(float alpha) {
        //getActivity().getActionBar().setTitle(mSpannableString);
        mName.setAlpha(1.0f - alpha);
        mDescription.setAlpha(1.0f - alpha);
    }

    public int getScrollY() {
        View c = mListView.getChildAt(0);
        if (c == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = c.getTop();

        int headerHeight = 0;
        if (firstVisiblePosition >= 1) {
            headerHeight = mPlaceHolderView.getHeight();
        }

        return -top + firstVisiblePosition * c.getHeight() + headerHeight;
    }

    private void setupActionBar() {
        android.support.v7.app.ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        //actionBar.setIcon(R.drawable.ic_avatar);
        //getActionBarTitleView().setAlpha(0f);
    }

    class RequestTopicHelper implements V2EXManager.HttpRequestHandler<ArrayList<TopicModel>> {
        @Override
        public void onSuccess(ArrayList<TopicModel> data) {
            mTopics = data;
            mAdapter.update(data, false);
            mSwipeLayout.setRefreshing(false);
        }

        @Override
        public void onFailure(int reason, String error) {
            mSwipeLayout.setRefreshing(false);
        }

    }

}
