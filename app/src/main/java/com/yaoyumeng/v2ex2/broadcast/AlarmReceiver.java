package com.yaoyumeng.v2ex2.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yaoyumeng.v2ex2.service.NoticeService;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent(NoticeService.INTENT_ACTION_REQUEST));
	}
}
