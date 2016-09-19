package com.yaoyumeng.v2ex2.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.model.MemberModel;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.model.PersistenceHelper;
import com.yaoyumeng.v2ex2.model.TopicModel;
import com.yaoyumeng.v2ex2.ui.fragment.AllNodesFragment;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;
import com.yaoyumeng.v2ex2.ui.widget.CustomDialog;
import com.yaoyumeng.v2ex2.ui.widget.CustomSpinner;
import com.yaoyumeng.v2ex2.utils.InputUtils;
import com.yaoyumeng.v2ex2.utils.MessageUtils;
import com.yaoyumeng.v2ex2.utils.SimpleTextWatcher;

import java.util.ArrayList;

public class TopicAddActivity extends SwipeBackActivity implements HttpRequestHandler<Integer> {

    EditText mTitle;
    EditText mContent;
    MenuItem mMenuAdd;
    NodeModel mNode;
    String mNodeName;
    CustomSpinner mNodeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_add);

        mTitle = (EditText) findViewById(R.id.topic_add_title);
        mContent = (EditText) findViewById(R.id.topic_add_content);
        mNodeSpinner = (CustomSpinner) findViewById(R.id.topic_add_node);

        mTitle.addTextChangedListener(textWatcher);
        mContent.addTextChangedListener(textWatcher);

        Intent intent = getIntent();
        if (intent.hasExtra("model")) {
            mNode = intent.getParcelableExtra("model");
            if(mNode != null) {
                mNodeName = mNode.name;
                mNodeSpinner.setText(mNode.title);
            }
        } else {
            mNodeName = intent.getStringExtra("node_name");
        }

        showProgressBar(true, getString(R.string.topic_add_get_all_nodes));
        V2EXManager.getAllNodes(this, false, nodesRequester);
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
        topic.member = new MemberModel();
        topic.member.username = mLoginProfile.username;
        topic.member.avatar = mLoginProfile.avatar;
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
        if (mNodeSpinner.isShowPopup()){
            mNodeSpinner.dismiss();
        } else if (mTitle.getText().toString().isEmpty() &&
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

    private HttpRequestHandler<ArrayList<NodeModel>> nodesRequester = new HttpRequestHandler<ArrayList<NodeModel>>(){
        @Override
        public void onSuccess(ArrayList<NodeModel> data) {
            showProgressBar(false);
            ArrayAdapter<NodeModel> adapter = new ArrayAdapter(TopicAddActivity.this,
                    android.R.layout.select_dialog_item, data);
            mNodeSpinner.setAdapter(adapter);
            mNodeSpinner.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mNode = (NodeModel)parent.getItemAtPosition(position);
                    mNodeName = mNode.name;
                    mNodeSpinner.setText(mNode.title);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent){
                    mNodeSpinner.setText("");
                    mNode = null;
                    mNodeName = "";
                }
            });
        }

        @Override
        public void onSuccess(ArrayList<NodeModel> data, int totalPages, int currentPage) {
            onSuccess(data);
        }

        @Override
        public void onFailure(String error) {
            showProgressBar(false);
            MessageUtils.showErrorMessage(TopicAddActivity.this, error);
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
        enableSendButton(!mTitle.getText().toString().isEmpty() && !mNodeName.isEmpty());
    }

    private void enableSendButton(boolean enable) {
        if (mMenuAdd == null)
            return;

        mMenuAdd.setEnabled(enable);
    }
}
