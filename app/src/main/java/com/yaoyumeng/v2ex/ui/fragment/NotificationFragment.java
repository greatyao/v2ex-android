package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.model.NotificationModel;
import com.yaoyumeng.v2ex.ui.MainActivity;
import com.yaoyumeng.v2ex.ui.adapter.NotificationsAdapter;
import com.yaoyumeng.v2ex.utils.MessageUtils;

import java.util.ArrayList;

/**
 * 显示单个节点下的话题或最新/最热话题类
 */
public class NotificationFragment extends BaseFragment implements V2EXManager.HttpRequestHandler<ArrayList<NotificationModel>> {
    public static final String TAG = "NotificationFragment";
    RecyclerView mRecyclerView;
    NotificationsAdapter mAdapter;
    SwipeRefreshLayout mSwipeLayout;
    private TextView mEmptyText;
    boolean mIsLoading;
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NotificationsAdapter(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notification_listview);
        mLayoutManager = new LinearLayoutManager(getActivity());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mEmptyText = (TextView) rootView.findViewById(R.id.txt_fragment_notification_empty);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNotifications();
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(!mIsLogin){
            mSwipeLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mEmptyText.setVisibility(View.VISIBLE);
        } else {
            mEmptyText.setVisibility(View.GONE);
            mSwipeLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mSwipeLayout.setRefreshing(true);
            requestNotifications();
        }
    }

    @Override
    public void onLogin(MemberModel member) {
       super.onLogin(member);

        //登录,刷新信息
        mEmptyText.setVisibility(View.GONE);
        mSwipeLayout.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

        mSwipeLayout.setRefreshing(true);
        requestNotifications();
    }

    @Override
    public void onSuccess(ArrayList<NotificationModel> data) {
        mSwipeLayout.setRefreshing(false);
        mIsLoading = false;
        if (data.size() == 0) {
            MessageUtils.showMiddleToast(getActivity(), getString(R.string.notification_message));
            return;
        }

        mAdapter.update(data);
    }

    @Override
    public void onFailure(int reason, String error) {
        mSwipeLayout.setRefreshing(false);
        mIsLoading = false;
        MessageUtils.showErrorMessage(getActivity(), error);
    }

    private void requestNotifications() {
        if (mIsLoading)
            return;

        mIsLoading = true;
        V2EXManager.getNotifications(getActivity(), this);
    }
}
