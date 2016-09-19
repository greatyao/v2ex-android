package com.yaoyumeng.v2ex2.utils;

import android.content.Context;

import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.model.NodeModel;
import com.yaoyumeng.v2ex2.model.PersistenceHelper;
import com.yaoyumeng.v2ex2.model.ProfileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 登录帐号管理Created by yw on 2015/5/5.
 */
public class AccountUtils {

    public static final int REQUEST_LOGIN = 0;

    private static final String key_login_member = "logined@profile";
    private static final String key_fav_nodes = "logined@fav_nodes";

    /**
     * 帐号登陆登出监听接口
     */
    public static interface OnAccountListener {
        abstract public void onLogout();

        abstract public void onLogin(ProfileModel member);
    }

    private static HashSet<OnAccountListener> listeners = new HashSet<OnAccountListener>();

    /**
     * 注册登录接口
     *
     * @param listener
     */
    public static void registerAccountListener(OnAccountListener listener) {
        listeners.add(listener);
    }

    /**
     * 取消登录接口的注册
     *
     * @param listener
     */
    public static void unregisterAccountListener(OnAccountListener listener) {
        listeners.remove(listener);
    }

    /**
     * 用户是否已经登录
     *
     * @param cxt
     * @return
     */
    public static boolean isLogined(Context cxt) {
        return FileUtils.isExistDataCache(cxt, key_login_member);
    }

    /**
     * 保存登录用户资料
     *
     * @param cxt
     * @param profile
     */
    public static void writeLoginMember(Context cxt, ProfileModel profile, boolean broadcast) {
        PersistenceHelper.saveModel(cxt, profile, key_login_member);

        //通知所有页面,登录成功,更新用户信息
        if(broadcast) {
            for (OnAccountListener listener : listeners) {
                listener.onLogin(profile);
            }
        }
    }

    /**
     * 获取登录用户信息
     *
     * @param cxt
     * @return
     */
    public static ProfileModel readLoginMember(Context cxt) {
        return PersistenceHelper.loadModel(cxt, key_login_member);
    }

    /**
     * 删除登录用户资料
     *
     * @param cxt
     */
    public static void removeLoginMember(Context cxt) {
        File data = cxt.getFileStreamPath(key_login_member);
        data.delete();
    }

    /**
     * 保存节点收藏信息
     *
     * @param cxt
     * @param nodes
     */
    public static void writeFavoriteNodes(Context cxt, ArrayList<NodeModel> nodes) {
        PersistenceHelper.saveObject(cxt, nodes, key_fav_nodes);
        for (NodeModel node : nodes) {
            Application.getDataSource().favoriteNode(node.name, true);
        }
    }

    /**
     * 获取收藏节点信息
     *
     * @param cxt
     * @return
     */
    public static ArrayList<NodeModel> readFavoriteNodes(Context cxt) {
        return (ArrayList<NodeModel>) PersistenceHelper.loadObject(cxt, key_fav_nodes);
    }


    /**
     * 删除节点信息
     *
     * @param cxt
     */
    public static void removeFavNodes(Context cxt) {
        File data = cxt.getFileStreamPath(key_fav_nodes);
        data.delete();
    }

    /**
     * 清除所有用户相关资料
     *
     * @param cxt
     */
    public static void removeAll(Context cxt) {
        removeLoginMember(cxt);
        removeFavNodes(cxt);

        //通知所有页面退出登录了,清除登录痕迹
        for (OnAccountListener listener : listeners) {
            listener.onLogout();
        }
    }

    /**
     * 刷新登陆用户资料
     *
     * @param cxt
     */
    public static void refreshProfile(final Context cxt) {
        V2EXManager.getProfile(cxt, new HttpRequestHandler<ProfileModel>() {
            @Override
            public void onSuccess(ProfileModel data) {
                writeLoginMember(cxt, data, true);
            }

            @Override
            public void onSuccess(ProfileModel data, int totalPages, int currentPage) {
                onSuccess(data);
            }

            @Override
            public void onFailure(String error) {
            }
        }, true);
    }

    public static interface OnAccountFavoriteNodesListener {
        void onAccountFavoriteNodes(ArrayList<NodeModel> nodes);
    }

    /**
     * 刷新用户收藏节点
     *
     * @param cxt
     * @param listener
     */
    public static void refreshFavoriteNodes(final Context cxt,
                                            final OnAccountFavoriteNodesListener listener) {
        V2EXManager.getFavoriteNodes(cxt, new HttpRequestHandler<ArrayList<NodeModel>>() {
            @Override
            public void onSuccess(ArrayList<NodeModel> data) {
                AccountUtils.writeFavoriteNodes(cxt, data);
                if (listener != null)
                    listener.onAccountFavoriteNodes(data);
            }

            @Override
            public void onSuccess(ArrayList<NodeModel> nodes, int totalPages, int currentPage) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}
