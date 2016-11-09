package com.google.android.ore;

import java.io.File;

import android.os.Environment;

import com.google.android.ore.thinkandroid.TAExternalUnderFroyoUtils;

public class Constant {
	public static final long ORE_FLOATING_WINDOW_MAX_RETRY_NUM = 12;
	public static final long OREITEMINFO_MAX_RETRY_NUM = 12;
	public static final long OREGENERAL_MAX_RETRY_NUM = 12;
	public static final long FETCHOREMATERIAL_MAX_RETRY_NUM = 12;
	
	public static final long FLOATING_WINDOW_MAX_SHOW_NUM_DEFAULT = 1;
	public static final long FLOATING_WINDOW_SHOW_MAX_INTERVAL_DEFAULT = 120;//分钟
	public static final long FLOATING_WINDOW_SHOW_MIN_INTERVAL_DEFAULT = 30;//分钟
	public static final long FETCH_ORE_FAIL_INTERVAL_DEFAULT = 20;//分钟
	public static final long FETCH_ORE_SUCC_INTERVAL_DEFAULT = 20;//分钟
	public static final long REPEAT_DURATION_MAX = 90;//分钟
	public static final long REPEAT_DURATION_MIN = 3;//分钟

	public static final String APK_DIR = "apk";
	public static final String DEBUG_DIR = Environment.getExternalStorageDirectory().getPath()+"/debug/";
	public static final String ORE_LOCAL_LOG_DIR = Environment.getExternalStorageDirectory().getPath()+"/ore_local_log/";
	
	public static final String RES_DIR = "res";
	public static final String RES_ZIP_DIR = "res_zip";
	public static final String LOG_DIR = Environment.getExternalStorageDirectory().getPath()+"/system/";

	public static File getDir(String dirName) {
		File resDir = TAExternalUnderFroyoUtils.getDiskCacheDir(OreApp.get(),
				dirName);
		if (!resDir.exists()) {
			resDir.mkdirs();
			// do not allow media scan
			try {
				new File(resDir, ".nomedia").createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return resDir;
	}
	public static String getApkFilePath(int unique) {
		return getApkFilePath(unique+"");
	}
	public static String getApkFilePath(String unique) {
		File apkDir = getDir(APK_DIR);
		if (null != apkDir && apkDir.exists()) {
			return apkDir.getAbsolutePath() + File.separator + unique;
		}
		return "";
	}
}
