package com.yaoyumeng.v2ex.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.ui.fragment.TopicsFragment;

import java.util.ArrayList;

public class FavNodesAdapter extends FragmentPagerAdapter {

    private ArrayList<NodeModel> mNodes;

    public FavNodesAdapter(FragmentManager fm, ArrayList<NodeModel> nodes) {
        super(fm);
        mNodes = nodes;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mNodes.get(position).title;
    }

    @Override
    public Fragment getItem(int position) {
        TopicsFragment nodeFragment = new TopicsFragment();
        Bundle argument = new Bundle();
        argument.putString("node_name", mNodes.get(position).name);
        argument.putBoolean("show_menu", false);
        nodeFragment.setArguments(argument);
        return nodeFragment;
    }

    @Override
    public int getCount() {
        return mNodes.size();
    }
}
