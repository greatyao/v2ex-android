package com.yaoyumeng.v2ex2.ui.photo;

import android.support.v4.app.Fragment;

public interface PhotoViewCallbacks {

    /**
     * Listener to be invoked for screen events.
     */
    public static interface OnScreenListener {

        /**
         * A new view has been activated and the previous view de-activated.
         */
        public void onViewActivated();

        /**
         * Called when a right-to-left touch move intercept is about to occur.
         *
         * @param origX the raw x coordinate of the initial touch
         * @param origY the raw y coordinate of the initial touch
         * @return {@code true} if the touch should be intercepted.
         */
        public boolean onInterceptMoveLeft(float origX, float origY);

        /**
         * Called when a left-to-right touch move intercept is about to occur.
         *
         * @param origX the raw x coordinate of the initial touch
         * @param origY the raw y coordinate of the initial touch
         * @return {@code true} if the touch should be intercepted.
         */
        public boolean onInterceptMoveRight(float origX, float origY);
    }

    public void addScreenListener(int position, OnScreenListener listener);

    public void removeScreenListener(int position);

    public boolean isFragmentActive(Fragment fragment);
}
