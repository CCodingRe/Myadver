package com.google.android.ore.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.ore.Constant;
import com.google.android.ore.activity.OreWebActivity;
import com.google.android.ore.bean.ClickAction;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.download.DLMgr;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.util.ApkUtil;

public class Advert {
	public static void onClick(Context context, int oreId, OreItemInfo info) {
		if (info.click_action == ClickAction.download) {
			goToDownLoad(context, oreId, info, false);
		} else if (info.click_action == ClickAction.download_ui) {
			goToDownLoad(context, oreId, info, true);
		} else if (info.click_action == ClickAction.open_web_ddl
				|| info.click_action == ClickAction.open_web_normal) {
			goToWeb(context, oreId, info);
		} else if (info.click_action == ClickAction.open_web_go_to_gp) {
			goToGP(context, oreId, info);
		}
	}

	public static void goToDownLoad(Context context, int oreId,
			OreItemInfo info, boolean btips) {
		if (ApkUtil.isAppInstalled(context, info.app_package_name)) {
			openApp(context, oreId, info);
			return;
		}
		if (ApkUtil.isCompleteApk(Constant.getApkFilePath(info.ore_item_id))) {
			ApkUtil.installAPP(Constant.getApkFilePath(info.ore_item_id));
			return;
		}
		DLMgr.get().download(context, oreId, info, btips);
	}

	public static void openApp(Context context, int oreId, OreItemInfo info) {
		ApkUtil.openApp(context, info);
		OreReport.statisticalReport(oreId, info.ore_item_id,
				ReportKey.ore_app_open);
	}

	public static void goToWeb(Context context, int oreId, OreItemInfo info) {
		if (TextUtils.isEmpty(info.click_url)) {
			return;
		}
		Intent intent = new Intent(context, OreWebActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.putExtra(OreWebActivity.WEB_URL, info.click_url);
		intent.putExtra(OreWebActivity.ORE_ID, oreId);
		intent.putExtra(OreWebActivity.ORE_ITEM_ID, info.ore_item_id);
		context.startActivity(intent);
		OreReport.statisticalReport(oreId, info.ore_item_id,
				ReportKey.ore_open_web);
	}

	public static void goToGP(Context context, int oreId, OreItemInfo info) {
		if (TextUtils.isEmpty(info.app_package_name)) {
			return;
		}
		Intent localIntent = new Intent("android.intent.action.VIEW");
		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		localIntent.setData(Uri.parse("market://details?id="
				+ info.app_package_name));
		if (localIntent.resolveActivity(context.getPackageManager()) != null) {
			context.startActivity(localIntent);
		} else {
			localIntent.setData(Uri
					.parse("https://play.google.com/store/apps/details?id="
							+ info.app_package_name));
			if (localIntent.resolveActivity(context.getPackageManager()) != null) { // 有浏览器
				context.startActivity(localIntent);
			}
		}
		OreReport.statisticalReport(oreId, info.ore_item_id,
				ReportKey.ore_open_gp);
	}
}
