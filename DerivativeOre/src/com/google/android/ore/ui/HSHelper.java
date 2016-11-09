package com.google.android.ore.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.google.android.ore.OreApp;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.dao.OreFloatingWindowDao;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.P;
import com.google.android.ore.util.Utils;

public class HSHelper {
	private static final String TAG = HSHelper.class.getSimpleName();

	private HSHelper() {
	}

	private static Dialog mDialog;
	private static BannerHS mBannerHS;

	public static void showAdvert(OreFloatingWindow oreFloatingWindow, ArrayList<OreItemInfo> oreItemInfoList) {
		if (null != oreItemInfoList && oreItemInfoList.size() > 0) {
			
		}else {
			L.d(TAG, "isShowing oreItemInfoList is null");
			return;
		}
		if (isShowing()) {
			L.d(TAG, "isShowing oreFloatingWindow.ore_id:"+oreFloatingWindow.ore_id);
			return;
		}
		if (mDialog == null) {
			initDialog();
		}
		boolean isHasView = mBannerHS.initDta(oreFloatingWindow,oreItemInfoList);
		L.d(TAG, "isHasView"+isHasView);
		if (!isHasView) {
			oreFloatingWindow.status = OreFloatingWindow.FLOATWINDOW_SHOW_NO_VIEW;
			OreFloatingWindowDao.get().update(oreFloatingWindow);
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
		}else {
			mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		}
		mDialog.show();
		oreFloatingWindow.show_num = oreFloatingWindow.show_num +1 ;
		if (oreFloatingWindow.show_num >= OreApp.get().getOreConfig().floating_window_max_show_num) {
			oreFloatingWindow.status = OreFloatingWindow.FLOATWINDOW_SHOW_MAX;
		}
		OreFloatingWindowDao.get().update(oreFloatingWindow);
		L.d(TAG, "oreFloatingWindow.status"+oreFloatingWindow.status);
		P.putLong(false, P.LAST_SHOW_TIME, System.currentTimeMillis());
		OreReport.statisticalReport(oreFloatingWindow.ore_id,0,ReportKey.ore_show);
	}

	private static void initDialog() {
		mDialog = new Dialog(OreApp.get(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		mDialog.setCanceledOnTouchOutside(false);
		mBannerHS = new BannerHS(OreApp.get());
		FrameLayout.LayoutParams lp = new LayoutParams(Utils.getWidth(), BannerHS.getH());
		lp.gravity = Gravity.CENTER;
		lp.leftMargin = Utils.dp2px(BannerHS.LEFT_RIGHTM_MARGIN);
		lp.rightMargin = Utils.dp2px(BannerHS.LEFT_RIGHTM_MARGIN);
		mDialog.addContentView(mBannerHS, lp);
	}
	
	public static boolean isShowing(){
		if (mDialog != null && mDialog.isShowing()) {
			return true;
		}
		return false;
	}

	public static void dismiss() {
		if (isShowing()) {
			mDialog.dismiss();
		}
		P.putLong(true, P.LAST_SHOW_TIME, System.currentTimeMillis());
	}
}
