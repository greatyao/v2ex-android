package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.yaoyumeng.v2ex.R;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(4);
    }
}

