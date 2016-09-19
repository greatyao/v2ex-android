package com.yaoyumeng.v2ex2.api;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.yaoyumeng.v2ex2.model.PersistenceHelper;
import com.yaoyumeng.v2ex2.model.V2EXModel;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yw on 2015/5/2.
 */
class WrappedJsonHttpResponseHandler<T extends V2EXModel> extends JsonHttpResponseHandler {
    HttpRequestHandler<ArrayList<T>> handler;
    Class c;
    Context context;
    String key;

    public WrappedJsonHttpResponseHandler(Context cxt, Class c, String key,
                                          HttpRequestHandler<ArrayList<T>> handler) {
        this.handler = handler;
        this.c = c;
        this.context = cxt;
        this.key = key;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        ArrayList<T> models = new ArrayList<T>();
        try {
            T obj = (T) Class.forName(c.getName()).newInstance();
            obj.parse(response);
            if (obj != null)
                models.add(obj);
        } catch (Exception e) {
        }
        PersistenceHelper.saveModelList(context, models, key);
        SafeHandler.onSuccess(handler, models);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        ArrayList<T> models = new ArrayList<T>();
        for (int i = 0; i < response.length(); i++) {
            try {
                JSONObject jsonObj = response.getJSONObject(i);
                T obj = (T) Class.forName(c.getName()).newInstance();
                obj.parse(jsonObj);
                if (obj != null)
                    models.add(obj);
            } catch (Exception e) {
            }
        }
        PersistenceHelper.saveModelList(context, models, key);
        SafeHandler.onSuccess(handler, models);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
        handleFailure(statusCode, e.getMessage());
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
        handleFailure(statusCode, e.getMessage());
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
        handleFailure(statusCode, e.getMessage());
    }

    private void handleFailure(int statusCode, String error) {
        error = V2EXErrorType.errorMessage(context, V2EXErrorType.ErrorApiForbidden);
        SafeHandler.onFailure(handler, error);
    }
}

