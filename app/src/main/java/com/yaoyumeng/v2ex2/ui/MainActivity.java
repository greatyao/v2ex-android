package com.yaoyumeng.v2ex2.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.ViewConfiguration;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.umeng.update.UmengUpdateAgent;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.ProfileModel;
import com.yaoyumeng.v2ex2.service.INoticeService;
import com.yaoyumeng.v2ex2.service.NoticeService;
import com.yaoyumeng.v2ex2.ui.fragment.AllNodesFragment;
import com.yaoyumeng.v2ex2.ui.fragment.MyInfoFragment;
import com.yaoyumeng.v2ex2.ui.fragment.ViewPagerFragment;
import com.yaoyumeng.v2ex2.ui.widget.ChangeColorIconWithText;
import com.yaoyumeng.v2ex2.utils.AccountUtils;
import com.yaoyumeng.v2ex2.utils.MessageUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnTabChangeListener {
    private FragmentTabHost mTabHost;
    private LayoutInflater mLayoutInflater;
    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<ChangeColorIconWithText>();
    private INoticeService mService = null;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = INoticeService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setOverflowButtonAlways();
        initTabHost();
        handleIntent(getIntent());

        if (mIsLogin) {
            AccountUtils.refreshProfile(this);
            AccountUtils.refreshFavoriteNodes(this, null);
            startNoticeService();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    public void onLogout() {
        super.onLogout();
        stopNoticeService();
    }

    @Override
    public void onLogin(ProfileModel member) {
        super.onLogin(member);
        startNoticeService();
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            MessageUtils.showToast(this, getString(R.string.main_exitapp_hint));
            exitTime = System.currentTimeMillis();
        } else {
            stopNoticeService();
            finish();
        }
    }

    private void initTabHost() {
        mLayoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.tab_content);
        mTabHost.getTabWidget().setDividerDrawable(null);

        TabHost.TabSpec[] tabSpecs = new TabHost.TabSpec[3];
        String[] texts = new String[3];
        ChangeColorIconWithText[] tabviews = new ChangeColorIconWithText[3];

        Bundle bundle = new Bundle();
        bundle.putInt("type", ViewPagerFragment.TypeViewPager_Aggregation);
        texts[0] = getString(R.string.title_activity_main_discovery);
        tabviews[0] = getTabView(R.layout.item_tab_discovery);
        tabSpecs[0] = mTabHost.newTabSpec(texts[0]).setIndicator(tabviews[0]);
        mTabHost.addTab(tabSpecs[0], ViewPagerFragment.class, bundle);
        mTabIndicators.add(tabviews[0]);

        texts[1] = getString(R.string.title_activity_main_nodes);
        tabviews[1] = getTabView(R.layout.item_tab_allnodes);
        tabSpecs[1] = mTabHost.newTabSpec(texts[1]).setIndicator(tabviews[1]);
        mTabHost.addTab(tabSpecs[1], AllNodesFragment.class, null);
        mTabIndicators.add(tabviews[1]);

        texts[2] = getString(R.string.title_activity_main_myinfo);
        tabviews[2] = getTabView(R.layout.item_tab_myinfo);
        tabSpecs[2] = mTabHost.newTabSpec(texts[2]).setIndicator(tabviews[2]);
        mTabHost.addTab(tabSpecs[2], MyInfoFragment.class, null);
        mTabIndicators.add(tabviews[2]);

        mTabHost.setOnTabChangedListener(this);
        mTabHost.setCurrentTab(0);
        onTabChanged(mTabHost.getCurrentTabTag());
    }

    private void handleIntent(Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_VIEW)) {
            ;
        } else if (intent.getBooleanExtra("NOTICE", false)) {
            showNotification(intent);
        }
    }

    private void showNotification(Intent fromWhich) {
        if (fromWhich != null) {
            boolean fromNoticeBar = fromWhich.getBooleanExtra("NOTICE", false);
            if (fromNoticeBar) {
                Intent intent = new Intent(this, MyInfoActivity.class);
                intent.putExtra("type", MyInfoActivity.TypeMyNotifications);
                startActivity(intent);
            }
        }
    }

    private void startNoticeService() {
        if (mService == null) {
            Intent intent = new Intent(this, NoticeService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }
    }

    private void stopNoticeService() {
        if (mService != null) {
            mService = null;
            unbindService(mConnection);
        }
        sendBroadcast(new Intent(NoticeService.INTENT_ACTION_SHUTDOWN));
    }

    private ChangeColorIconWithText getTabView(int layoutId) {
        ChangeColorIconWithText tab = (ChangeColorIconWithText) mLayoutInflater.inflate(layoutId, null);
        return tab;
    }

    @Override
    public void onTabChanged(String tabId) {
        setTitle(tabId);

        resetOtherTabs();
        ChangeColorIconWithText tabview = (ChangeColorIconWithText) mTabHost.getCurrentTabView();
        if (tabview != null)
            tabview.setIconAlpha(1.0f);
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

    /**
     * 重置其他的TabIndicator的颜色
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }
}
