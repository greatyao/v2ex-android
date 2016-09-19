package com.yaoyumeng.v2ex2.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;
import com.yaoyumeng.v2ex2.model.ProfileModel;
import com.yaoyumeng.v2ex2.ui.BaseActivity;
import com.yaoyumeng.v2ex2.ui.widget.FootUpdate;
import com.yaoyumeng.v2ex2.utils.AccountUtils;

/**
 * Created by yw on 2015/5/3.
 */
public class BaseFragment extends Fragment implements AccountUtils.OnAccountListener {

    protected boolean mIsLogin;
    protected ProfileModel mLoginProfile;
    protected BackHandledInterface mBackHandledInterface;

    protected FootUpdate mFootUpdate = new FootUpdate();

    public static interface BackHandledInterface {
        public abstract void setSelectedFragment(BaseFragment selectedFragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mIsLogin = AccountUtils.isLogined(getActivity());
        if (mIsLogin)
            mLoginProfile = AccountUtils.readLoginMember(getActivity());
        AccountUtils.registerAccountListener(this);

        mBackHandledInterface = (BackHandledInterface) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        //告诉FragmentActivity，当前Fragment在栈顶
        mBackHandledInterface.setSelectedFragment(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(this.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(this.toString());
    }

    @Override
    public void onDestroy() {
        AccountUtils.unregisterAccountListener(this);
        super.onDestroy();
    }

    @Override
    public void onLogout() {
        mIsLogin = false;
    }

    @Override
    public void onLogin(ProfileModel profile) {
        mIsLogin = true;
        mLoginProfile = profile;
    }

    public boolean onBackPressed() {
        return false;
    }


    final public BaseActivity getBaseActivity() {
        return ((BaseActivity) super.getActivity());
    }

    final public void showProgress(int messageId) {
        getBaseActivity().showProgressBar(messageId);
    }

    final public void showProgress(boolean show) {
        getBaseActivity().showProgressBar(show);
    }
}
