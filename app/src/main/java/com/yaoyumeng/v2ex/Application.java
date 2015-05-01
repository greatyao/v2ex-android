package com.yaoyumeng.v2ex;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.yaoyumeng.v2ex.api.V2EXManager;
import com.yaoyumeng.v2ex.database.DatabaseHelper;
import com.yaoyumeng.v2ex.database.V2EXDataSource;
import com.yaoyumeng.v2ex.utils.AccountUtils;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;

public class Application extends android.app.Application{

    private static Application mContext;
    private static int sMemoryClass;
    public static boolean mHttps = false;
    private DatabaseHelper mDatabaseHelper;
    private static V2EXDataSource mDataSource;

    @Override
    public void onCreate() {
        super.onCreate();
        //MobclickAgent.openActivityDurationTrack(false);

        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());

        mContext = this;

        initDatabase();
        initiImageLoader();
        initAppConfig();
        initAccount();
    }

    //刷新用户资料:包括节点收藏,话题收藏等
    private void initAccount(){
        if(AccountUtils.isLogined(this)){
            AccountUtils.refreshFavoriteNodes(this);
            AccountUtils.refreshNotifications(this);
        }
    }

    private void initAppConfig() {
        final ActivityManager mgr = (ActivityManager) getApplicationContext().
                getSystemService(Activity.ACTIVITY_SERVICE);
        sMemoryClass = mgr.getMemoryClass();
        mHttps = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_https", false);
    }

    private void initDatabase(){
        mDatabaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDataSource = new V2EXDataSource(mDatabaseHelper);
    }

    private void initiImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.ic_avatar)
                .build();

        File cacheDir;
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }else{
            cacheDir = getCacheDir();
        }
        ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(options);
        if(BuildConfig.DEBUG){
            configBuilder.writeDebugLogs();
        }
        ImageLoader.getInstance().init(configBuilder.build());
    }

    public static Application getInstance(){
        return mContext;
    }

    public static V2EXDataSource getDataSource() {
        return mDataSource;
    }

    public int getMemorySize(){
        return sMemoryClass;
    }

    public static Context getContext(){
        return mContext;
    }
}
