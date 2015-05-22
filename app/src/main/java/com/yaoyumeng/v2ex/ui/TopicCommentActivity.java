package com.yaoyumeng.v2ex.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.HttpRequestHandler;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.model.ReplyModel;
import com.yaoyumeng.v2ex.utils.AccountUtils;
import com.yaoyumeng.v2ex.utils.InputUtils;
import com.yaoyumeng.v2ex.utils.MessageUtils;
import com.yaoyumeng.v2ex.utils.SimpleTextWatcher;

public class TopicCommentActivity extends BaseActivity
        implements HttpRequestHandler<Integer> {

    EditText mContent;
    MenuItem mMenuAdd;
    int mTopicId;
    String mReplyToWho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_comment);

        mContent = (EditText) findViewById(R.id.topic_add_content);
        mContent.addTextChangedListener(textWatcher);

        Intent intent = getIntent();
        mTopicId = intent.getIntExtra("topic_id", 0);
        mReplyToWho = intent.getStringExtra("reply_to");
        if (mReplyToWho != null && !mReplyToWho.isEmpty()) {
            mContent.setText("@" + mReplyToWho + " ");
            //mContent.setSelection(mContent.getText().length());
            InputUtils.popSoftkeyboard(TopicCommentActivity.this, mContent, true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topic_comment, menu);
        mMenuAdd = menu.findItem(R.id.action_add);
        updateAddButton();

        return super.onCreateOptionsMenu(menu);
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
            case R.id.action_add:
                createComment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private SimpleTextWatcher textWatcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            updateAddButton();
        }
    };

    @Override
    public void onSuccess(Integer data) {
        showProgressBar(false);
        Intent intent = new Intent();
        ReplyModel reply = new ReplyModel();
        reply.content = reply.contentRendered = mContent.getText().toString();
        reply.created = System.currentTimeMillis() / 1000;
        reply.member = AccountUtils.readLoginMember(this);
        intent.putExtra("reply_result", (Parcelable) reply);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSuccess(Integer data, int total, int current) {
    }

    @Override
    public void onFailure(String error) {
        showProgressBar(false);
        MessageUtils.showErrorMessage(TopicCommentActivity.this, error);
    }

    private void createComment() {
        InputUtils.popSoftkeyboard(this, mContent, false);
        showProgressBar(R.string.topic_comment_working);
        V2EXManager.replyCreateWithTopicId(this, mTopicId,
                mContent.getText().toString(), this);
    }

    private void updateAddButton() {
        enableSendButton(!mContent.getText().toString().isEmpty());
    }

    private void enableSendButton(boolean enable) {
        if (mMenuAdd == null)
            return;

        if (enable) {
            mMenuAdd.setIcon(R.drawable.ic_menu_ok);
            mMenuAdd.setEnabled(true);
        } else {
            mMenuAdd.setIcon(R.drawable.ic_menu_ok_unable);
            mMenuAdd.setEnabled(false);
        }
    }
}
