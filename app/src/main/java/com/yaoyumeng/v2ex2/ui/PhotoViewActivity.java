package com.yaoyumeng.v2ex2.ui;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.adapter.PhotoViewerPagerAdapter;
import com.yaoyumeng.v2ex2.ui.photo.PhotoViewCallbacks;
import com.yaoyumeng.v2ex2.ui.photo.PhotoViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhotoViewActivity extends BaseActivity implements ViewPager.OnPageChangeListener, PhotoViewPager.OnInterceptTouchListener, PhotoViewCallbacks {
    public static final String EXTRA_PHOTO_INDEX = "photo_index";
    public static final String EXTRA_PHOTO_DATAS = "photo_arrays";

    private PhotoViewPager mViewPager;
    private PhotoViewerPagerAdapter mAdapter;

    private ArrayList<String> mPhohoUrls;

    /**
     * The index of the currently viewed photo
     */
    private int mCurrentPhotoIndex;

    /**
     * The listeners wanting full screen state for each screen position
     */
    private final Map<Integer, OnScreenListener>
            mScreenListeners = new HashMap<Integer, OnScreenListener>();

    public static void launch(Context context, int position, ArrayList<String> photoUrls) {
        Intent intent = new Intent(context, PhotoViewActivity.class);
        intent.putExtra(EXTRA_PHOTO_INDEX, position);
        intent.putExtra(EXTRA_PHOTO_DATAS, photoUrls);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        final Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_PHOTO_DATAS)) {
            mPhohoUrls = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_PHOTO_DATAS);
        }

        mCurrentPhotoIndex = getIntent().getIntExtra(EXTRA_PHOTO_INDEX, 0);

        mAdapter = new PhotoViewerPagerAdapter(getSupportFragmentManager());
        mAdapter.setData(mPhohoUrls);

        mViewPager = (PhotoViewPager) findViewById(R.id.photo_view_pager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOnInterceptTouchListener(this);
        mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.photo_page_margin));
        mViewPager.setCurrentItem(mCurrentPhotoIndex);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPhotoIndex = position;
        setTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public PhotoViewPager.InterceptType onTouchIntercept(float origX, float origY) {
        boolean interceptLeft = false;
        boolean interceptRight = false;

        for (OnScreenListener listener : mScreenListeners.values()) {
            if (!interceptLeft) {
                interceptLeft = listener.onInterceptMoveLeft(origX, origY);
            }
            if (!interceptRight) {
                interceptRight = listener.onInterceptMoveRight(origX, origY);
            }
        }

        if (interceptLeft) {
            if (interceptRight) {
                return PhotoViewPager.InterceptType.BOTH;
            }
            return PhotoViewPager.InterceptType.LEFT;
        } else if (interceptRight) {
            return PhotoViewPager.InterceptType.RIGHT;
        }
        return PhotoViewPager.InterceptType.NONE;
    }

    @Override
    public void addScreenListener(int position, OnScreenListener listener) {
        mScreenListeners.put(position, listener);
    }

    @Override
    public void removeScreenListener(int position) {
        mScreenListeners.remove(position);
    }

    @Override
    public boolean isFragmentActive(Fragment fragment) {
        if (mViewPager == null || mAdapter == null) {
            return false;
        }
        return mViewPager.getCurrentItem() == mAdapter.getItemPosition(fragment);
    }

    private void setTitle(){
        super.setTitle(String.format("%d / %d", mCurrentPhotoIndex+1, mPhohoUrls.size()));
    }
}
