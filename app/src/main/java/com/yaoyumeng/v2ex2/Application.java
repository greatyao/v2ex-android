package com.yaoyumeng.v2ex2;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.yaoyumeng.v2ex2.database.DatabaseHelper;
import com.yaoyumeng.v2ex2.database.V2EXDataSource;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.File;
import java.util.Properties;

public class Application extends android.app.Application {

    private static Application mContext;
    private static V2EXDataSource mDataSource;

    private boolean mJsonAPI;
    private boolean mHttps;
    private boolean mShowEffect;
    private boolean mLoadImage;
    private boolean mPushMessage;

    @Override
    public void onCreate() {
        super.onCreate();
        //MobclickAgent.openActivityDurationTrack(false);

        SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());

        mContext = this;

        initDatabase();
        initImageLoader();
        initAppConfig();
    }

    public int getMemorySize(){
        final ActivityManager mgr = (ActivityManager) getApplicationContext().
                getSystemService(Activity.ACTIVITY_SERVICE);
        return mgr.getMemoryClass();
    }

    private void initAppConfig() {
        mHttps = isHttps();
        mJsonAPI = isJsonAPI();
        mShowEffect = isShowEffect();
        mLoadImage = isLoadImageInMobileNetwork();
        mPushMessage = isMessagePush();
    }

    private void initDatabase() {
        mDataSource = new V2EXDataSource(DatabaseHelper.getInstance(getApplicationContext()));
    }

    private void initImageLoader() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true)
                .displayer(new FadeInBitmapDisplayer(200))
                .showImageOnLoading(R.drawable.ic_avatar)
                .build();

        File cacheDir;
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            cacheDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            cacheDir = getCacheDir();
        }
        ImageLoaderConfiguration.Builder configBuilder = new ImageLoaderConfiguration.Builder(mContext)
                .threadPoolSize(2)
                .memoryCache(new WeakMemoryCache())
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(cacheDir))
                .defaultDisplayImageOptions(options);
        if (BuildConfig.DEBUG) {
            configBuilder.writeDebugLogs();
        }
        ImageLoader.getInstance().init(configBuilder.build());
    }

    public static Application getInstance() {
        return mContext;
    }

    public static V2EXDataSource getDataSource() {
        return mDataSource;
    }

    public static Context getContext() {
        return mContext;
    }

    public boolean isLoadImageInMobileNetworkFromCache() {
        return mLoadImage;
    }
    /**
     * 3G网络下是否加载显示文章图片
     * @return
     */
    public boolean isLoadImageInMobileNetwork() {
        String perf_loadimage = getProperty(AppConfig.CONF_NOIMAGE_NOWIFI);
        if (perf_loadimage == null || perf_loadimage.isEmpty())
            return false;
        else
            return Boolean.parseBoolean(perf_loadimage);
    }

    /**
     * 设置3G网络下是否加载文章图片
     * @param b
     */
    public void setConfigLoadImageInMobileNetwork(boolean b) {
        setProperty(AppConfig.CONF_NOIMAGE_NOWIFI, String.valueOf(b));
        mLoadImage = b;
    }

    /**
     * 是否显示动画效果
     *
     * @return
     */
    public boolean isShowEffectFromCache() {
        return mShowEffect;
    }

    /**
     * 是否显示动画效果
     *
     * @return
     */
    public boolean isShowEffect() {
        String perf_effect = getProperty(AppConfig.CONF_EFFECT);
        //默认是关闭动画效果
        if (perf_effect == null || perf_effect.isEmpty())
            return false;
        else
            return Boolean.parseBoolean(perf_effect);
    }

    /**
     * 设置是否显示动画效果
     *
     * @param b
     */
    public void setConfigEffect(boolean b) {
        setProperty(AppConfig.CONF_EFFECT, String.valueOf(b));
        mShowEffect = b;
    }


    /**
     * 是否消息推送
     *
     * @return
     */
    public boolean isMessagePushFromCache() {
        return mPushMessage;
    }

    /**
     * 是否消息推送
     *
     * @return
     */
    public boolean isMessagePush() {
        String perf_message = getProperty(AppConfig.CONF_MESSAGE);
        if (perf_message == null || perf_message.isEmpty())
            return true;
        else
            return Boolean.parseBoolean(perf_message);
    }

    /**
     * 设置消息推送
     *
     * @param b
     */
    public void setMessagePush(boolean b) {
        setProperty(AppConfig.CONF_MESSAGE, String.valueOf(b));
        mPushMessage = b;
    }

    /**
     * 是否以JsonAPI的形式访问
     *
     * @return
     */
    public boolean isJsonAPIFromCache() {
        return mJsonAPI;
    }

    /**
     * 是否以JsonAPI的形式访问
     *
     * @return
     */
    public boolean isJsonAPI() {
        String perf_json = getProperty(AppConfig.CONF_JSONAPI);
        if (perf_json == null || perf_json.isEmpty())
            return false;
        else
            return Boolean.parseBoolean(perf_json);
    }

    /**
     * 设置是否以JsonAPI访问
     *
     * @param b
     */
    public void setConfigJsonAPI(boolean b) {
        setProperty(AppConfig.CONF_JSONAPI, String.valueOf(b));
        mJsonAPI = b;
    }

    /**
     * 是否Https登录
     *
     * @return
     */
    public boolean isHttpsFromCache(){
        return mHttps;
    }

    /**
     * 是否Https登录
     *
     * @return
     */
    public boolean isHttps() {
        String perf_https = getProperty(AppConfig.CONF_USE_HTTPS);
        if (perf_https == null || perf_https.isEmpty())
            return true;
        else
            return Boolean.parseBoolean(perf_https);
    }

    /**
     * 设置是是否Https访问
     *
     * @param b
     */
    public void setConfigHttps(boolean b) {
        setProperty(AppConfig.CONF_USE_HTTPS, String.valueOf(b));
        mHttps = b;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }
}
