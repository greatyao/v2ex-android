package com.yaoyumeng.v2ex2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;
import com.yaoyumeng.v2ex2.ui.widget.RichTextView;
import com.yaoyumeng.v2ex2.utils.MessageUtils;
import com.yaoyumeng.v2ex2.utils.PhoneUtils;


public class AboutActivity extends SwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = (TextView) findViewById(R.id.version);
        version.setText("版本: " + PhoneUtils.getPackageInfo(this).versionName);

        final RelativeLayout markV2EX = (RelativeLayout) findViewById(R.id.markV2EX);
        markV2EX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markV2EX();
            }
        });

        final RelativeLayout checkUpdate = (RelativeLayout) findViewById(R.id.checkUpdate);
        checkUpdate.setVisibility(View.GONE);
        /*checkUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdate();
            }
        });*/

        final RichTextView author = (RichTextView) findViewById(R.id.v2ex);
        author.setRichText("By <a href=\"http://www.v2ex.com/member/njustyw\">@njustyw</a>");
    }

    void markV2EX() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            MessageUtils.showToast(this, "软件市场里暂时没有找到V2EX");
        }
    }
    /*
    void checkUpdate() {
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
            @Override
            public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                switch (updateStatus) {
                    case UpdateStatus.Yes: // has update
                        UmengUpdateAgent.showUpdateDialog(AboutActivity.this, updateInfo);
                        break;
                    case UpdateStatus.No: // has no update
                        MessageUtils.showToast(AboutActivity.this, "已经是最新版本");
                        break;
                    case UpdateStatus.NoneWifi: // none wifi
                        MessageUtils.showToast(AboutActivity.this, "没有wifi连接， 只在wifi下更新");
                        break;
                    case UpdateStatus.Timeout: // time out
                        MessageUtils.showToast(AboutActivity.this, "超时");
                        break;
                }
            }
        });
        UmengUpdateAgent.update(this);
    }*/
}
