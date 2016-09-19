package com.yaoyumeng.v2ex2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.ui.fragment.TopicsFragment;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;

import java.util.List;

public class NodeActivity extends SwipeBackActivity {
    private static String TAG = "TopicActivity";
    int mNodeId;
    String mNodeName;
    NodeModel mNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        TopicsFragment fragment = new TopicsFragment();
        Bundle bundle = new Bundle();
        if (savedInstanceState == null) {
            /**
             * deal such scheme: <a href="http://www.v2ex.com/go/v2ex">go</>
             *
             * AndroidMainfext.xml config:
             * <data android:scheme="http" android:host="www.v2ex.com" android:pathPattern="/go/.*" />
             */
            Intent intent = getIntent();
            Uri data = intent.getData();
            String scheme = data != null ? data.getScheme() : ""; // "http"
            String host = data != null ? data.getHost() : ""; // "www.v2ex.com"
            List<String> params = data != null ? data.getPathSegments() : null;
            if ((scheme.equals("http") || scheme.equals("https"))
                    && (host.equals("www.v2ex.com") || host.equals("v2ex.com"))
                    && params != null && params.size() == 2) {
                mNodeName = params.get(1);
                bundle.putString("node_name", mNodeName);
            } else {
                mNode = intent.getParcelableExtra("model");
                if (mNode == null) finish();
                setTitle(mNode.title);
                mNodeId = mNode.id;
                mNodeName = mNode.name;
                //bundle.putInt("node_id", mNodeId);
                bundle.putString("node_name", mNodeName);
            }
        } else {
            mNodeId = savedInstanceState.getInt("id");
            bundle.putInt("node_id", mNodeId);
        }
        bundle.putBoolean("show_menu", true);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment).commitAllowingStateLoss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("id", mNodeId);
        outState.putParcelable("model", mNode);
        super.onSaveInstanceState(outState);
    }
}
