package com.yaoyumeng.v2ex2.database;

/**
 * Created by yw on 2015/5/12.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public final class DatabaseHelper extends SQLiteOpenHelper {

    //db
    public static final String DB_NAME = "v2ex.db";
    public static final int DB_VERSION = 6;

    // 话题数据表(话题ID,收藏状态,阅读状态)
    public static final String TOPIC_TABLE_NAME = "topics_table";
    public static final String TOPIC_COLUMN_ID = "_id";
    public static final String TOPIC_COLUMN_TOPICID = "topic_id";
    public static final String TOPIC_COLUMN_FAVOR = "isfavored";
    public static final String TOPIC_COLUMN_READ = "isread";

    private static final String TOPIC_TABLE_CREATE = "CREATE TABLE " + TOPIC_TABLE_NAME
            + "(" + TOPIC_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TOPIC_COLUMN_TOPICID + " INTEGER UNIQUE NOT NULL, "
            + TOPIC_COLUMN_READ + " INTEGER NOT NULL, "
            + TOPIC_COLUMN_FAVOR + " INTEGER NOT NULL);";

    //节点数据表(节点ID,收藏状态)
    public static final String NODE_TABLE_NAME = "nodes_table";
    public static final String NODE_COLUMN_ID = "_id";
    public static final String NODE_COLUMN_NODENAME = "node_name";
    public static final String NODE_COLUMN_ISFAVOR = "isfavored";

    private static final String NODE_TABLE_CREATE = "CREATE TABLE " + NODE_TABLE_NAME
            + "(" + NODE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NODE_COLUMN_NODENAME + " CHAR(256) UNIQUE NOT NULL, "
            + NODE_COLUMN_ISFAVOR + " INTEGER NOT NULL);";

    private volatile static DatabaseHelper mDBHelper;

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (mDBHelper == null) {
            synchronized (DatabaseHelper.class) {
                if (mDBHelper == null) {
                    mDBHelper = new DatabaseHelper(context);
                }
            }
        }

        return mDBHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TOPIC_TABLE_CREATE);
        db.execSQL(NODE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TOPIC_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NODE_TABLE_NAME);

        onCreate(db);
    }
}
