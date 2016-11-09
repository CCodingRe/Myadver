package com.google.android.ore.util;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.android.ore.OreApp;

/*
 * SharedPreferences
 *   全局
 *   当天
 */
public class P {
	private static final String GLOBAL_SHAREDPREFERENCES_KEY = OreApp.get()
			.getPackageName() + "";
	private static final SharedPreferences mGlobalSharedPreferences = OreApp.get()
			.getSharedPreferences(GLOBAL_SHAREDPREFERENCES_KEY,
					Activity.MODE_PRIVATE);

	private static SharedPreferences getCurrDaySharedPreferences(String ymd) {
		return OreApp.get()
				.getSharedPreferences(GLOBAL_SHAREDPREFERENCES_KEY + "_" + ymd,
						Activity.MODE_PRIVATE);
	}
	
	public static void clearLastDaySharedPreferences(){
		SharedPreferences lastDaySharedPreferences = OreApp.get()
		.getSharedPreferences(GLOBAL_SHAREDPREFERENCES_KEY + "_" + DateUtil.getLastDate_YMD(),
				Activity.MODE_PRIVATE);
		lastDaySharedPreferences.edit().clear().commit();
	}

	public static boolean getBoolean(boolean isGlobal,String key, boolean b) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		return currPreferences.getBoolean(key, b);
	}

	public static void putBoolean(boolean isGlobal,String key, boolean b) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		currPreferences.edit().putBoolean(key, b).commit();
	}

	public static String getString(boolean isGlobal, String key, String defValue) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		return currPreferences.getString(key, defValue);
	}

	public static void putString(boolean isGlobal, String key, String value) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		currPreferences.edit().putString(key, value).commit();
	}

	public static int getInt(boolean isGlobal, String key, int defValue) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		return currPreferences.getInt(key, defValue);
	}

	public static void putInt(boolean isGlobal, String key, int value) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		currPreferences.edit().putInt(key, value).commit();
	}

	public static long getLong(boolean isGlobal, String key, long defValue) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		return currPreferences.getLong(key, defValue);
	}

	public static void putLong(boolean isGlobal, String key, long value) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		currPreferences.edit().putLong(key, value).commit();
	}

	public static float getFloat(boolean isGlobal, String key, float defValue) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		return currPreferences.getFloat(key, defValue);
	}

	public static void putFloat(boolean isGlobal, String key, float value) {
		SharedPreferences currPreferences;
		if (isGlobal) {
			currPreferences = mGlobalSharedPreferences;
		} else {
			currPreferences = getCurrDaySharedPreferences(DateUtil
					.getCurDate_YMD());
		}
		currPreferences.edit().putFloat(key, value).commit();
	}
	
	
	public static String USER_ID = OreApp.get().getPackageName() +"_USER_ID";
	public static String ORE_CONFIG = OreApp.get().getPackageName() +"_ORE_CONFIG";
	public static String LOG_UPLOAD_TIME = OreApp.get().getPackageName() +"_LOG_UPLOAD_TIME";
	public static String LAST_SUCCESS_LOG_UPLOAD_TIME = OreApp.get().getPackageName() +"_LAST_SUCCESS_LOG_UPLOAD_TIME";
	public static String UPLOAD_LOG_FAIL_TIMES = OreApp.get().getPackageName() +"_UPLOAD_LOG_FAIL_TIMES";
	
	public static String LAST_POLLING_TIME = OreApp.get().getPackageName() +"_LAST_POLLING_TIME";
	public static String LAST_LOG_TIME = OreApp.get().getPackageName() +"_LAST_LOG_TIME";
	public static String LAST_SHOW_TIME = OreApp.get().getPackageName() +"_LAST_SHOW_TIME";
	
	
	
	public static String CURR_DAY_STRATEGY_DATA = OreApp.get().getPackageName() +"_CURR_DAY_STRATEGY_DATA";
	public static String CURR_DAY_FETCH_AD_SUCC_NUM = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_AD_SUCC_NUM";
	public static String CURR_DAY_FETCH_DNS = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_DNS";
	public static String CURR_DAY_FETCH_DNS_URL_PRE = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_DNS_URL_PRE";
//	public static String CURR_DAY_ALARM_NUM = APP.get().getPackageName() +"_CURR_DAY_ALARM_NUM";
	public static String CURR_DAY_ALARM_STARTED = OreApp.get().getPackageName() +"_CURR_DAY_ALARM_STARTED";
	//当天拉取策略尝试次数
	public static String CURR_DAY_FETCH_STRATEGY_NUM = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_STRATEGY_NUM";
	//拉广告间隔控制--server下发
	public static String CURR_DAY_FETCH_AD_POLLING_DURATION = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_AD_POLLING_DURATION";
	//上次拉取广告时间
	public static String CURR_DAY_FETCH_AD_LAST_TIME = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_AD_LAST_TIME";
	//当天是否启动过activity
	public static String CURR_DAY_HAS_LAUNCHER_ACTIVITY = OreApp.get().getPackageName() +"_CURR_DAY_HAS_LAUNCHER_ACTIVITY";
	//上次拉取ore时间
	public static String CURR_DAY_FETCH_ORE_TIME = OreApp.get().getPackageName() +"_CURR_DAY_FETCH_ORE_TIME";
	//用以标记是否是新的一天
	public static String IS_A_NEW_DAY = OreApp.get().getPackageName() +"_IS_A_NEW_DAY";
	
}
