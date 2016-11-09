package com.google.android.ore.bean;

import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.ore.util.DevUtil;

public class MacheInfo {
	public String imei;
	public String imsi;
	public String phone_num;
	public String brand;
	public String model;
	public String screenWH;
	public String google_accout;
	public String country;
	public String language;
	public String sign;
	public boolean is_gp_install;// Google play 安装
	public boolean is_open_no_mareke_setting;// 未知应用
	public String os_version;

	public MacheInfo(Context context) {
		imei = getImei(context);
		imsi = getImsi(context);
		phone_num = DevUtil.getPhoneNum(context);
		brand = android.os.Build.BRAND;
		model = android.os.Build.MODEL;
		screenWH = getScreenWH(context);
//		google_accout = getGoogleAccount(context);
		country = Locale.getDefault().getCountry();
		language = Locale.getDefault().getLanguage();
		// sign = getSign(context);
//		is_gp_install = ApkUtil.isAppInstalled(context, "com.android.vending");
		os_version = android.os.Build.VERSION.RELEASE;
//		is_open_no_mareke_setting = isNonMarketAppsAllowed(context);
	}

	public static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	public static String getImsi(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSubscriberId();
	}

	public static String getScreenWH(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics displaysMetrics = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(displaysMetrics);
		return displaysMetrics.widthPixels + "x" + displaysMetrics.heightPixels;
	}

	public static String getGoogleAccount(Context context) {
		return "";
//		StringBuilder googleAccountStrBld = new StringBuilder();
//		try {
//			ClassLoader classLoader = context.getClassLoader();
//
//			Class<?> accountManagerClass = classLoader
//					.loadClass("android.accounts.AccountManager");
//			Method get = accountManagerClass.getDeclaredMethod("get",
//					Context.class);
//			Object accountManagerInstance = get.invoke(null, context);
//			Method getAccounts = accountManagerClass.getDeclaredMethod(
//					"getAccountsByType", String.class);
//			Object[] accounts = (Object[]) getAccounts.invoke(
//					accountManagerInstance, "com.google");
//			String temp = null;
//			for (int i = 0; i < accounts.length; i++) {
//				temp = (String) accounts[i].getClass().getDeclaredField("name")
//						.get(accounts[i]);
//				if (!TextUtils.isEmpty(temp)) {
//					googleAccountStrBld.append(temp).append(";");
//				}
//			}
//
//		} catch (Exception e) {
//
//		}
//
//		return googleAccountStrBld.toString();
	}

	public static String getSign(Context context) {
		String sign = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_SIGNATURES);
			sign = pi.signatures[0].toCharsString();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		if (null == sign || (null != sign && sign.length() <= 0)) {
			return "";
		} else {
			return sign;
		}
	}

	private static boolean isNonMarketAppsAllowed(Context context) {
		int installNonMarketApps = 0;
		if (Build.VERSION.SDK_INT < 3) {
			installNonMarketApps = Settings.System.getInt(
					context.getContentResolver(),
					Settings.System.INSTALL_NON_MARKET_APPS, 0);
		} else if (Build.VERSION.SDK_INT < 17) {
			installNonMarketApps = Settings.Secure.getInt(
					context.getContentResolver(),
					Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
		} else {
			installNonMarketApps = Settings.Global.getInt(
					context.getContentResolver(),
					Settings.Global.INSTALL_NON_MARKET_APPS, 0);
		}
		if (installNonMarketApps > 0) {
			return true;
		}
		return false;
	}

}
