package com.yaoyumeng.v2ex.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.R;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.ui.AboutActivity;
import com.yaoyumeng.v2ex.utils.AccountUtils;
import com.yaoyumeng.v2ex.utils.FileUtils;

/**
 * 设置
 * Created by yw on 2015/5/13.
 */
public class SettingsFragment extends PreferenceFragment {

    public static final String GITGUB_PROJECT = "https://github.com/greatyao/v2ex-android";
    SharedPreferences mPreferences;
    Preference mCache;
    Preference mAbout;
    Preference mCheckIn;
    CheckBoxPreference mHttps;
    CheckBoxPreference mEffect;
    CheckBoxPreference mLoadimage;
    CheckBoxPreference mJsonAPI;
    Button mLogout;
    Application mApp = Application.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        ViewGroup root = (ViewGroup) getView();
        ListView localListView = (ListView) root.findViewById(android.R.id.list);
        localListView.setBackgroundColor(0);
        localListView.setCacheColorHint(0);
        root.removeView(localListView);

        ViewGroup localViewGroup = (ViewGroup) LayoutInflater.from(getActivity())
                .inflate(R.layout.fragment_settings, null);
        ((ViewGroup) localViewGroup.findViewById(R.id.setting_content))
                .addView(localListView, -1, -1);
        localViewGroup.setVisibility(View.VISIBLE);
        root.addView(localViewGroup);

        //退出登录
        mLogout = (Button) localViewGroup.findViewById(R.id.setting_logout);
        boolean isLogin = AccountUtils.isLogined(getActivity());
        if (isLogin) {
            mLogout.setVisibility(View.VISIBLE);
        } else {
            mLogout.setVisibility(View.GONE);
        }
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                V2EXManager.logout(getActivity());
                AccountUtils.removeAll(getActivity());
                getActivity().finish();
            }
        });

        // https
        mHttps = (CheckBoxPreference) findPreference("pref_https");
        mHttps.setChecked(mApp.isHttps());
        mHttps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                mApp.setConfigHttps(mHttps.isChecked());
                return true;
            }
        });

        // 加载图片loadimage
        mLoadimage = (CheckBoxPreference) findPreference("pref_noimage_nowifi");
        mLoadimage.setChecked(!mApp.isLoadImageInMobileNetwork());
        mLoadimage.setSummary(mLoadimage.isChecked()
                ? R.string.settings_noimage_nowifi_summary
                : R.string.settings_image_nowifi_summary);
        mLoadimage.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                mApp.setConfigLoadImageInMobileNetwork(!mLoadimage.isChecked());
                mLoadimage.setSummary(mLoadimage.isChecked()
                        ? R.string.settings_noimage_nowifi_summary
                        : R.string.settings_image_nowifi_summary);
                return true;
            }
        });

        mJsonAPI = (CheckBoxPreference) findPreference("pref_jsonapi");
        mJsonAPI.setChecked(mApp.isJsonAPI());
        mJsonAPI.setSummary(getActivity().getString(mJsonAPI.isChecked()
                ? R.string.settings_use_jsonapi_summary
                : R.string.settings_use_browser_summary));
        mJsonAPI.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                mApp.setConfigJsonAPI(mJsonAPI.isChecked());
                mJsonAPI.setSummary(getActivity().getString(mJsonAPI.isChecked()
                        ? R.string.settings_use_jsonapi_summary
                        : R.string.settings_use_browser_summary));
                return true;
            }
        });

        // 动画效果
        mEffect = (CheckBoxPreference) findPreference("pref_effect");
        mEffect.setChecked(mApp.isShowEffect());
        mEffect.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                mApp.setConfigEffect(mEffect.isChecked());
                return true;
            }
        });

        // 清除缓存
        mCache = findPreference("pref_cache");
        mCache.setSummary(FileUtils.getFileSize(FileUtils.getCacheSize(getActivity())));
        mCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                FileUtils.clearAppCache(getActivity());
                mCache.setSummary("0KB");
                return true;
            }
        });

        // 关于我们
        mAbout = findPreference("pref_about");
        mAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                showAboutMe();
                return true;
            }
        });
    }

    private void showAboutMe() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }
}
