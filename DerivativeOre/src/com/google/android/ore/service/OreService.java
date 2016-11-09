package com.google.android.ore.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.ore.OreManager;
import com.google.android.ore.activity.OreWebActivity;
import com.google.android.ore.thinkandroid.L;

public class OreService extends Service {
	public static final String ACTION = "com.google.android.ore.polling";
	private static final String TAG = OreService.class.getSimpleName();
	private OreManager mOreManager;
	private boolean isRunning = false;

	@Override
	public void onCreate() {
		super.onCreate();
		L.d(TAG, "onCreate");
		mOreManager = new OreManager();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		L.d(TAG, "onStartCommand");
		if (!isRunning) {
			startForground();
		}
		mOreManager.doTask(OreService.this);
		doSomeThingForThird();
		return START_STICKY;
	}

	protected void doSomeThingForThird() {

	}

	@Override
	public void onDestroy() {
		L.d(TAG, "onDestroy");
		mOreManager = null;
		stopForground();
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	private void startForground() {
		isRunning = true;
		android.app.Notification note = new android.app.Notification(
				android.R.drawable.stat_notify_chat, "",
				System.currentTimeMillis());
		Intent i = new Intent(this, OreWebActivity.class);

		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

		note.setLatestEventInfo(OreService.this, "", "", pi);
		note.flags |= Notification.FLAG_NO_CLEAR;

		startForeground(0, note);
	}

	private void stopForground() {
		isRunning = false;
		stopForeground(true);
	}

}
