package com.yaoyumeng.v2ex2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.model.TopicModel;
import com.yaoyumeng.v2ex2.ui.fragment.TopicFragment;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;

import java.util.List;

public class TopicActivity extends SwipeBackActivity {
    TopicModel mTopic;
    int mTopicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        TopicFragment fragment = new TopicFragment();
        Bundle bundle = new Bundle();

        if (savedInstanceState == null) {
            /**
             * deal such scheme: <a href="http://www.v2ex.com/t/1">go</>
             *
             * AndroidMainfext.xml config:
             * <data android:scheme="http" android:host="www.v2ex.com" android:pathPattern="/t/.*" />
             */
            Intent intent = getIntent();
            Uri data = intent.getData();
            String scheme = data != null ? data.getScheme() : ""; // "http"
            String host = data != null ? data.getHost() : ""; // "www.v2ex.com"
            List<String> params = data != null ? data.getPathSegments() : null;
            if ((scheme.equals("http") || scheme.equals("https"))
                    && (host.equals("www.v2ex.com") || host.equals("v2ex.com"))
                    && params != null && params.size() == 2) {
                String topicId = params.get(1);
                mTopicId = Integer.parseInt(topicId);
                bundle.putInt("topic_id", mTopicId);
            } else {
                if (intent.hasExtra("model")) {
                    mTopic = intent.getParcelableExtra("model");
                    mTopicId = mTopic.id;
                    bundle.putParcelable("model", mTopic);
                } else {
                    mTopicId = intent.getIntExtra("topic_id", 0);
                    bundle.putInt("topic_id", mTopicId);
                }
            }
        } else {
            mTopicId = savedInstanceState.getInt("topic_id");
            bundle.putInt("topic_id", mTopicId);
        }

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
                } else {
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("topic_id", mTopicId);
        super.onSaveInstanceState(outState);
    }
}
