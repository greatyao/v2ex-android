package com.yaoyumeng.v2ex2.ui.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.adapter.HeaderViewRecyclerAdapter;

import java.lang.reflect.Method;

/**
 * Created by chaochen on 14-10-22.
 */
public class FootUpdate {

    View mLayout;
    View mClick;
    View mLoading;
    Object mListView;
    boolean mAdd = false;

    public FootUpdate() {
    }

    public int getHigh() {
        if (mLayout == null) {
            return 0;
        }

        return mLayout.getHeight();
    }

    private void removeFromListView(Object listView, View v) {
        if (listView instanceof ListView) {
            try {
                Method method = listView.getClass().getMethod("removeFooterView", View.class);
                method.invoke(listView, v);
                mAdd = false;
            } catch (Exception e) {
            }
        } else if (listView instanceof HeaderViewRecyclerAdapter) {
            try {
                Method method = listView.getClass().getMethod("removeFooterView", View.class);
                method.invoke(listView, v);
                mAdd = false;
            } catch (Exception e) {
            }
        }
    }

    private void addToListView(Object listView, View v) {
        if (listView instanceof ListView) {
            try {
                Method method = listView.getClass().getMethod("addFooterView", View.class);
                method.invoke(listView, v);
                mAdd = true;
            } catch (Exception e) {
            }
        } else if (listView instanceof HeaderViewRecyclerAdapter) {
            try {
                Method method = listView.getClass().getMethod("addFooterView", View.class);
                method.invoke(listView, v);
                mAdd = true;
            } catch (Exception e) {
            }
        }
    }

    public void init(Object listView, LayoutInflater inflater, final LoadMore loadMore) {
        mListView = listView;
        mLayout = inflater.inflate(R.layout.listview_foot, null, false);
        mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mClick = mLayout.findViewById(R.id.textView);
        mClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMore.loadMore();
                showLoading();
            }
        });

        mLoading = mLayout.findViewById(R.id.progressBar);

        mLayout.setVisibility(View.GONE);
    }

    public void showLoading() {
        show(true, true);
    }

    public void showFail() {
        show(true, false);
    }

    public void dismiss() {
        show(false, true);
    }

    private void show(boolean show, boolean loading) {
        if (mLayout == null) {
            return;
        }

        if (show) {
            if (!mAdd) addToListView(mListView, mLayout);

            mLayout.setVisibility(View.VISIBLE);
            mLayout.setPadding(0, 0, 0, 0);
            if (loading) {
                mClick.setVisibility(View.INVISIBLE);
                mLoading.setVisibility(View.VISIBLE);
            } else {
                mClick.setVisibility(View.VISIBLE);
                mLoading.setVisibility(View.INVISIBLE);
            }
        } else {
            removeFromListView(mListView, mLayout);
            mLayout.setVisibility(View.GONE);
            mLayout.setPadding(0, -mLayout.getHeight(), 0, 0);
        }
    }

    public static interface LoadMore {
        public void loadMore();
    }
}
