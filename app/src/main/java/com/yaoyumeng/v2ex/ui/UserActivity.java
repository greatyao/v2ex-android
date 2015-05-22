package com.yaoyumeng.v2ex.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.ui.fragment.UserFragment;

import java.util.List;

/**
 * Created by yw on 2015/5/2.
 */
public class UserActivity extends BaseActivity {
    MemberModel mMember;
    String mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    && host.equals("www.v2ex.com")
                    && params != null && params.size() == 2) {
                mUsername = params.get(1);
                setTitle(mUsername);
            } else {
                if (intent.hasExtra("model")) {
                    mMember = (MemberModel) intent.getParcelableExtra("model");
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
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("username", mUsername);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}