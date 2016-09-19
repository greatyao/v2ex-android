package com.yaoyumeng.v2ex2.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.yaoyumeng.v2ex2.Application;
import com.yaoyumeng.v2ex2.R;
import com.yaoyumeng.v2ex2.api.HttpRequestHandler;
import com.yaoyumeng.v2ex2.api.V2EXManager;
import com.yaoyumeng.v2ex2.broadcast.AlarmReceiver;
import com.yaoyumeng.v2ex2.model.ProfileModel;
import com.yaoyumeng.v2ex2.ui.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by yw on 2015/10/12.
 */
public class NoticeService extends Service {
    public static final String INTENT_ACTION_BROADCAST = "com.yaoyumeng.v2ex2.service.BROADCAST";
    public static final String INTENT_ACTION_SHUTDOWN = "com.yaoyumeng.v2ex2.service.SHUTDOWN";
    public static final String INTENT_ACTION_REQUEST = "com.yaoyumeng.v2ex2.service.REQUEST";
    public static final String INTENT_ACTION_NOTICE = "com.yaoyumeng.v2ex2.action.APPWIDGET_UPDATE";

    private static final long INTERVAL = 1000 * 120;
    private AlarmManager mAlarmMgr;
    private int mLastNoticeCount = 0;
    private String TAG = "NoticeService";

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "onReceive:" + action);
            if (INTENT_ACTION_NOTICE.equals(action)) {
                int count = intent.getIntExtra("notice_count", 0);
                if (count == 0) {
                    NotificationManagerCompat.from(NoticeService.this).cancel(
                            R.string.you_have_notifications);
                }
            } else if (INTENT_ACTION_BROADCAST.equals(action)) {
                if (mLastNoticeCount > 0) {
                    sendBroadCast(NoticeService.this, mLastNoticeCount);
                }
            } else if (INTENT_ACTION_SHUTDOWN.equals(action)) {
                stopSelf();
            } else if (INTENT_ACTION_REQUEST.equals(action)) {
                requestNotice();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mAlarmMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
        startRequestAlarm();
        //requestNotice();

        IntentFilter filter = new IntentFilter(INTENT_ACTION_BROADCAST);
        filter.addAction(INTENT_ACTION_SHUTDOWN);
        filter.addAction(INTENT_ACTION_REQUEST);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        cancelRequestAlarm();
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void startRequestAlarm() {
        cancelRequestAlarm();
        // 每隔2分钟执行
        mAlarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 2500, INTERVAL,
                getOperationIntent());
    }

    private void cancelRequestAlarm() {
        mAlarmMgr.cancel(getOperationIntent());
    }

    /**
     * 掌上V2EX采用轮询方式实现消息推送
     */
    private PendingIntent getOperationIntent() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent operation = PendingIntent.getBroadcast(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return operation;
    }

    private void requestNotice() {
        Log.i(TAG, "requestNotice");
        V2EXManager.getProfile(this, new HttpRequestHandler<ProfileModel>() {
            @Override
            public void onSuccess(ProfileModel data) {
                int count = data.notifications;
                sendBroadCast(NoticeService.this, count);
                if(Application.getInstance().isMessagePushFromCache())
                    notification(count);
            }

            @Override
            public void onSuccess(ProfileModel data, int totalPages, int currentPage) {
                onSuccess(data);
            }

            @Override
            public void onFailure(String error) {
            }

        }, true);
    }

    public static void sendBroadCast(Context context, int count) {
        Intent intent = new Intent(INTENT_ACTION_NOTICE);
        Bundle bundle = new Bundle();
        bundle.putInt("notice_count", count);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    private void notification(int count) {
        if (count == 0) {
            mLastNoticeCount = 0;
            NotificationManagerCompat.from(this).cancel(R.string.you_have_notifications);
            return;
        }
        if (count == mLastNoticeCount)
            return;

        mLastNoticeCount = count;

        Resources res = getResources();
        String contentTitle = res.getString(R.string.app_name);
        String contentText = res.getString(R.string.you_have_notifications, count);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NOTICE", true);

        PendingIntent pi = PendingIntent.getActivity(this, 1000, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setTicker(contentTitle).setContentTitle(contentTitle)
                .setContentText(contentText).setAutoCancel(true)
                .setContentIntent(pi).setSmallIcon(R.drawable.ic_notifications);
        builder.setSound(Uri.parse("android.resource://"
                + Application.getInstance().getPackageName() + "/"
                + R.raw.notificationsound));
        long[] vibrate = {0, 10, 20, 30};
        builder.setVibrate(vibrate);

        Notification notification = builder.build();
        NotificationManagerCompat.from(this).notify(
                R.string.you_have_notifications, notification);
    }

    private static class ServiceStub extends INoticeService.Stub {
        WeakReference<NoticeService> mService;

        ServiceStub(NoticeService service) {
            mService = new WeakReference<NoticeService>(service);
        }

        @Override
        public void scheduleNotice() throws RemoteException {
            mService.get().startRequestAlarm();
        }

        @Override
        public void requestNotice() throws RemoteException {
            mService.get().requestNotice();
        }
    }

    private final IBinder mBinder = new ServiceStub(this);

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
