package com.google.android.ore;

import android.content.Context;
import android.content.Intent;

import com.google.android.ore.service.OreService;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.P;

public class StartApp {
	private static final String TAG = StartApp.class.getSimpleName();

	public static final long LOG_DURATION = 1 * 60 * 1000;

	public static void onReceive(Context context, Intent intent) {
		long last_polling_time_duration = System.currentTimeMillis() - P.getLong(true, P.LAST_POLLING_TIME, -1);
		if (OreApp.get().getDebug() || moreThanPollingDuration(last_polling_time_duration)) {
			L.d(TAG, "OreService startService");
			P.putLong(true, P.LAST_POLLING_TIME, System.currentTimeMillis());
			Intent intent1 = new Intent(context, OreService.class);
			context.startService(intent1);
		} else {
			long lastLogTime = P.getLong(true, P.LAST_LOG_TIME, 0l);
			if (System.currentTimeMillis() - lastLogTime > LOG_DURATION) {
				L.d(TAG, "do nothing  : "+last_polling_time_duration/(60*1000)+"分钟");
				P.putLong(true, P.LAST_LOG_TIME, System.currentTimeMillis());
			}
		}
	}

	public static boolean moreThanPollingDuration(long last_polling_time_duration) {
		if (last_polling_time_duration < 0) {
			last_polling_time_duration = 0;
			P.putLong(true, P.LAST_POLLING_TIME, System.currentTimeMillis());
			return false;
		}
		if (last_polling_time_duration > OreApp.get().getOreConfig().repeat_duration * 60 * 1000) {
			return true;
		}
		return false;
	}
}
