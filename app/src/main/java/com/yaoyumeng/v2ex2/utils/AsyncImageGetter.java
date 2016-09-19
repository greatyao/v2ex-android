package com.yaoyumeng.v2ex2.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.utils.Html;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by yw on 2015/4/25.
 */
public class AsyncImageGetter implements Html.ImageGetter {

    private Context mContext;
    private TextView mContainer;
    private Drawable mDefaultDrawable;
    private int mMaxWidth;

    public AsyncImageGetter(Context context, TextView container){
        mContext = context;
        mContainer = container;
        mMaxWidth = ScreenUtils.getDisplayWidth(mContext) -  ScreenUtils.dp(mContext, 100);
        mDefaultDrawable = context.getResources().getDrawable(R.drawable.ic_launcher);
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable urlDrawable = new URLDrawable();
        ImageLoader.getInstance().loadImage(source, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap != null) {
                    int width;
                    int height;
                    if (bitmap.getWidth() > mMaxWidth) {
                        width = mMaxWidth;
                        height = mMaxWidth * bitmap.getHeight() / bitmap.getWidth();
                    } else {
                        width = bitmap.getWidth();
                        height = bitmap.getHeight();
                    }
                    Drawable drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                    drawable.setBounds(0, 0, width, height);
                    urlDrawable.setBounds(0, 0, width, height);
                    urlDrawable.mDrawable = drawable;
                    //reset text to invalidate.
                    mContainer.setText(mContainer.getText());
                }
            }
        });
        return urlDrawable;
    }

    public class URLDrawable extends BitmapDrawable{

        protected Drawable mDrawable;

        @Override
        public void draw(Canvas canvas) {
            if(mDrawable != null){
                mDrawable.draw(canvas);
            }else{
                mDefaultDrawable.draw(canvas);
            }
        }
    }
}
