/*
 * Copyright (C) 2011 Google Inc.
 * Licensed to The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yaoyumeng.v2ex2.ui.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.ui.photo.ImageUtils;
import com.yaoyumeng.v2ex2.ui.photo.PhotoView;
import com.yaoyumeng.v2ex2.ui.photo.PhotoViewCallbacks;

/**
 * Displays a photo.
 */
public class PhotoViewFragment extends BaseFragment implements
        OnClickListener, PhotoViewCallbacks.OnScreenListener {

    protected static ImageSize sTargetSize = new ImageSize(1024, 1024);
    protected static DisplayImageOptions sOptions = new DisplayImageOptions.Builder()
            .bitmapConfig(Bitmap.Config.RGB_565)
            .imageScaleType(ImageScaleType.EXACTLY)
            .cacheOnDisc(true)
            .displayer(new FadeInBitmapDisplayer(300))
            .build();

    public final static String ARG_POSITION = "arg-position";
    public static final String ARG_PHOTO_DOWNLOAD_URL = "arg-photo-download-url";

    /**
     * The size of the photo
     */
    public static Integer sPhotoSize;

    protected PhotoView mPhotoView;
    protected String mDownloadUrl;
    protected int mPosition;

    protected PhotoViewCallbacks mCallback;


    /**
     * Whether or not the progress bar is showing valid information about the progress stated
     */
    protected boolean mProgressBarNeeded = true;

    public static Fragment newInstance(String imageUrl, int position) {
        final Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        bundle.putString(ARG_PHOTO_DOWNLOAD_URL, imageUrl);
        final PhotoViewFragment fragment = new PhotoViewFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCallback = (PhotoViewCallbacks) getActivity();
        if (mCallback == null) {
            throw new IllegalArgumentException("Activity must be a derived class of PhotoViewActivity");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sPhotoSize == null) {
            final DisplayMetrics metrics = new DisplayMetrics();
            final WindowManager wm =
                    (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            final ImageUtils.ImageSize imageSize = ImageUtils.sUseImageSize;
            wm.getDefaultDisplay().getMetrics(metrics);
            switch (imageSize) {
                case EXTRA_SMALL:
                    // Use a photo that's 80% of the "small" size
                    sPhotoSize = (Math.min(metrics.heightPixels, metrics.widthPixels) * 800) / 1000;
                    break;
                case SMALL:
                    // Fall through.
                case NORMAL:
                    // Fall through.
                default:
                    sPhotoSize = Math.min(metrics.heightPixels, metrics.widthPixels);
                    break;
            }
        }

        final Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }

        mDownloadUrl = bundle.getString(ARG_PHOTO_DOWNLOAD_URL);
        mPosition = bundle.getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_photo_view, container, false);
        initializeView(view);
        return view;
    }

    protected void initializeView(View view) {
        mPhotoView = (PhotoView) view.findViewById(R.id.photo_view);
        mPhotoView.setMaxInitialScale(5);
        mPhotoView.setOnClickListener(this);
        mPhotoView.setFullScreen(false, false);
        mPhotoView.enableImageTransforms(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mCallback.addScreenListener(mPosition, this);

        if (!isPhotoBound()) {
            mProgressBarNeeded = true;
            startLoadBitmapTask();
        }
    }

    @Override
    public void onPause() {
        mCallback.removeScreenListener(mPosition);
        resetPhotoView();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        // Clean up views and other components
        if (mPhotoView != null) {
            mPhotoView.clear();
            mPhotoView = null;
        }
        super.onDestroyView();
    }

    private void startLoadBitmapTask() {

        ImageLoader.getInstance().loadImage(mDownloadUrl, sTargetSize, sOptions, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                displayPhoto(bitmap);
            }
        }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        android.util.Log.d("photoviewfragment", "update : " + current + "/" + total);
                    }
                });
    }

    private void displayPhoto(Bitmap data) {
        bindPhoto(data);
    }

    /**
     * Binds an image to the photo view.
     */
    private void bindPhoto(Bitmap bitmap) {
        if (bitmap != null) {
            if (mPhotoView != null) {
                mPhotoView.bindPhoto(bitmap);
            }
            enableImageTransforms(true);
        }
    }

    /**
     * Enable or disable image transformations. When transformations are enabled, this view
     * consumes all touch events.
     */
    public void enableImageTransforms(boolean enable) {
        if (mPhotoView != null) {
            mPhotoView.enableImageTransforms(enable);
        }
    }

    /**
     * Resets the photo view to it's default state w/ no bound photo.
     */
    private void resetPhotoView() {
        if (mPhotoView != null) {
            mPhotoView.bindPhoto(null);
        }
    }


    @Override
    public void onClick(View v) {
        //Image Click
    }

    /**
     * Reset the views to their default states
     */
    public void resetViews() {
        if (mPhotoView != null) {
            mPhotoView.resetTransformations();
        }
    }

    /**
     * Returns {@code true} if a photo has been bound. Otherwise, returns {@code false}.
     */
    public boolean isPhotoBound() {
        return (mPhotoView != null && mPhotoView.isPhotoBound());
    }

    @Override
    public void onViewActivated() {
        if (!mCallback.isFragmentActive(this)) {
            // we're not in the foreground; reset our view
            resetViews();
        } else {
            if (!isPhotoBound()) {
                startLoadBitmapTask();
            }
        }
    }

    @Override
    public boolean onInterceptMoveLeft(float origX, float origY) {
        if (!mCallback.isFragmentActive(this)) {
            // we're not in the foreground; don't intercept any touches
            return false;
        }
        return (mPhotoView != null && mPhotoView.interceptMoveLeft(origX, origY));
    }

    @Override
    public boolean onInterceptMoveRight(float origX, float origY) {
        if (!mCallback.isFragmentActive(this)) {
            // we're not in the foreground; don't intercept any touches
            return false;
        }
        return (mPhotoView != null && mPhotoView.interceptMoveRight(origX, origY));
    }
}
