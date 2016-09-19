package com.yaoyumeng.v2ex2.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.fragment.TopicsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yw on 2015/6/10.
 */
public class AggregateTopicsAdapter extends FragmentStatePagerAdapter {
    private List<TopicsFragment> mFragments = new ArrayList<TopicsFragment>();
    private List<String> mTitles = new ArrayList<String>();
    private final Context mContext;

    public AggregateTopicsAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
        initFragments();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    private void initFragments() {

        //最新话题
        {
            TopicsFragment newestTopicsFragment = new TopicsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("node_id", TopicsFragment.LatestTopics);
            bundle.putBoolean("attach_main", true);
            bundle.putBoolean("show_menu", false);
            newestTopicsFragment.setArguments(bundle);
            mFragments.add(newestTopicsFragment);
            mTitles.add(mContext.getString(R.string.main_discovery_newest));
        }

        //今日热议话题
        {
            TopicsFragment hotTopicsFragment = new TopicsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("node_id", TopicsFragment.HotTopics);
            bundle.putBoolean("attach_main", true);
            bundle.putBoolean("show_menu", false);
            hotTopicsFragment.setArguments(bundle);
            mFragments.add(hotTopicsFragment);
            mTitles.add(mContext.getString(R.string.main_discovery_top10));
        }

        //首页Tab话题
        String[] tabTitles = mContext.getResources().getStringArray(R.array.v2ex_favorite_tab_titles);
        String[] TabPaths = mContext.getResources().getStringArray(R.array.v2ex_favorite_tab_paths);
        for (int i = 0; i < tabTitles.length; i++) {
            TopicsFragment fragment = new TopicsFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tab", TabPaths[i]);
            bundle.putBoolean("attach_main", true);
            bundle.putBoolean("show_menu", false);
            fragment.setArguments(bundle);
            mFragments.add(fragment);
            mTitles.add(tabTitles[i]);
        }
    }
}
