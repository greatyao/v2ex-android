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
import java.util.Properties;

public class Application extends android.app.Application{

    private static Application mContext;
    private static int sMemoryClass;
    private DatabaseHelper mDatabaseHelper;
    private static V2EXDataSource mDataSource;

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

    private void initAppConfig() {
        final ActivityManager mgr = (ActivityManager) getApplicationContext().
                getSystemService(Activity.ACTIVITY_SERVICE);
        sMemoryClass = mgr.getMemoryClass();
    }

    private void initDatabase(){
        mDatabaseHelper = DatabaseHelper.getInstance(getApplicationContext());
        mDataSource = new V2EXDataSource(mDatabaseHelper);
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

    /**
     * 3G网络下是否加载显示文章图片
     * @return
     */
    public boolean isLoadImageInMobileNetwork()
    {
        String perf_loadimage = getProperty(AppConfig.CONF_NOIMAGE_NOWIFI);
        if(perf_loadimage == null || perf_loadimage.isEmpty())
            return false;
        else
            return Boolean.parseBoolean(perf_loadimage);
    }

    /**
     * 设置3G网络下是否加载文章图片
     * @param b
     */
    public void setConfigLoadImageInMobileNetwork(boolean b)
    {
        setProperty(AppConfig.CONF_NOIMAGE_NOWIFI, String.valueOf(b));
    }

    /**
     * 是否发出提示音
     * @return
     */
    public boolean isVoice()
    {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        //默认是开启提示声音
        if(perf_voice == null || perf_voice.isEmpty())
            return true;
        else
            return Boolean.parseBoolean(perf_voice);
    }

    /**
     * 设置是否发出提示音
     * @param b
     */
    public void setConfigVoice(boolean b)
    {
        setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
    }

    /**
     * 是否Https登录
     * @return
     */
    public boolean isHttps()
    {
        String perf_https = getProperty(AppConfig.CONF_USE_HTTPS);
        if(perf_https == null || perf_https.isEmpty())
            return true;
        else
            return Boolean.parseBoolean(perf_https);
    }

    /**
     * 设置是是否Https访问
     * @param b
     */
    public void setConfigHttps(boolean b)
    {
        setProperty(AppConfig.CONF_USE_HTTPS, String.valueOf(b));
    }

    public boolean containsProperty(String key){
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps){
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties(){
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key,String value){
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key){
        return AppConfig.getAppConfig(this).get(key);
    }
    public void removeProperty(String...key){
        AppConfig.getAppConfig(this).remove(key);
    }
}
