package com.google.android.ore.util;

import java.io.File;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.ore.OreApp;
import com.google.android.ore.bean.OreItemInfo;

public class ApkUtil {
	public static final String TAG = "ApkUtil";
	public static final int INSTALL_FAILED_INVALID_APK = -2;

	public static boolean isAppInstalled(Context context, String packageName) {
		PackageInfo packageInfo = null;
		boolean isExist = false;
		if (null == packageName || "".equals(packageName)) {
			return false;
		}

		try {
			packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
			if (packageInfo == null) {
				isExist = false;
			} else {
				isExist = true;
			}
		} catch (NameNotFoundException e) {
			// e.printStackTrace();
			isExist = false;
		}
		return isExist;
	}

	public static boolean openApp(Context context, OreItemInfo oreItem) {
		if (null == context) {
			return false;
		}
		try {
			String launcheAction = oreItem.app_launcher_action;
			if (!TextUtils.isEmpty(launcheAction)) {
				Intent intent = new Intent(launcheAction);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				return true;
			}
			String pkgName = oreItem.app_package_name;
			if (TextUtils.isEmpty(pkgName)) {
				return false;
			}
			String mainActivityName = oreItem.app_main_activity_name;
			if (!TextUtils.isEmpty(mainActivityName)) {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(pkgName, mainActivityName);
				intent.setComponent(cn);
				context.startActivity(intent);
				return true;
			}
			PackageManager pm = OreApp.get().getPackageManager();
			PackageInfo pi = pm.getPackageInfo(pkgName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ComponentName cn = new ComponentName(pkgName, className);
				intent.setComponent(cn);
				context.startActivity(intent);
				return true;
			}
		} catch (Throwable e) {
			Log.e("", e.getMessage());
		}
		return false;
	}

	public void uninstallAPK(Context context, String packageName) {
		Uri uri = Uri.parse("package:" + packageName);
		Intent intent = new Intent(Intent.ACTION_DELETE, uri);
		context.startActivity(intent);
	}

	public static boolean checkPermission(String permissionName) {
		if (TextUtils.isEmpty(permissionName)) {
			return false;
		}
		PackageManager pm = OreApp.get().getPackageManager();
		String pkgName = OreApp.get().getPackageName();
		boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permissionName, pkgName));
		return permission;
	}

	public static String getInstalledAppInfo(Context context) {
		StringBuilder sb = new StringBuilder();
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		if (packages != null) {
			int count = packages.size();
			for (int i = 0; i < count; i++) {
				PackageInfo packageInfo = packages.get(i);
				if (null != packageInfo) {
					// InstalledAppInfo installedAppInfo = new
					// InstalledAppInfo();
					// installedAppInfo.appName =
					// packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
					// installedAppInfo.packageName = packageInfo.packageName;
					// installedAppInfo.versionName = packageInfo.versionName;
					// installedAppInfo.versionCode = packageInfo.versionCode;
					// appName | packageName | versionName | versionCode ; 下一个应用
					sb.append(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString()).append("|")
							.append(packageInfo.packageName).append("|").append(packageInfo.versionName).append("|")
							.append(packageInfo.versionCode).append(";");
				}
			}
		}
		return sb.toString();
	}

	public static boolean installAPP(String apkPath) {
		if (TextUtils.isEmpty(apkPath)) {
			return false;
		}
		File apkFile = new File(apkPath);
		if (apkFile == null || !apkFile.exists()) {
			return false;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
		OreApp.get().startActivity(intent);
		return true;
	}
	
	public static void installAPPByBroadcastReceiver(String apkPath) {
		if (!TextUtils.isEmpty(apkPath)) {
			Intent intent = new Intent("android.intnet.action.OTA");
			intent.putExtra("message", apkPath);
			OreApp.get().sendBroadcast(intent);
		}
	}

	/*
	 * 检查安装包完整性
	 */
	public static boolean isCompleteApk(String apkPath) {
		boolean result = false;
		try {
			PackageManager pm = OreApp.get().getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
			if (info != null) {
				result = true;
			}
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	/*
	 * 检查是否是系统APP
	 */
	public static boolean isSystemApplication(Context context, String packageName){
    	PackageManager manager = context.getPackageManager();
        try {
			PackageInfo packageInfo = manager.getPackageInfo(packageName, PackageManager.GET_CONFIGURATIONS);
			if(new File("/data/app/"+packageInfo.packageName+".apk").exists()){
				return true;
			}
			if((packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM)!=0){
				return true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
    }
}
