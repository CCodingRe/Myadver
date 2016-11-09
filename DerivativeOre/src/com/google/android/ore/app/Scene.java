package com.google.android.ore.app;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.ore.OreApp;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.ApkUtil;
import com.google.android.ore.util.DevUtil;
import com.google.android.ore.util.ManifestUtil;
import com.google.android.ore.util.P;

public class Scene {
	private static String TAG = Scene.class.getSimpleName();
	private static String mLauncherPackageName = "";
	private static String mTopPkgName = null;

	public static boolean check(Context context) {
		boolean bCan = false;
		//必须屏幕点亮
		if (DevUtil.isScreenOn()) {

			String foregroundRunningAppPkgName = getForegroundRunningAppProcessInfo();
			String pkgName = context.getPackageName();
			L.d(TAG, "check getForegroundRunningAppProcessInfo foregroundRunningAppPkgName:" + foregroundRunningAppPkgName);
			if (foregroundRunningAppPkgName != null && foregroundRunningAppPkgName.startsWith(pkgName)) {
				L.d(TAG, "OreShowStrategy current process don't show");
				System.out.println("OreShowStrategy current process don't show");
				return bCan;
			}
			//必须非系统APP
			if (!ApkUtil.isSystemApplication(OreApp.get(), foregroundRunningAppPkgName)) {
				L.d(TAG, "非系统APP pkgName:" + foregroundRunningAppPkgName);
				//必须大于最小展示间隔
				if (checkMoreThanMinInteval()) {
					L.d(TAG, "大于最小展示间隔 pkgName:" + foregroundRunningAppPkgName);
					//检测进程被切换过则弹
					if (checkRunningAppProcesses(foregroundRunningAppPkgName)) {
						L.d(TAG, "进程被切换过 pkgName:" + foregroundRunningAppPkgName);
						bCan = true;
					}
				}
			}
			if (!"816263c9-507f-4691-b9fd-2cc4535015c5".equalsIgnoreCase(ManifestUtil.getMetaValue(OreApp.get(),ManifestUtil.ONEIGHT_CHANNEL)) && checkMoreThanMaxInteval()) {
				// 大于最大间隔强制弹
				L.d(TAG, "大于最大间隔强制弹");
				bCan = true;
			} 
		}
		L.d(TAG, "check bCan:"+bCan);
		return bCan;
	}

	private static boolean checkRunningAppProcesses(String topPkgName) {
		if (mTopPkgName == null) {
			mTopPkgName = topPkgName;
			return true;
		}
		if (null != topPkgName && mTopPkgName != null) {
			boolean isLauncher = isLauncher(topPkgName);
			L.d(TAG, "checkRunningAppProcesses isLauncher:" + isLauncher
					+ " topPkgName:" + topPkgName
					+ " mRunningAppProcessInfo.processName:" + mTopPkgName);
			if (!topPkgName.equalsIgnoreCase(mTopPkgName) && !isLauncher) {
				mTopPkgName = topPkgName;
				return true;
			}
		}
		mTopPkgName = topPkgName;
		return false;
	}

	/**
	 * 检查当前在最前端运行的进程包名列表
	 * 
	 * @param context
	 * @return
	 */
	public final static String getForegroundRunningAppProcessInfo() {
		try {
			ActivityManager activityManager = (ActivityManager) OreApp.get()
					.getApplicationContext()
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager
					.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					Log.d(TAG, "当前顶端进程的信息");
					Log.d(TAG, "进程id: " + processInfo.pid);
					Log.d(TAG, "进程名  : " + processInfo.processName);
					Log.d(TAG, "该进程下可能存在的包名");
					for (String pkgName : processInfo.pkgList) {
						Log.d(TAG, "  " + pkgName);
					}
					return (processInfo.pkgList != null && processInfo.pkgList.length > 0) ? processInfo.pkgList[0]
							: "";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getLauncherPackageName() {
		if (!TextUtils.isEmpty(mLauncherPackageName)) {
			return mLauncherPackageName;
		}
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = OreApp.get().getPackageManager()
				.resolveActivity(intent, 0);
		if (res == null || res.activityInfo == null) {
			return "launcher";
		}
		// 如果是不同桌面主题，可能会出现某些问题，这部分暂未处理
		if (res.activityInfo.packageName.equals("android")) {
			return "launcher";
		} else {
			return res.activityInfo.packageName;
		}
	}

	private static boolean checkMoreThanMaxInteval() {
		long last_show_time = getLastShowTime();
		long interval = System.currentTimeMillis() - last_show_time;
		L.d(TAG, "checkMoreThanMaxInteval:" + interval / (60 * 1000) + "分钟");
		if (interval > OreApp.get().getOreConfig().floating_window_show_max_inteval * 60 * 1000) {
			return true;
		}
		return false;
	}

	private static boolean checkMoreThanMinInteval() {
		boolean isDebug = OreApp.get().getDebug();
		if (isDebug) {
			return true;
		}
		long last_show_time = getLastShowTime();
		long interval = System.currentTimeMillis() - last_show_time;
		L.d(TAG, "checkMoreThanMinInteval:" + interval / (60 * 1000) + "分钟");
		if (interval > OreApp.get().getOreConfig().floating_window_show_min_inteval * 60 * 1000) {
			return true;
		}
		return false;
	}

	private static long getLastShowTime() {
		long last_show_time = P.getLong(true, P.LAST_SHOW_TIME, -1);
		if (last_show_time == -1) {
			P.putLong(true, P.LAST_SHOW_TIME, System.currentTimeMillis());
		}
		return last_show_time;
	}

	private static boolean isLauncher(String topPackageName) {
		List<String> launcherName = getLauncherPackageNameNew();
		if (launcherName != null && launcherName.size() != 0) {
			for (int i = 0; i < launcherName.size(); i++) {
				if (launcherName.get(i) != null
						&& launcherName.get(i).equals(topPackageName)) {
					return true;
				}
			}
		}
		return false;
	}

	private static List<String> getLauncherPackageNameNew() {
		List<String> packageNames = new ArrayList<String>();
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		// final ResolveInfo res =
		// context.getPackageManager().resolveActivity(intent, 0);
		List<ResolveInfo> resolveInfo = OreApp
				.get()
				.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo) {
			packageNames.add(ri.activityInfo.packageName);
			L.d(TAG, "packageName =" + ri.activityInfo.packageName);
		}
		if (packageNames == null || packageNames.size() == 0) {
			return null;
		} else {
			return packageNames;
		}
	}
}
