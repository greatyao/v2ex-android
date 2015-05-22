package com.yaoyumeng.v2ex.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.ui.adapter.SpinnerAdapter;
import com.yaoyumeng.v2ex.ui.fragment.AllNodesFragment;
import com.yaoyumeng.v2ex.ui.fragment.FavNodesFragment;
import com.yaoyumeng.v2ex.ui.fragment.NavigationDrawerFragment;
import com.yaoyumeng.v2ex.ui.fragment.NotificationFragment;
import com.yaoyumeng.v2ex.ui.fragment.SettingsFragment;
import com.yaoyumeng.v2ex.ui.fragment.TopicsFragment;
import com.yaoyumeng.v2ex.utils.AccountUtils;
import com.yaoyumeng.v2ex.utils.MessageUtils;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private SpinnerAdapter mSpinnerAdapter;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ViewGroup mDrawerLayout;
    private View mActionbarCustom;

    private TopicsFragment mNewestTopicsFragment;
    private TopicsFragment mHotTopicsFragment;
    private AllNodesFragment mAllNodesFragment;
    private FavNodesFragment mFavNodesFragment;
    private NotificationFragment mNotificationFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private String[] mFavoriteTabTitles;
    private String[] mFavoriteTabPaths;
    private String[] mMainTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.update(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (ViewGroup) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.left_drawer);
        mTitle = getTitle();

        mFavoriteTabTitles = getResources().getStringArray(R.array.v2ex_favorite_tab_titles);
        mFavoriteTabPaths = getResources().getStringArray(R.array.v2ex_favorite_tab_paths);
        mMainTitles = getResources().getStringArray(R.array.v2ex_nav_main_titles);

        mSpinnerAdapter = new SpinnerAdapter(this, mFavoriteTabTitles);

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setCustomView(R.layout.actionbar_custom_spinner);
        mActionbarCustom = supportActionBar.getCustomView();
        Spinner spinner = (Spinner) supportActionBar.getCustomView().findViewById(R.id.spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TopicsFragment fragment = new TopicsFragment();
                Bundle bundle = new Bundle();
                mSpinnerAdapter.setCheckPos(position);
                bundle.putString("tab", mFavoriteTabPaths[position]);
                bundle.putBoolean("attach_main", true);
                bundle.putBoolean("show_menu", false);
                fragment.setArguments(bundle);

                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.container, fragment, mFavoriteTabTitles[position]).commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.left_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //ActionBar actionBar = getSupportActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);
        //actionBar.setDisplayShowTitleEnabled(true);

        if (mIsLogin) initAccount();
    }

    int mSelectPos = 0;

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        mSelectPos = position;

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        switch (position) {
            case 0:
                if (mNewestTopicsFragment == null) {
                    mNewestTopicsFragment = new TopicsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("node_id", TopicsFragment.LatestTopics);
                    bundle.putBoolean("attach_main", true);
                    bundle.putBoolean("show_menu", false);
                    mNewestTopicsFragment.setArguments(bundle);
                }
                fragmentTransaction.replace(R.id.container, mNewestTopicsFragment).commit();
                break;
            case 1:
                if (mHotTopicsFragment == null) {
                    mHotTopicsFragment = new TopicsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("node_id", TopicsFragment.HotTopics);
                    bundle.putBoolean("attach_main", true);
                    bundle.putBoolean("show_menu", false);
                    mHotTopicsFragment.setArguments(bundle);
                }
                fragmentTransaction.replace(R.id.container, mHotTopicsFragment).commit();
                break;
            case 2:
                //特别处理
                break;
            case 3:
                if (mAllNodesFragment == null) {
                    mAllNodesFragment = new AllNodesFragment();
                }
                fragmentTransaction.replace(R.id.container, mAllNodesFragment).commit();
                break;
            case 4:
                if (mFavNodesFragment == null) {
                    mFavNodesFragment = new FavNodesFragment();
                }
                fragmentTransaction.replace(R.id.container, mFavNodesFragment).commit();
                break;
            case 5:
                if (mNotificationFragment == null) {
                    mNotificationFragment = new NotificationFragment();
                }
                fragmentTransaction.replace(R.id.container, mNotificationFragment).commit();
                break;
            case 6:
                fragmentTransaction.replace(R.id.container, new SettingsFragment()).commit();
                break;
        }

        if (position == 2) {
            onSpinnerSelected();
            return;
        }
    }

    private void onSpinnerSelected() {
        ActionBar actionBar = getSupportActionBar();
        Spinner spinner;
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(mActionbarCustom);
        actionBar.setTitle("");
        spinner = (Spinner) mActionbarCustom.findViewById(R.id.spinner);

        boolean containFragment = false;
        if (!containFragment) {
            int pos = spinner.getSelectedItemPosition();
            spinner.getOnItemSelectedListener().onItemSelected(null, null, pos, pos);
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (mSelectPos != 2) {
            mTitle = mMainTitles[mSelectPos];
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        } else {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(mActionbarCustom);
            actionBar.setTitle("");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }

        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "再按一次退出V2EX", Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    //刷新用户资料:包括节点收藏,话题收藏等
    public void initAccount() {
        if (mNavigationDrawerFragment.getCurrentSelectedPosition() != 4)
            AccountUtils.refreshFavoriteNodes(this, null);

        AccountUtils.refreshNotificationCount(this, new AccountUtils.OnAccountNotificationCountListener() {
            @Override
            public void onAccountNotificationCount(int count) {
                MessageUtils.showMiddleToast(MainActivity.this, "你有 " + count + " 条未读提醒");
            }
        });

    }
}
