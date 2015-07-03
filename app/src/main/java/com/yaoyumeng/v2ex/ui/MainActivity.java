package com.yaoyumeng.v2ex.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.ui.fragment.AllNodesFragment;
import com.yaoyumeng.v2ex.ui.fragment.MyInfoFragment;
import com.yaoyumeng.v2ex.ui.fragment.ViewPagerFragment;
import com.yaoyumeng.v2ex.ui.widget.ChangeColorIconWithText;
import com.yaoyumeng.v2ex.utils.AccountUtils;
import com.yaoyumeng.v2ex.utils.MessageUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnClickListener,
        OnPageChangeListener {
    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.update(this);

        setOverflowButtonAlways();

        initView();
        initFragment();
        initEvent();

        if (mIsLogin) {
            AccountUtils.refreshProfile(this);
            AccountUtils.refreshFavoriteNodes(this, null);
        }
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            MessageUtils.showMiddleToast(this, getString(R.string.main_exitapp_hint));
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);

        ChangeColorIconWithText discovery = (ChangeColorIconWithText) findViewById(R.id.id_indicator_discovery);
        mTabIndicators.add(discovery);
        ChangeColorIconWithText nodes = (ChangeColorIconWithText) findViewById(R.id.id_indicator_nodes);
        mTabIndicators.add(nodes);
        ChangeColorIconWithText my = (ChangeColorIconWithText) findViewById(R.id.id_indicator_my);
        mTabIndicators.add(my);

        discovery.setOnClickListener(this);
        nodes.setOnClickListener(this);
        my.setOnClickListener(this);

        discovery.setIconAlpha(1.0f);
    }

    private void initFragment() {

        {
            ViewPagerFragment aggregateFragment = new ViewPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", ViewPagerFragment.TypeViewPager_Aggregation);
            aggregateFragment.setArguments(bundle);
            mTabs.add(aggregateFragment);
        }

        mTabs.add(new AllNodesFragment());

        mTabs.add(new MyInfoFragment());

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };
        mViewPager.setAdapter(mAdapter);
    }


    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     * 利用反射，设置sHasPermanentMenuKey 为false
     * 作用是actionBar显示overflow menu
     */
    private void setOverflowButtonAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKey = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            menuKey.setAccessible(true);
            menuKey.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (positionOffset > 0) {
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);
            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }

        if (position == 0)
            setTitle(getString(R.string.title_activity_main_discovery));
        else if (position == 1)
            setTitle(getString(R.string.title_activity_main_nodes));
        else if (position == 2)
            setTitle(getString(R.string.title_activity_main_myinfo));
    }

    @Override
    public void onPageSelected(int arg0) {
    }

    @Override
    public void onClick(View v) {
        clickTab(v);
    }

    /**
     * 点击Tab按钮
     *
     * @param v
     */
    private void clickTab(View v) {
        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_discovery:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_nodes:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_my:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }
    }

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }
}
