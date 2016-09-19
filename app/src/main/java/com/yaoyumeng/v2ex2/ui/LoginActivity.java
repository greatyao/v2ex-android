package com.yaoyumeng.v2ex2.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.model.ProfileModel;
import com.yaoyumeng.v2ex2.ui.swipeback.SwipeBackActivity;
import com.yaoyumeng.v2ex2.utils.AccountUtils;
import com.yaoyumeng.v2ex2.utils.MessageUtils;

public class LoginActivity extends SwipeBackActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private EditText mUsername;
    private EditText mPassword;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.login_username_edit);
        mPassword = (EditText) findViewById(R.id.login_password_edit);
        mLogin = (Button) findViewById(R.id.login_login_btn);
        mLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                if (mUsername.getText().length() == 0) {
                    mUsername.setError(getString(R.string.login_error_empty_user));
                    mUsername.requestFocus();
                } else if (mPassword.getText().length() == 0) {
                    mPassword.setError(getString(R.string.login_error_empty_passwd));
                    mPassword.requestFocus();
                } else {
                    login();
                }
                break;
            case R.id.login_sign_up_txt:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(V2EXManager.SIGN_UP_URL)));
                break;
        }
    }

    private void login() {
        showProgressBar(true, getString(R.string.login_loging));

        V2EXManager.loginWithUsername(this,
                mUsername.getText().toString(),
                mPassword.getText().toString(),
                new HttpRequestHandler<Integer>() {
                    @Override
                    public void onSuccess(Integer data) {
                        getProfile();
                    }

                    @Override
                    public void onSuccess(Integer data, int totalPages, int currentPage) {
                        getProfile();
                    }

                    @Override
                    public void onFailure(String error) {
                        MessageUtils.showErrorMessage(LoginActivity.this, error);
                        showProgressBar(false);
                    }
                });
    }

    private HttpRequestHandler<ProfileModel> profileHandler =
            new HttpRequestHandler<ProfileModel>() {
                @Override
                public void onSuccess(ProfileModel data) {
                    showProgressBar(false);
                    mLoginProfile = data;
                    AccountUtils.writeLoginMember(LoginActivity.this, data, true);
                    showProgressBar(false);
                    Intent intent = new Intent();
                    intent.putExtra("profile", (Parcelable) data);
                    setResult(RESULT_OK, intent);
                    finish();
                }

                @Override
                public void onSuccess(ProfileModel data, int totalPages, int currentPage) {
                }

                @Override
                public void onFailure(String error) {
                    showProgressBar(false);
                    MessageUtils.showErrorMessage(LoginActivity.this, error);
                }
            };

    private void getProfile() {
        showProgressBar(true, getString(R.string.login_obtain_profile));
        V2EXManager.getProfile(this, profileHandler, false);
    }
}
