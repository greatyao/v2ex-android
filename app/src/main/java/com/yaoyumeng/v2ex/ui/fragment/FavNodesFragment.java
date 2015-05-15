package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.ui.MainActivity;
import com.yaoyumeng.v2ex.ui.adapter.FavNodesAdapter;
import com.yaoyumeng.v2ex.utils.AccountUtils;

import java.util.ArrayList;

public class FavNodesFragment extends BaseFragment {

    SwipeRefreshLayout mSwipeLayout;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private TextView mEmptyText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fav_nodes, container, false);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) rootView.findViewById(R.id.tab_fragment_collection);
        mViewPager = (ViewPager) rootView.findViewById(R.id.viewpager_fragment_collection);
        mEmptyText = (TextView) rootView.findViewById(R.id.txt_fragment_collection_empty);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayList<NodeModel> favnodes = AccountUtils.readFavoriteNodes(getActivity());
        if (favnodes != null && favnodes.size() != 0) {
            mViewPager.setAdapter(new FavNodesAdapter(getFragmentManager(), favnodes));
            mPagerSlidingTabStrip.setViewPager(mViewPager);
        } else {
            mEmptyText.setVisibility(View.VISIBLE);
            mPagerSlidingTabStrip.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLogin(MemberModel member) {
        super.onLogin(member);

        //如果登录了,则刷新用户收藏的节点
        mEmptyText.setVisibility(View.GONE);
        mPagerSlidingTabStrip.setVisibility(View.VISIBLE);
        refreshFavNodes();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }

    class RefreshNodesHelper extends AccountUtils.FavNodesHelper{

        public RefreshNodesHelper(Context cxt){
            super(cxt);
        }

        @Override
        public void onSuccess(ArrayList<NodeModel> data){
            super.onSuccess(data);
            if (data != null && data.size() != 0) {
                mViewPager.setAdapter(new FavNodesAdapter(getFragmentManager(), data));
                mPagerSlidingTabStrip.setViewPager(mViewPager);
            }

        }

        @Override
        public void onFailure(int reason, String error){
        }

    }

    private void refreshFavNodes(){
        AccountUtils.refreshFavoriteNodes(getActivity(), new RefreshNodesHelper(getActivity()));

    }
}

