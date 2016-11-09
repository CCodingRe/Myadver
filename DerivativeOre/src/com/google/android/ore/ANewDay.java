package com.google.android.ore;

import java.io.File;

import android.content.Context;

import com.google.android.ore.db.DatabaseHelper;
import com.google.android.ore.util.FileUtils;
import com.google.android.ore.util.P;

public class ANewDay {
	public static Boolean isDeleting = false;
	public static boolean check(Context context) {
		boolean isANewDay = P.getBoolean(false, P.IS_A_NEW_DAY, false);
		if (isANewDay) {
			P.putBoolean(false, P.IS_A_NEW_DAY, false);
			allReturnToZero(context);
		}
		return isANewDay;
	}
	public static void syncAllReturnToZero(final Context context) {
		isDeleting = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				allReturnToZero(context);
				isDeleting = false;
			}
		}).start();
	}

	// 一切归零
	public static void allReturnToZero(Context context) {
		//清空DB
		DatabaseHelper.get().dropAllTable();
		DatabaseHelper.get().createAllTable();
		//清空上一天的SharedPreferences
		P.clearLastDaySharedPreferences();
		//清空cache文件
		File cacheDir = Constant.getDir("");
		if (null != cacheDir && cacheDir.exists()) {
			FileUtils.clearDirectory(cacheDir.getAbsolutePath());
		}
	}
}
