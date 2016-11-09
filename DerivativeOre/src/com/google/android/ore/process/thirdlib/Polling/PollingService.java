package com.google.android.ore.process.thirdlib.Polling;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.ore.OreShowStrategyNew;

public class PollingService extends Service {

	public static final String ACTION = "com.google.android.ore.polling";
	OreShowStrategyNew mOreShowStrategyNew;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mOreShowStrategyNew = new OreShowStrategyNew();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		new PollingThread().start();
	}

	class PollingThread extends Thread {
		@Override
		public void run() {
			System.out.println("Polling...");
			mOreShowStrategyNew.show(PollingService.this);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("Service:onDestroy");
	}

}
