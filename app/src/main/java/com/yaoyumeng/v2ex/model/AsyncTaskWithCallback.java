package com.yaoyumeng.v2ex.model;

import android.os.AsyncTask;

/**
 *
 * Created by erichua on 6/5/15.
 */
public class AsyncTaskWithCallback extends AsyncTask<Object, Object, Object> {
    private Callback callback;
    private Runnable runnable;

    public interface Callback {
        void onFinish();
    }

    public AsyncTaskWithCallback(Runnable runnable, Callback callback) {
        this.runnable = runnable;
        this.callback = callback;
    }

    @Override
    protected Object doInBackground(Object... params) {
        if (null != this.runnable) {
            this.runnable.run();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (null != this.callback) {
            this.callback.onFinish();
        }
    }
}
