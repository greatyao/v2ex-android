package com.yaoyumeng.v2ex2.ui;

import android.content.Intent;
import android.os.Bundle;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.fragment.NotificationFragment;
import com.yaoyumeng.v2ex2.ui.fragment.TopicsFragment;
import com.yaoyumeng.v2ex2.ui.fragment.ViewPagerFragment;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;

/**
 * 我的节点收藏/主题收藏/未读消息
 * Created by yw on 2015/6/11.
 */
public class MyInfoActivity extends SwipeBackActivity {

    public static final int TypeInvalid = 0;
    public static final int TypeMyNodesFavorite = 1;
    public static final int TypeMyTopicsFavorite = 2;
    public static final int TypeMyNotifications = 3;
    public static final int TypeMyFollowings = 4;

    private int mType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mType = intent.getIntExtra("type", TypeInvalid);
        } else {
            mType = savedInstanceState.getInt("type");
        }

        /*
        if (mType == TypeMyNodesFavorite) {
            //特别处理我的节点收藏
            initForFavoriteNodes();
            return;
        }*/

        setContentView(R.layout.activity_container);

        if (mType == TypeMyNotifications) {
            //我的未读消息
            getSupportFragmentManager().beginTransaction().add(R.id.container, new NotificationFragment()).commitAllowingStateLoss();
            setTitle(getString(R.string.title_activity_myinfo_notification));
        } else if (mType == TypeMyTopicsFavorite) {
            //我的节点收藏
            TopicsFragment fragment = new TopicsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("node_id", TopicsFragment.MyFavoriteTopics);
            bundle.putBoolean("attach_main", true);
            bundle.putBoolean("show_menu", false);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
            setTitle(getString(R.string.title_activity_myinfo_topicsfav));
        } else if (mType == TypeMyFollowings) {
            //我的特别关注
            TopicsFragment fragment = new TopicsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("node_id", TopicsFragment.MyFollowerTopics);
            bundle.putBoolean("attach_main", false);
            bundle.putBoolean("show_menu", false);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
            setTitle(getString(R.string.title_activity_myinfo_following));
        } else if (mType == TypeMyNodesFavorite) {
            ViewPagerFragment fragment = new ViewPagerFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("type", ViewPagerFragment.TypeViewPager_Favorite);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commit();
            setTitle(getString(R.string.title_activity_myinfo_nodesfav));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("type", mType);
        super.onSaveInstanceState(outState);
    }

    /*
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private TextView mEmptyText;
    private int mPagerPosition;
    private int mPagerOffsetPixels;

    private void initForFavoriteNodes() {
        //setSwipeContentViewForViewPager(R.layout.fragment_viewpager);
        setTitle(getString(R.string.title_activity_myinfo_nodesfav));

        mSwipeBack.setOnInterceptMoveEventListener(
                new SwipeBack.OnInterceptMoveEventListener() {
                    @Override
                    public boolean isViewDraggable(View v, int dx, int x, int y) {
                        if (v == mViewPager) {
                            return !(mPagerPosition == 0 && mPagerOffsetPixels == 0)
                                    || dx < 0;
                        }
                        return false;
                    }
                }
        );

        mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_tabstrip);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mEmptyText = (TextView) findViewById(R.id.empty_layout);

        ArrayList<NodeModel> nodes = AccountUtils.readFavoriteNodes(this);
        if (nodes != null && !nodes.isEmpty()) {
            mViewPager.setAdapter(new FavNodesAdapter(getSupportFragmentManager(), nodes));
            mPagerSlidingTabStrip.setViewPager(mViewPager);
        } else {
            mEmptyText.setVisibility(View.VISIBLE);
            mPagerSlidingTabStrip.setVisibility(View.INVISIBLE);
        }

        mPagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mPagerPosition = position;
                mPagerOffsetPixels = positionOffsetPixels;
            }

        });
    }
    */
}
