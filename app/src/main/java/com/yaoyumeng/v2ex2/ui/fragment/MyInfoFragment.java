package com.yaoyumeng.v2ex2.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.model.ProfileModel;
import com.yaoyumeng.v2ex2.ui.LoginActivity;
import com.yaoyumeng.v2ex2.ui.MyInfoActivity;
import com.yaoyumeng.v2ex2.ui.SettingsActivity;
import com.yaoyumeng.v2ex2.ui.UserActivity;
import com.yaoyumeng.v2ex2.utils.AccountUtils;
import com.yaoyumeng.v2ex2.utils.MessageUtils;

/**
 * Created by yw on 2015/6/11.
 */
public class MyInfoFragment extends BaseFragment implements View.OnClickListener {
    private ImageView mUserAvatar;
    private TextView mUserNickname;
    private BadgeView mNotifyCount;
    private TextView mTopicCount;
    private TextView mNodeCount;
    private TextView mFollowingCount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_info, container, false);

        View userLayout = rootView.findViewById(R.id.user_layout);
        mUserAvatar = (ImageView) rootView.findViewById(R.id.user_avatar);
        mUserNickname = (TextView) rootView.findViewById(R.id.user_nickname);

        View nodesFavorite = rootView.findViewById(R.id.nodesFavorite);
        mNodeCount = (TextView) rootView.findViewById(R.id.countOfNodes);
        View topicsFavorite = rootView.findViewById(R.id.topicsFavorite);
        mTopicCount = (TextView) rootView.findViewById(R.id.countOfTopics);
        View myFollowing = rootView.findViewById(R.id.myFollowing);
        mFollowingCount = (TextView) rootView.findViewById(R.id.countOfFollowing);
        View notifyLayout = rootView.findViewById(R.id.notificationLayout);
        mNotifyCount = (BadgeView) rootView.findViewById(R.id.badgeOfNotification);
        View settings = rootView.findViewById(R.id.settings);
        View checkIn = rootView.findViewById(R.id.checkIn);

        mNodeCount.setVisibility(View.INVISIBLE);
        mTopicCount.setVisibility(View.INVISIBLE);
        mFollowingCount.setVisibility(View.INVISIBLE);
        mNotifyCount.setVisibility(View.INVISIBLE);
        userLayout.setOnClickListener(this);
        nodesFavorite.setOnClickListener(this);
        topicsFavorite.setOnClickListener(this);
        myFollowing.setOnClickListener(this);
        notifyLayout.setOnClickListener(this);
        settings.setOnClickListener(this);
        checkIn.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mIsLogin) {
            updateProfileUi(mLoginProfile);
            
            refreshProfile();
        }
    }

    @Override
    public void onLogin(ProfileModel profile) {
        super.onLogin(profile);

        updateProfileUi(profile);
        AccountUtils.refreshFavoriteNodes(getActivity(), null);
    }

    @Override
    public void onLogout() {
        super.onLogout();
        mUserNickname.setText(R.string.login_please_login);
        mUserNickname.setTextColor(getResources().getColor(R.color.gray_b2));
        mUserAvatar.setImageResource(R.drawable.ic_avatar);
        mNotifyCount.setVisibility(View.INVISIBLE);
        mNodeCount.setVisibility(View.INVISIBLE);
        mTopicCount.setVisibility(View.INVISIBLE);
        mFollowingCount.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_layout:
                if (!willLaunchLoginActivity()) {
                    Intent intent = new Intent(getActivity(), UserActivity.class);
                    intent.putExtra("username", mLoginProfile.username);
                    startActivity(intent);
                }
                break;
            case R.id.nodesFavorite:
                if (!willLaunchLoginActivity()) {
                    Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                    intent.putExtra("type", MyInfoActivity.TypeMyNodesFavorite);
                    startActivity(intent);
                }
                break;
            case R.id.topicsFavorite:
                if (!willLaunchLoginActivity()) {
                    Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                    intent.putExtra("type", MyInfoActivity.TypeMyTopicsFavorite);
                    startActivity(intent);
                }
                break;
            case R.id.myFollowing:
                if (!willLaunchLoginActivity()) {
                    Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                    intent.putExtra("type", MyInfoActivity.TypeMyFollowings);
                    startActivity(intent);
                }
                break;
            case R.id.notificationLayout:
                if (!willLaunchLoginActivity()) {
                    //将未读消息清零,并持久化
                    mNotifyCount.setVisibility(View.INVISIBLE);
                    mLoginProfile.notifications = 0;
                    AccountUtils.writeLoginMember(getActivity(), mLoginProfile, false);

                    //显示未读消息
                    Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                    intent.putExtra("type", MyInfoActivity.TypeMyNotifications);
                    startActivity(intent);
                }
                break;
            case R.id.settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.checkIn:
                if (!willLaunchLoginActivity()) {
                    checkIn();
                }
                break;
        }
    }

    private boolean willLaunchLoginActivity() {
        if (!mIsLogin) {
            Intent intent2 = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent2);
            return true;
        } else {
            return false;
        }
    }

    private void updateProfileUi(ProfileModel profile) {
        mUserNickname.setText(profile.username);
        mUserNickname.setTextColor(getResources().getColor(R.color.gray_22));
        ImageLoader.getInstance().displayImage(profile.avatar, mUserAvatar);

        if (profile.notifications == 0) {
            mNotifyCount.setVisibility(View.INVISIBLE);
        } else {
            mNotifyCount.setText(String.format("%d", profile.notifications));
            mNotifyCount.setVisibility(View.VISIBLE);
        }

        mNodeCount.setText(String.format("%d", profile.nodes));
        mNodeCount.setVisibility(View.VISIBLE);

        mTopicCount.setText(String.format("%d", profile.topics));
        mTopicCount.setVisibility(View.VISIBLE);

        mFollowingCount.setText(String.format("%d", profile.followings));
        mFollowingCount.setVisibility(View.VISIBLE);
    }

    //刷新用户资料:包括节点收藏,话题收藏等
    private void refreshProfile() {
        AccountUtils.refreshProfile(getActivity());
    }

    private void checkIn() {
        V2EXManager.dailyCheckIn(getActivity(), new HttpRequestHandler<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                MessageUtils.showToast(getActivity(), "签到成功");
            }

            @Override
            public void onSuccess(Integer data, int totalPages, int currentPage) {

            }

            @Override
            public void onFailure(String error) {
                MessageUtils.showToast(getActivity(), error);
            }
        });
    }
}
