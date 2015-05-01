package com.yaoyumeng.v2ex.utils;

import android.content.Context;

import com.yaoyumeng.v2ex.Application;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.model.MemberModel;
import com.yaoyumeng.v2ex.model.NodeModel;
import com.yaoyumeng.v2ex.model.PersistenceHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * 登录帐号管理Created by yw on 2015/5/5.
 */
public class AccountUtils {

    public static final int REQUEST_LOGIN = 0;

    private static final String key_login_member = "logined@member";
    private static final String key_fav_nodes = "logined@fav_nodes";

    /**
     * 帐号登陆登出监听接口
     */
    public static interface OAccountListener{
        abstract public void onLogout();

        abstract public void onLogin(MemberModel member);
    }

    private static HashSet<OAccountListener> listeners = new HashSet<OAccountListener>();

    /**
     * 注册登录接口
     * @param listener
     */
    public static void registerAccountListener(OAccountListener listener){
        listeners.add(listener);
    }

    /**
     * 取消登录接口的注册
     * @param listener
     */
    public static void unregisterAccountListener(OAccountListener listener){
        listeners.remove(listener);
    }

    /**
     * 用户是否已经登录
     * @param cxt
     * @return
     */
    public static boolean isLogined(Context cxt) {
        return FileUtils.isExistDataCache(cxt, key_login_member);
    }

    /**
     * 保存登录用户资料
     * @param cxt
     * @param profile
     */
    public static void writeLoginMember(Context cxt, MemberModel profile) {
        PersistenceHelper.saveModel(cxt, profile, key_login_member);

        //刷新用户收藏节点资料
        refreshFavoriteNodes(cxt);

        //通知所有页面,登录成功,更新用户信息
        Iterator<OAccountListener> iterator = listeners.iterator();
        while(iterator.hasNext()){
            OAccountListener listener = iterator.next();
            listener.onLogin(profile);
        }
    }

    /**
     * 获取登录用户信息
     * @param cxt
     * @return
     */
    public static MemberModel readLoginMember(Context cxt) {
        return PersistenceHelper.loadModel(cxt, key_login_member);
    }

    /**
     * 删除登录用户资料
     * @param cxt
     */
    public static void removeLoginMember(Context cxt) {
        File data = cxt.getFileStreamPath(key_login_member);
        data.delete();
    }

    /**
     * 保存节点收藏信息
     * @param cxt
     * @param nodes
     */
    public static void writeFavoriteNodes(Context cxt, ArrayList<NodeModel> nodes) {
        PersistenceHelper.saveObject(cxt, nodes, key_fav_nodes);
        for(NodeModel node : nodes){
            Application.getDataSource().favoriteNode(node.name, true);
        }
    }

    /**
     * 获取收藏节点信息
     * @param cxt
     * @return
     */
    public static ArrayList<NodeModel> readFavoriteNodes(Context cxt) {
        return (ArrayList<NodeModel>) PersistenceHelper.loadObject(cxt, key_fav_nodes);
    }


    /**
     * 删除节点信息
     * @param cxt
     */
    public static void removeFavNodes(Context cxt) {
        File data = cxt.getFileStreamPath(key_fav_nodes);
        data.delete();
    }

    /**
     * 清除所有用户相关资料
     * @param cxt
     */
    public static void removeAll(Context cxt) {
        removeLoginMember(cxt);
        removeFavNodes(cxt);

        //通知所有页面退出登录了,清除登录痕迹
        Iterator<OAccountListener> iterator = listeners.iterator();
        while(iterator.hasNext()){
            OAccountListener listener = iterator.next();
            listener.onLogout();
        }
    }

    static class FavNodesHelper implements V2EXManager.HttpRequestHandler<ArrayList<NodeModel>>{
        Context mContext;
        FavNodesHelper(Context cxt){
            mContext = cxt;
        }

        @Override
        public void onSuccess(ArrayList<NodeModel> data){
            AccountUtils.writeFavoriteNodes(mContext, data);
        }

        @Override
        public void onFailure(int reason, String error){
        }
    }

    /**
     * 刷新用户收藏节点
     * @param cxt
     * @param handler
     */
    public static void refreshFavoriteNodes(Context cxt, FavNodesHelper handler){
        if(handler == null)
            handler = new FavNodesHelper(cxt);
        V2EXManager.getFavoriteNodes(cxt, handler);
    }

    /**
     * 刷新用户收藏节点
     * @param cxt
     */
    public static void refreshFavoriteNodes(Context cxt){
        refreshFavoriteNodes(cxt, null);
    }

    /**
     * 刷新用户的未读提醒
     * @param cxt
     */
    public static void refreshNotifications(Context cxt){
        //V2EXManager.getNotifications(cxt);
    }
}
