package com.yaoyumeng.v2ex.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.TopicModel;
import com.yaoyumeng.v2ex.ui.widget.CustomDialog;
import com.yaoyumeng.v2ex.utils.InputUtils;
import com.yaoyumeng.v2ex.utils.MessageUtils;
import com.yaoyumeng.v2ex.utils.SimpleTextWatcher;

public class TopicAddActivity extends BaseActivity implements HttpRequestHandler<Integer> {

    EditText mTitle;
    EditText mContent;
    MenuItem mMenuAdd;
    NodeModel mNode;
    String mNodeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_add);
        mTitle = (EditText) findViewById(R.id.topic_add_title);
        mContent = (EditText) findViewById(R.id.topic_add_content);

        mTitle.addTextChangedListener(textWatcher);
        mContent.addTextChangedListener(textWatcher);

        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            mNode = (NodeModel) intent.getParcelableExtra("model");
            mNodeName = mNode.name;
        } else {
            mNodeName = intent.getStringExtra("node_name");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_topic_add, menu);

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
                createTopic();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(Integer data) {
        showProgressBar(false);

        Intent intent = new Intent();
        TopicModel topic = new TopicModel();
        topic.node = mNode;
        topic.title = mTitle.getText().toString();
        topic.content = topic.contentRendered = mContent.getText().toString();
        topic.created = System.currentTimeMillis() / 1000;
        topic.member = mLoginProfile;
        intent.putExtra("create_result", (Parcelable) topic);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onSuccess(Integer data, int total, int current) {
    }

    @Override
    public void onFailure(String error) {
        showProgressBar(false);
        MessageUtils.showErrorMessage(this, error);
    }

    @Override
    public void onBackPressed() {
        if (mTitle.getText().toString().isEmpty() &&
                mContent.getText().toString().isEmpty()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog dialog = builder.setTitle(R.string.title_activity_topic_add)
                    .setMessage(R.string.topic_add_quit_or_not)
                    .setPositiveButton(R.string.title_confirm_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.title_confirm_cancel, null).show();
            CustomDialog.dialogTitleLineColor(this, dialog);
        }
    }

    private SimpleTextWatcher textWatcher = new SimpleTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            updateAddButton();
        }
    };

    private void createTopic() {
        InputUtils.popSoftkeyboard(this, mContent, false);
        showProgressBar(R.string.topic_add_working);
        //onSuccess(200);
        V2EXManager.topicCreateWithNodeName(this, mNodeName, mTitle.getText().toString(),
                mContent.getText().toString(), this);
    }

    private void updateAddButton() {
        enableSendButton(!mTitle.getText().toString().isEmpty());
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
