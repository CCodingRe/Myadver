package com.google.android.ore;

import android.content.Context;

import com.google.android.ore.report.DeviceInfoReport;
import com.google.android.ore.thinkandroid.L;

public class OreManager {
	private final String TAG = OreManager.class.getSimpleName();
	private OreOperation mOreOperation;
	private DeviceInfoReport mDeviceInfoReport;
	private FetchOreNew mFetchOreNew;

	public OreManager() {
		L.d(TAG, "create");

		mOreOperation = new OreOperation();
		mDeviceInfoReport = new DeviceInfoReport();
		mFetchOreNew = new FetchOreNew();
	}

	public void doTask(Context context) {
		if (ANewDay.isDeleting || ANewDay.check(context)) {
			L.d(TAG, "isDeleting");
			return;
		}
		L.d(TAG, "doTask");
		mFetchOreNew.fetch(context);
		// 运营需求
		mOreOperation.doSyncTask(context);
		// 设备信息上报
		mDeviceInfoReport.report(context);
	}
}
