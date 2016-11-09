package com.google.android.ore.report.bean;

import java.util.ArrayList;

import com.google.android.ore.OreApp;

public class DeviceInfo {
	public DeviceInfo() {
		
	}
	public String installedAppInfoList;
	public ArrayList<String> qqList;
	public String phone_num;
	public int ip;
	public String user_info;

	public String generateUserInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append(OreApp.get().getSdkInfo().user_id).append("|")
				.append(OreApp.get().getMacheInfo().imei).append("|")
				.append(OreApp.get().getMacheInfo().imsi);
		return sb.toString();
	}
}
