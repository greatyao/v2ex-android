package com.yaoyumeng.v2ex.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yaoyumeng.v2ex.model.TopicModel;

/**
 * 类说明： 话题已读/未读,收藏/未收藏数据表，数据库帮助类
 * Created by yw on 2015/5/12.
 */
public class V2EXDataSource {

    private SQLiteDatabase database;

    final String TAG = "V2EXDataSource";

    private String[] allColumns = { DatabaseHelper.TOPIC_COLUMN_TOPICID,
            DatabaseHelper.TOPIC_COLUMN_READ,
            DatabaseHelper.TOPIC_COLUMN_FAVOR };

    public V2EXDataSource(DatabaseHelper dbHelper) {
        database = dbHelper.getWritableDatabase();
    }

    private void insert(TopicModel model, boolean read, boolean favor) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TOPIC_COLUMN_TOPICID, model.id);
        values.put(DatabaseHelper.TOPIC_COLUMN_FAVOR, favor ? 1 : 0);
        values.put(DatabaseHelper.TOPIC_COLUMN_READ, read ? 1 : 0);
        database.insert(DatabaseHelper.TOPIC_TABLE_NAME, null, values);
    }

    /**
     * 设置为已读
     */
    public boolean readTopic(TopicModel model) {

        int topicId = model.id;
        if (topicId == 0)
            return false;

        //数据项不存在,插入
        if(!isTopicExisted(topicId)){
            insert(model, true, false);
            return true;
        }

        boolean read = isTopicRead(topicId);
        if (read) return false;

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TOPIC_COLUMN_READ, 1);
        database.update(DatabaseHelper.TOPIC_TABLE_NAME, values,
                DatabaseHelper.TOPIC_COLUMN_TOPICID + " = " + topicId,
                null);
        return true;
    }

    /**
     * 设置收藏状态
     */
    public boolean favoriteTopic(TopicModel model, boolean favored) {

        int topicId = model.id;
        if (topicId == 0)
            return false;

        if (!isTopicExisted(topicId)) {
            insert(model, false, true);
            return true;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.TOPIC_COLUMN_FAVOR, favored ? 1 : 0);
        database.update(DatabaseHelper.TOPIC_TABLE_NAME, values,
                DatabaseHelper.TOPIC_COLUMN_TOPICID + " = " + topicId,
                null);
        return true;
    }

    /**
     * 话题是否已读
     */
    public boolean isTopicRead(int topicId) {
        return getTopicField(topicId, DatabaseHelper.TOPIC_COLUMN_READ) == 1;
    }

    /**
     * 话题是否已收藏
     */
    public boolean isTopicFavorite(int topicId) {
        return getTopicField(topicId, DatabaseHelper.TOPIC_COLUMN_FAVOR) == 1;
    }

    /**
     * 话题是否存在数据表中
     * @param topicId
     * @return
     */
    private boolean isTopicExisted(int topicId) {
        Cursor cursor = database.query(DatabaseHelper.TOPIC_TABLE_NAME, allColumns,
                DatabaseHelper.TOPIC_COLUMN_TOPICID + " = " + topicId, null,
                null, null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            return true;
        }

        return false;
    }

    /**
     * 获取状态
     * @param topicId
     * @param column
     * @return
     */
    private int getTopicField(int topicId, String column) {
        int result = 0;

        Cursor cursor = database.query(DatabaseHelper.TOPIC_TABLE_NAME, allColumns,
                DatabaseHelper.TOPIC_COLUMN_TOPICID + " = " + topicId, null,
                null, null, null);

        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            result = cursor.getInt(cursor.getColumnIndex(column));
        }

        cursor.close();
        return result;
    }
}
