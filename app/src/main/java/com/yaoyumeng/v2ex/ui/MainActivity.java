package com.yaoyumeng.v2ex.ui;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Toast;

import com.umeng.update.UmengUpdateAgent;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.ui.fragment.AllNodesFragment;
import com.yaoyumeng.v2ex.ui.fragment.FavNodesFragment;
import com.yaoyumeng.v2ex.ui.fragment.NavigationDrawerFragment;
import com.yaoyumeng.v2ex.ui.fragment.NotificationFragment;
import com.yaoyumeng.v2ex.ui.fragment.SettingsFragment;
import com.yaoyumeng.v2ex.ui.fragment.TopicsFragment;

public class MainActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{


    private NavigationDrawerFragment mNavigationDrawerFragment;
    ViewGroup mDrawerLayout;

    private TopicsFragment mNewestTopicsFragment;
    private TopicsFragment mHotTopicsFragment;
    private AllNodesFragment mAllNodesFragment;
    private FavNodesFragment mFavNodesFragment;
    private NotificationFragment mNotificationFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UmengUpdateAgent.setDefault();
        UmengUpdateAgent.update(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (ViewGroup)findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.left_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.left_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //ActionBar actionBar = getActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        // update the main content by replacing fragments
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        switch (position){
            case 0:
                if(mNewestTopicsFragment == null){
                    mNewestTopicsFragment = new TopicsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("node_id", TopicsFragment.LatestTopics);
                    bundle.putBoolean("attach_main", true);
                    bundle.putBoolean("show_menu", false);
                    mNewestTopicsFragment.setArguments(bundle);
                }
                fragmentTransaction.replace(R.id.container, mNewestTopicsFragment);
                break;
            case 1:
                if(mHotTopicsFragment == null){
                    mHotTopicsFragment = new TopicsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("node_id", TopicsFragment.HotTopics);
                    bundle.putBoolean("attach_main", true);
                    bundle.putBoolean("show_menu", false);
                    mHotTopicsFragment.setArguments(bundle);
                }
                fragmentTransaction.replace(R.id.container, mHotTopicsFragment);
                break;
            case 2:
                if(mAllNodesFragment == null){
                    mAllNodesFragment = new AllNodesFragment();
                }
                fragmentTransaction.replace(R.id.container, mAllNodesFragment);
                break;
            case 3:
                if(mFavNodesFragment == null){
                    mFavNodesFragment = new FavNodesFragment();
                }
                fragmentTransaction.replace(R.id.container, mFavNodesFragment);
                break;
            case 4:
                if(mNotificationFragment == null){
                    mNotificationFragment = new NotificationFragment();
                }
                fragmentTransaction.replace(R.id.container, mNotificationFragment);
                break;
            case 5:
                fragmentTransaction.replace(R.id.container, new SettingsFragment());
                break;
        }
        fragmentTransaction.commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_main_latest);
                break;
            case 2:
                mTitle = getString(R.string.title_main_hot);
                break;
            case 3:
                mTitle = getString(R.string.title_main_all_nodes);
                break;
            case 4:
                mTitle = getString(R.string.title_main_fav_nodes);
                break;
            case 5:
                mTitle = getString(R.string.title_main_notification);
                break;
            case 6:
                mTitle = getString(R.string.title_main_settings);
                break;
        }
    }

    public void restoreActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
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
        if(mNavigationDrawerFragment.isDrawerOpen()){
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
}
