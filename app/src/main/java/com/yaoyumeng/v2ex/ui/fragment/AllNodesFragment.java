package com.yaoyumeng.v2ex.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.ui.MainActivity;
import com.yaoyumeng.v2ex.ui.NodeActivity;
import com.yaoyumeng.v2ex.ui.adapter.AllNodesAdapter;
import com.yaoyumeng.v2ex.ui.widget.AlphaView;

import java.util.ArrayList;

/**
 * Created by yw on 2015/4/28.
 */
public class AllNodesFragment extends BaseFragment
        implements AlphaView.OnAlphaChangedListener, V2EXManager.HttpRequestHandler<ArrayList<NodeModel>> {
    static String TAG = "NodesActivity";
    StaggeredGridView mGridView;
    AllNodesAdapter mNodeAdapter;
    AlphaView mAlphaView;
    TextView mOverlay;
    SwipeRefreshLayout mSwipeLayout;
    WindowManager mWindowManager;
    private Handler handler = new Handler();
    private Runnable overlayThread = new Runnable() {
        @Override
        public void run() {
            mOverlay.setVisibility(View.GONE);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_all_nodes, container, false);

        final Context context = getActivity();
        mNodeAdapter = new AllNodesAdapter(context);
        mAlphaView = (AlphaView) layout.findViewById(R.id.alpha_view);
        mAlphaView.setOnAlphaChangedListener(this);
        mGridView = (StaggeredGridView) layout.findViewById(R.id.grid_all_node);
        mGridView.setAdapter(mNodeAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NodeModel node = (NodeModel) mNodeAdapter.getItem(position);
                Intent intent = new Intent(context, NodeActivity.class);
                intent.putExtra("model", (Parcelable) node);
                startActivity(intent);
            }
        });

        mOverlay = (TextView) inflater.inflate(R.layout.overlay, null);
        mOverlay.setVisibility(View.INVISIBLE);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mOverlay, lp);

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
    public void onDestroyView() {
        mWindowManager.removeView(mOverlay);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(3);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSwipeLayout.setRefreshing(true);
        requestNode(false);
    }

    @Override
    public void onSuccess(ArrayList<NodeModel> data) {
        mNodeAdapter.update(data);
        mSwipeLayout.setRefreshing(false);
        mAlphaView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFailure(int reason, String error) {
        mSwipeLayout.setRefreshing(false);
    }

    private void requestNode(boolean refresh) {
        mAlphaView.setVisibility(View.GONE);
        V2EXManager.getAllNodes(getActivity(), refresh, this);
    }

    @Override
    public void OnAlphaChanged(String s, int index) {
        if (s != null && s.trim().length() > 0) {
            mOverlay.setText(s);
            mOverlay.setVisibility(View.VISIBLE);
            handler.removeCallbacks(overlayThread);
            handler.postDelayed(overlayThread, 500);
            if (mNodeAdapter.getAlphaPosition().get(s) != null) {
                int position = mNodeAdapter.getAlphaPosition().get(s);
                mGridView.setNeedSync(true);
                mGridView.setSelection(position);
                mGridView.smoothScrollBy(100, 10);
            }
        }
    }
}
