package com.google.android.ore.report;

import android.text.TextUtils;

import com.google.android.ore.OreApp;
import com.google.android.ore.report.bean.Statistical;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.AsyncHttpClient;
import com.google.android.ore.thinkandroid.http.AsyncHttpResponseHandler;
import com.google.android.ore.thinkandroid.http.RequestParams;
import com.google.android.ore.util.ManifestUtil;

public class OreReport {
	
	public static void statisticalReport(int ore_id,int ore_item_id,String report_key) {
		if (TextUtils.isEmpty(report_key)) {
			return;
		}
		final Statistical statistical = new Statistical();
		statistical.ore_id = ore_id;
		statistical.ore_item_id = ore_item_id;
		statistical.report_key = report_key;
		OreReport.statisticalReport(statistical);
	}
	/*
	 * 统计
	 */
	public static void statisticalReport(Statistical statistical) {
		if (null == statistical) {
			return;
		}
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams requestParams = new RequestParams();
		requestParams.put("data", statistical.toString());
		L.d("statisticalReport", statistical.toString());
		client.post(ManifestUtil.getUact(OreApp.get()) , requestParams, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
			}
			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}
	/*
	 * 错误上报
	 */
	public static void errReport(String errInfo) {
		if (TextUtils.isEmpty(errInfo)) {
			return;
		}
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams requestParams = new RequestParams();
		requestParams.put("data", errInfo);
		L.d("errReport", errInfo);
		client.post(ManifestUtil.getErr(OreApp.get()) , requestParams, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
			}
			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}

}
