package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.model.NotificationModel;
import com.yaoyumeng.v2ex.ui.MainActivity;
import com.yaoyumeng.v2ex.ui.TopicActivity;
import com.yaoyumeng.v2ex.ui.adapter.NotificationAdapter;

import java.util.ArrayList;

/**
 * 显示单个节点下的话题或最新/最热话题类
 */
public class NotificationFragment extends BaseFragment implements V2EXManager.HttpRequestHandler<ArrayList<NotificationModel>> {
    public static final String TAG = "NotificationFragment";
    ListView mListView;
    NotificationAdapter mAdapter;
    SwipeRefreshLayout mSwipeLayout;
    boolean mIsLoading;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((MainActivity) activity).onSectionAttached(5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        mListView = (ListView) rootView.findViewById(R.id.notification_listview);

        mAdapter = new NotificationAdapter(getActivity());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NotificationModel notification = (NotificationModel) mAdapter.getItem(position);
                if (notification != null) {
                    Intent intent = new Intent(getActivity(), TopicActivity.class);
                    intent.putExtra("topic_id", notification.notificationTopic.id);
                    startActivity(intent);
                }
            }
        });

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
        requestNotifications();
    }

    @Override
    public void onSuccess(ArrayList<NotificationModel> data) {
        mSwipeLayout.setRefreshing(false);
        mIsLoading = false;
        if (data.size() == 0) return;

        mAdapter.update(data);
    }

    @Override
    public void onFailure(int reason, String error) {
        mSwipeLayout.setRefreshing(false);
        mIsLoading = false;
    }

    private void requestNotifications() {
        if (mIsLoading)
            return;

        mIsLoading = true;
        V2EXManager.getNotifications(getActivity(), this);
    }
}
