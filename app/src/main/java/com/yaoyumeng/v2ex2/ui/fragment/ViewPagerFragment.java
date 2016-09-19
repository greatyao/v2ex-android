package com.yaoyumeng.v2ex2.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.model.ProfileModel;
import com.yaoyumeng.v2ex2.ui.adapter.AggregateTopicsAdapter;
import com.yaoyumeng.v2ex2.ui.adapter.FavNodesAdapter;
import com.yaoyumeng.v2ex2.utils.AccountUtils;

import java.util.ArrayList;

public class ViewPagerFragment extends BaseFragment {

    public static final int TypeViewPager_Aggregation = 0;  //首页Tab
    public static final int TypeViewPager_Favorite = 1;     //节点收藏

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private TextView mEmptyText;
    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.pager_tabstrip);
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mEmptyText = (TextView) rootView.findViewById(R.id.empty_layout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mType = getArguments().getInt("type");

        if (mType == TypeViewPager_Favorite) {
            ArrayList<NodeModel> nodes = AccountUtils.readFavoriteNodes(getActivity());
            if (nodes != null && nodes.size() != 0) {
                mViewPager.setAdapter(new FavNodesAdapter(getChildFragmentManager(), nodes));
                mPagerSlidingTabStrip.setViewPager(mViewPager);
            } else {
                mEmptyText.setVisibility(View.VISIBLE);
                mPagerSlidingTabStrip.setVisibility(View.INVISIBLE);
            }
        } else {
            mViewPager.setAdapter(new AggregateTopicsAdapter(getChildFragmentManager(), getActivity()));
            mPagerSlidingTabStrip.setViewPager(mViewPager);
        }
    }

    @Override
    public void onLogin(ProfileModel profile) {
        super.onLogin(profile);

        if (mType == TypeViewPager_Favorite) {
            //如果登录了,则刷新用户收藏的节点
            mEmptyText.setVisibility(View.GONE);
            mPagerSlidingTabStrip.setVisibility(View.VISIBLE);
            refreshFavNodes();
        }
    }

    private void refreshFavNodes() {
        AccountUtils.refreshFavoriteNodes(getActivity(), new AccountUtils.OnAccountFavoriteNodesListener() {
            @Override
            public void onAccountFavoriteNodes(ArrayList<NodeModel> nodes) {
                if (nodes != null && nodes.size() != 0) {
                    mViewPager.setAdapter(new FavNodesAdapter(getFragmentManager(), nodes));
                    mPagerSlidingTabStrip.setViewPager(mViewPager);
                }
            }
        });
    }
}

