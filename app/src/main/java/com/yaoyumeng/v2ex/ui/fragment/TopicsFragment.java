package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.melnykov.fab.FloatingActionButton;
import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.database.V2EXDataSource;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.BaseActivity;
import com.yaoyumeng.v2ex.ui.MainActivity;
import com.yaoyumeng.v2ex.ui.TopicAddActivity;
import com.yaoyumeng.v2ex.ui.adapter.TopicsAdapter;
import com.yaoyumeng.v2ex.utils.MessageUtils;

import java.util.ArrayList;

/**
 * 显示单个节点下的话题或最新/最热话题类
 */
public class TopicsFragment extends BaseFragment implements V2EXManager.HttpRequestHandler<ArrayList<TopicModel>> {
    public static final int RESULT_ADD_TOPIC = 100;
    public static final String TAG = "TopicsFragment";
    public static final int LatestTopics = 0;
    public static final int HotTopics = -1;
    public static final int InvalidTopics = -2;
    int mNodeId = InvalidTopics;   //0表示最新话题,-1表示最热话题,其他表示节点下的话题
    RecyclerView mRecyclerView;
    TopicsAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;
    SwipeRefreshLayout mSwipeLayout;
    FloatingActionButton mAddButton;
    boolean mIsLoading;
    boolean mAttachMain;
    NodeModel mNode;
    String mNodeName;
    MenuItem mStarItem;
    MenuItem mUnStarItem;
    boolean mIsStarred;
    boolean mShowMenu;
    V2EXDataSource mDataSource = Application.getDataSource();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mAttachMain = args.getBoolean("attach_main", false);
        mNodeId = args.getInt("node_id", InvalidTopics);
        mNodeName = args.getString("node_name", "");
        mShowMenu = args.getBoolean("show_menu", false);

        if (mNodeId <= 0 && mAttachMain)
            ((MainActivity) activity).onSectionAttached(mNodeId == LatestTopics ? 1 : 2);

        setHasOptionsMenu(mShowMenu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_topics, container, false);

        mAddButton = (FloatingActionButton) rootView.findViewById(R.id.add_topic_button);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_topics);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if ((mNodeId > 0 || !mNodeName.isEmpty()) && mIsLogin)
            mAddButton.attachToRecyclerView(mRecyclerView);
        else
            mAddButton.hide(false);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TopicAddActivity.class);
                if (mNode == null)
                    intent.putExtra("node_name", mNodeName);
                else
                    intent.putExtra("model", (Parcelable) mNode);
                startActivityForResult(intent, RESULT_ADD_TOPIC);
            }
        });

        mAdapter = new TopicsAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestTopics(true);
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
        Bundle args = getArguments();
        mSwipeLayout.setRefreshing(true);
        if (args.containsKey("node_name")) {
            mNodeName = args.getString("node_name");
            requestTopicsByName(false);
        } else if (args.containsKey("node_id")) {
            mNodeId = args.getInt("node_id");
            requestTopicsById(false);
        } else if (args.containsKey("model")) {
            mNode = (NodeModel) args.getParcelable("model");
            mNodeId = mNode.id;
            mNodeName = mNode.name;
            requestTopicsById(false);
        } else {
            getActivity().finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_ADD_TOPIC) {
            if (resultCode == Activity.RESULT_OK || data != null) {
                final TopicModel topic = (TopicModel) data.getParcelableExtra("create_result");
                mAdapter.update(new ArrayList<TopicModel>() {{
                    add(topic);
                }}, true);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.node, menu);
        mStarItem = menu.findItem(R.id.menu_node_star);
        mUnStarItem = menu.findItem(R.id.menu_node_unstar);
        if (mShowMenu && mIsLogin) {
            mIsStarred = mDataSource.isNodeFavorite(mNodeName);
            mStarItem.setVisible(!mIsStarred);
            mUnStarItem.setVisible(mIsStarred);
        } else {
            mStarItem.setVisible(false);
            mUnStarItem.setVisible(false);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_node_star:
            case R.id.menu_node_unstar:
                favoriteNode();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(ArrayList<TopicModel> data) {
        mSwipeLayout.setRefreshing(false);
        mIsLoading = false;
        if (data.size() == 0) return;

        if (mNode == null)
            mNode = data.get(0).node;

        if (!mAttachMain && mNodeName.isEmpty())
            mNodeName = data.get(0).node.name;

        mAdapter.update(data, true);
    }

    @Override
    public void onSuccess(ArrayList<TopicModel> data, int totalPages, int currentPage){
    }

    @Override
    public void onFailure(int reason, String error) {
        mSwipeLayout.setRefreshing(false);
        mIsLoading = false;
    }

    private void favoriteNode() {
        showProgress(R.string.fav_nodes_working);
        V2EXManager.favNodeWithNodeName(getActivity(), mNodeName, new RequestFavNodeHelper());
    }

    private void requestTopicsByName(boolean refresh) {
        V2EXManager.getTopicsByNodeName(getActivity(), mNodeName, refresh, this);
    }

    private void requestTopicsById(boolean refresh) {
        if (mNodeId == LatestTopics)
            V2EXManager.getLatestTopics(getActivity(), refresh, this);
        else if (mNodeId == HotTopics)
            V2EXManager.getHotTopics(getActivity(), refresh, this);
        else if (mNodeId > 0)
            V2EXManager.getTopicsByNodeId(getActivity(), mNodeId, refresh, this);
    }

    private void requestTopics(boolean refresh) {
        if (mIsLoading)
            return;

        mIsLoading = true;
        if (mNodeName != null && !mNodeName.isEmpty())
            requestTopicsByName(refresh);
        else
            requestTopicsById(refresh);

    }

    private class RequestFavNodeHelper implements V2EXManager.HttpRequestHandler<Integer> {
        @Override
        public void onSuccess(Integer data) {
            ((BaseActivity) getActivity()).showProgressBar(false);
            mIsStarred = data == 200;
            mStarItem.setVisible(!mIsStarred);
            mUnStarItem.setVisible(mIsStarred);
            mDataSource.favoriteNode(mNodeName, mIsStarred);
            MessageUtils.showMiddleToast(getActivity(),
                    getString(mIsStarred ? R.string.fav_nodes_ok : R.string.unfav_nodes_ok));
        }

        @Override
        public void onSuccess(Integer data, int totalPages, int currentPage){
        }

        @Override
        public void onFailure(int reason, String error) {
            ((BaseActivity) getActivity()).showProgressBar(false);
            MessageUtils.showErrorMessage(getActivity(), error);
        }
    }

}
