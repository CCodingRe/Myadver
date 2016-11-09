package com.google.android.ore.util;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.google.android.ore.OreApp;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.dao.OreFloatingWindowDao;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.gson.reflect.TypeToken;

public class DbUtil {
	
	public  static ArrayList<OreItemInfo> fetchOreItemInfoListNeedShow(Context context) {
		OreFloatingWindow oreFloatingWindow = fetchFloatingWindowNeedShow(context);
		if (null != oreFloatingWindow) {
			return fetchFloatingWindowNeedShow(context, oreFloatingWindow);
		}
		return new ArrayList<OreItemInfo>();
	}

	public  static OreFloatingWindow fetchFloatingWindowNeedShow(Context context) {
		List<OreFloatingWindow> oreFloatingWindowList = OreFloatingWindowDao.get().getOreFloatingWindowNeedShow();
		if (null != oreFloatingWindowList && oreFloatingWindowList.size() > 0) {
			return oreFloatingWindowList.get(0);
		}
		return null;
	}

	public   static ArrayList<OreItemInfo> fetchFloatingWindowNeedShow(Context context,
			OreFloatingWindow oreFloatingWindow) {
		ArrayList<OreItemInfo> oreItemInfoList = new ArrayList<OreItemInfo>();
		if (null != oreFloatingWindow) {
			Type type = new TypeToken<ArrayList<Integer>>() {
			}.getType();
			oreFloatingWindow.ore_item_id_list = OreApp.get().getGson()
					.fromJson(oreFloatingWindow.ore_item_id_list_json, type);
		}
		if (null != oreFloatingWindow
				&& null != oreFloatingWindow.ore_item_id_list
				&& oreFloatingWindow.ore_item_id_list.size() > 0) {
			int count = oreFloatingWindow.ore_item_id_list.size();
			for (int i = 0; i < count; i++) {
				int ore_id = oreFloatingWindow.ore_item_id_list.get(i);
				OreItemInfo oreItemInfo = fetchOreItemInfo(context, ore_id);
				if (null != oreItemInfo && oreItemInfo.status != 4) {
					oreItemInfoList.add(oreItemInfo);
				}
			}
		}
		return oreItemInfoList;
	}

	public  static OreItemInfo fetchOreItemInfo(Context context, int ore_id) {
		return OreItemInfoDao.get().get(ore_id);
	}
}
