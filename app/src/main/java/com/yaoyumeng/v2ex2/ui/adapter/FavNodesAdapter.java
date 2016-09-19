package com.yaoyumeng.v2ex2.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.ui.fragment.TopicsFragment;

import java.util.ArrayList;
import java.util.List;

public class FavNodesAdapter extends FragmentStatePagerAdapter {
    private List<TopicsFragment> mFragments = new ArrayList<TopicsFragment>();
    private List<NodeModel> mNodes;

    public FavNodesAdapter(FragmentManager fm, ArrayList<NodeModel> nodes) {
        super(fm);
        mNodes = nodes;
        initFragments();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNodes.get(position).title;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }

    private void initFragments() {
        for (int i = 0; i < mNodes.size(); i++) {
            TopicsFragment nodeFragment = new TopicsFragment();
            Bundle argument = new Bundle();
            argument.putString("node_name", mNodes.get(i).name);
            argument.putBoolean("show_menu", false);
            nodeFragment.setArguments(argument);
            mFragments.add(nodeFragment);
        }
    }
}
