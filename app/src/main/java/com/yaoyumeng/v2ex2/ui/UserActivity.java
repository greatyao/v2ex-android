package com.yaoyumeng.v2ex2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.MemberModel;
import com.yaoyumeng.v2ex2.ui.fragment.UserFragment;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;

import java.util.List;

/**
 * Created by yw on 2015/5/2.
 */
public class UserActivity extends SwipeBackActivity {
    MemberModel mMember;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {
            /**
             * deal such scheme: <a href="http://www.v2ex.com/member/njustyw">go</>
             *
             * AndroidMainfext.xml config:
             * <data android:scheme="http" android:host="www.v2ex.com" android:pathPattern="/member/.*" />
             */
            Intent intent = getIntent();
            Uri data = intent.getData();
            String scheme = data != null ? data.getScheme() : ""; // "http"
            String host = data != null ? data.getHost() : ""; // "www.v2ex.com"
            List<String> params = data != null ? data.getPathSegments() : null;
            if ((scheme.equals("http") || scheme.equals("https"))
                    && (host.equals("www.v2ex.com") || host.equals("v2ex.com"))
                    && params != null && params.size() == 2) {
                mUsername = params.get(1);
                setTitle(mUsername);
            } else {
                if (intent.hasExtra("model")) {
                    mMember = intent.getParcelableExtra("model");
                    mUsername = mMember.username;
                    setTitle(mUsername);
                } else {
                    mUsername = intent.getStringExtra("username");
                    setTitle(mUsername);
                }
            }
        } else {
            mUsername = savedInstanceState.getString("username");
        }

        UserFragment fragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("username", mUsername);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("username", mUsername);
        super.onSaveInstanceState(outState);
    }

}