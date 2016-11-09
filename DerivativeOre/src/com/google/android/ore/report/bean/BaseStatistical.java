package com.google.android.ore.report.bean;

import com.google.android.ore.bean.MacheInfo;
import com.google.android.ore.bean.SdkInfo;

public class BaseStatistical {
	//user_id  |  ore_id  |  ore_type  |  version_code  |  version_name  |  package_name  |  channel_id  |  imei  |  imsi |
	private String user_id;
	private int version_code;
	private String version_name;
	private String package_name;
	private String channel_id;
	private String imei;
	private String imsi;
	public BaseStatistical(MacheInfo macheInfo, SdkInfo sdkInfo) {
		if (null != macheInfo) {
			imei = macheInfo.imei;
			imsi = macheInfo.imsi;
		}
		if (null != sdkInfo) {
			user_id = sdkInfo.user_id;
			version_code = sdkInfo.version_code;
			version_name = sdkInfo.version_name;
			package_name = sdkInfo.package_name;
			channel_id = sdkInfo.channel_id;
		}
	}
	public void setUserId(String userId){
		user_id = userId;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(user_id).append("|")
		  .append(version_code).append("|")
		  .append(version_name).append("|")
		  .append(package_name).append("|")
		  .append(channel_id).append("|")
		  .append(imei).append("|")
		  .append(imsi);
		return sb.toString();
	}
}
