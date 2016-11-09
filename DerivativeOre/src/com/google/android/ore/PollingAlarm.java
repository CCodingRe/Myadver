package com.google.android.ore;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.DateUtil;

public class PollingAlarm {
	private static String TAG = PollingAlarm.class.getSimpleName();
	public static final long POLLING_DURATION = 1 * 60 * 1000;

	public static void sendOncePollingAlarm() {
		L.d(TAG, "sendOncePollingAlarm");
		AlarmManager alarms = (AlarmManager) OreApp.get().getSystemService(
				Context.ALARM_SERVICE);
		Intent intent = new Intent(OreApp.get().getPackageName());
		intent.putExtra(TAG , DateUtil.getCurDate_YMD());
		PendingIntent sendIntent = PendingIntent.getBroadcast(OreApp.get(),
				Integer.MAX_VALUE, intent, 0);
		alarms.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+POLLING_DURATION , sendIntent);
	}
}
