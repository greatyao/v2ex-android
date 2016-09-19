package com.yaoyumeng.v2ex2.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.ui.adapter.AllNodesAdapter;
import com.yaoyumeng.v2ex2.ui.widget.IndexableRecyclerView;
import com.yaoyumeng.v2ex2.utils.MessageUtils;

import java.util.ArrayList;

/**
 * Created by yw on 2015/4/28.
 */
public class AllNodesFragment extends BaseFragment
        implements HttpRequestHandler<ArrayList<NodeModel>> {
    private static final String TAG = "AllNodesFragment";
    IndexableRecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    AllNodesAdapter mNodeAdapter;
    SwipeRefreshLayout mSwipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_all_nodes, container, false);

        final Context context = getActivity();
        mNodeAdapter = new AllNodesAdapter(context);
        mRecyclerView = (IndexableRecyclerView) layout.findViewById(R.id.grid_all_node);

        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mNodeAdapter);
        mRecyclerView.setFastScrollEnabled(true);

        mSwipeLayout = (SwipeRefreshLayout) layout.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNode(true);
            }
        });
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeLayout.setProgressViewOffset(false, 0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeLayout.setRefreshing(true);
        requestNode(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_nodes, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_nodes_search);

        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_nodes_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNodeAdapter.filterText(newText);
                return true;
            }
        });
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data) {
        mNodeAdapter.update(data);
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data, int totalPages, int currentPage) {
        mNodeAdapter.update(data);
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    public void onFailure(String error) {
        mSwipeLayout.setRefreshing(false);
        MessageUtils.showErrorMessage(getActivity(), error);
    }

    private void requestNode(boolean refresh) {
        V2EXManager.getAllNodes(getActivity(), refresh, this);
    }
}
