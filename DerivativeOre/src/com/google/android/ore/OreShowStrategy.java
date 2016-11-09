package com.google.android.ore;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.ore.app.Scene;
import com.google.android.ore.bean.ClickAction;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.ui.HSHelper;
import com.google.android.ore.util.ApkUtil;
import com.google.android.ore.util.DbUtil;

public class OreShowStrategy {
	private final String TAG =  OreShowStrategy.class.getSimpleName();
	private boolean isAliving = false;
	private OreFloatingWindow mOreFloatingWindow = new OreFloatingWindow();
	private ArrayList<OreItemInfo> mOreItemInfoList = new ArrayList<OreItemInfo>();
	public void show(final Context context){
		L.d(TAG , "show isAliving.get():"+isAliving);
		if (HSHelper.isShowing()) {
			L.d(TAG, "HSHelper is showing");
			return;
		}
		if (isAliving) {
			return;
		}
		isAliving = true;
		try {
			new AsyncTask<Object, Object, Boolean>() {

				@Override
				protected Boolean doInBackground(Object... params) {
					update(context);
					boolean isSomeAppLaunchered = Scene.check(context);
					L.d(TAG , "isSomeAppLaunchered : "+isSomeAppLaunchered);
					return isSomeAppLaunchered;
				}
				
				protected void onPostExecute(Boolean isSomeAppLaunchered) {
					reportInstalled(context);
					showHS(context,isSomeAppLaunchered);
					isAliving = false;
				};


				protected void onCancelled(Boolean result) {
					isAliving = false;
				};
			}.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//上报安装成功
	private void reportInstalled(Context context) {
		if (null == mOreFloatingWindow || null == mOreItemInfoList) {
			L.d(TAG , "reportInstalled mOreFloatingWindow: " + mOreFloatingWindow + " | mOreItemInfoList: " + mOreItemInfoList);
			return;
		}
		for (int i = 0; i < mOreItemInfoList.size(); i++) {
			OreItemInfo item = mOreItemInfoList.get(i);
			if (5 == item.status) {
				OreReport.statisticalReport(mOreFloatingWindow.ore_id, item.ore_item_id, ReportKey.ore_app_install_succ);
				item.status = 6;
			}
		}
	}
	private boolean showHS(Context context,boolean isSomeAppLaunchered){
		if (null != mOreFloatingWindow && mOreItemInfoList != null && mOreItemInfoList.size() > 0 && isSomeAppLaunchered) {
			L.d(TAG , "showHS mOreFloatingWindow.ore_id:"+mOreFloatingWindow.ore_id);
			L.d(TAG , "showHS mOreItemInfoList.size():"+mOreItemInfoList.size());
			ArrayList<OreItemInfo> data = new ArrayList<OreItemInfo>();
			for (int i = 0; i < mOreItemInfoList.size(); i++) {
				OreItemInfo oreItemInfo = mOreItemInfoList.get(i);
				if (oreItemInfo.status < 4) {
					data.add(oreItemInfo);
				}
			}
			L.d(TAG , "showHS data.size():"+data.size());
			if (data.size() > 0) {
				HSHelper.showAdvert(mOreFloatingWindow,data);
			}
			return true;
		} else {
			L.d(TAG , "showHS mOreFloatingWindow: " + mOreFloatingWindow + " | isSomeAppLaunchered: " + isSomeAppLaunchered + " | mOreItemInfoList: " + mOreItemInfoList);
		}
		return false;
	}
	
	
	protected void  update(Context context){
		OreFloatingWindow oreFloatingWindow = DbUtil.fetchFloatingWindowNeedShow(context);
		if (null != oreFloatingWindow) {
			ArrayList<OreItemInfo> oreItemInfoList = DbUtil.fetchFloatingWindowNeedShow(context,oreFloatingWindow);
			mOreFloatingWindow = oreFloatingWindow;
			mOreItemInfoList.clear();
			if (null != oreItemInfoList && oreItemInfoList.size() > 0) {
				for (int i = 0; i < oreItemInfoList.size(); i++) {
					OreItemInfo oreItemInfo = oreItemInfoList.get(i);
					if (null != oreItemInfo) {
						if (oreItemInfo.status != 6 && (oreItemInfo.click_action == ClickAction.download || oreItemInfo.click_action == ClickAction.download_ui) && ApkUtil.isAppInstalled(context, oreItemInfo.app_package_name)) {
							//状态6落DB
							oreItemInfo.status = 6;
							OreItemInfoDao.get().update(oreItemInfo);
							//内存状态5为了上报，先这样吧
							oreItemInfo.status = 5;
						}
						mOreItemInfoList.add(oreItemInfo);
					}
				}
			}
			L.d(TAG , "update null != oreFloatingWindow && mOreItemInfoList.size="+mOreItemInfoList.size());
		}else{
			L.d(TAG , "update null == oreFloatingWindow");
		}
	}
	
}
