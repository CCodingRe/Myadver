package com.google.android.ore.bean;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.ore.util.ManifestUtil;
import com.google.android.ore.util.P;

public class SdkInfo {
	public int version_code;
	public String version_name;
	public String package_name;
	public String channel_id;
	public String user_id; // server给的用户唯一标识
	public String apk_path; // 安装路径

	public SdkInfo(Context context) {
		version_code = getVersionCode(context);
		version_name = getVersionName(context);
		package_name = getPackageName(context);
		channel_id = ManifestUtil.getMetaValue(context,ManifestUtil.ONEIGHT_CHANNEL);
		apk_path = getSdkInstallPath(context);
		user_id = P.getString(true, P.USER_ID, "");
	}

	public String getSdkInstallPath(Context context) {
		String apkpath = "";
		try {
			ApplicationInfo appInfo = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (appInfo != null) {
				apkpath = appInfo.sourceDir;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			apkpath = "";
		}
		return apkpath;
	}
	
	public static String getPackageName(Context context) {
		String pkgName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			pkgName = pi.packageName;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pkgName;
	}
	
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionCode;
	}

	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}


}
