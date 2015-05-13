package com.yaoyumeng.v2ex.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.utils.FileUtils;
import com.yaoyumeng.v2ex.utils.AccountUtils;
import com.yaoyumeng.v2ex.utils.PhoneUtils;

/**
 * Created by yw on 2015/5/7.
 */
public class SettingsActivity extends PreferenceActivity {

    public static final String GITGUB_PROJECT = "https://github.com/greatyao/v2ex-android";
    SharedPreferences mPreferences;
    Preference mCache;
    Preference mFeedback;
    Preference mUpdate;
    Preference mAbout;
    CheckBoxPreference mHttps;
    CheckBoxPreference mLoadimage;
    Button mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        ListView localListView = getListView();
        localListView.setBackgroundColor(0);
        localListView.setCacheColorHint(0);
        ((ViewGroup) localListView.getParent()).removeView(localListView);
        ViewGroup localViewGroup = (ViewGroup) getLayoutInflater().inflate(
                R.layout.activity_settings, null);
        ((ViewGroup) localViewGroup.findViewById(R.id.setting_content))
                .addView(localListView, -1, -1);
        mLogout = (Button)localViewGroup.findViewById(R.id.setting_logout);
        setContentView(localViewGroup);

        //退出登录
        if(AccountUtils.isLogined(this)){
            mLogout.setVisibility(View.VISIBLE);
        } else{
            mLogout.setVisibility(View.GONE);
        }
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                V2EXManager.logout(SettingsActivity.this);
                AccountUtils.logout(SettingsActivity.this);
                finish();
            }
        });

        // https
        mHttps = (CheckBoxPreference) findPreference("pref_https");
        mHttps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                V2EXManager.setHttps(mHttps.isChecked());
                return true;
            }
        });

        // 加载图片loadimage
        mLoadimage = (CheckBoxPreference) findPreference("pref_noimage_nowifi");
        mLoadimage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });

        // // 版本更新
        mUpdate = (Preference) findPreference("pref_check_update");
        mUpdate.setSummary("版本: " + PhoneUtils.getPackageInfo(this).versionName);
        mUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });

        // 清除缓存
        mCache = (Preference) findPreference("pref_cache");
        mCache.setSummary(FileUtils.getFileSize(FileUtils.getCacheSize(this)));
        mCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                FileUtils.clearAppCache(SettingsActivity.this);
                mCache.setSummary("0KB");
                return true;
            }
        });

        // 意见反馈
        mFeedback = (Preference) findPreference("pref_feedback");
        mFeedback.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, "软件市场里暂时没有找到V2EX", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

        });

        // 关于我们
        mAbout = (Preference) findPreference("pref_about");
        mAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showAboutMe();
                return true;
            }
        });
    }

    private void showAboutMe() {
        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_settings_dialog_aboutme);

        TextView textView = (TextView) dialog.findViewById(R.id.dialog_text);
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        String title = new StringBuilder().append(PhoneUtils.getApplicationName(this)).append("<br/>").toString();
        String subTitle = new StringBuilder().append(getString(R.string.app_sub_name)).append("<br/>").toString();
        String author = new StringBuilder().append("@").append(getString(R.string.app_author)).toString();
        String githubUrl = new StringBuilder().append("<a href='")
                .append(GITGUB_PROJECT)
                .append("'>")
                .append(GITGUB_PROJECT)
                .append("</a>")
                .append("<br/>")
                .toString();

        String data = getString(R.string.settings_aboutme_format);
        data = String.format(data, title, subTitle, githubUrl, author);
        CharSequence charSequence = Html.fromHtml(data);
        textView.setText(charSequence);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        dialog.show();
    }

}

