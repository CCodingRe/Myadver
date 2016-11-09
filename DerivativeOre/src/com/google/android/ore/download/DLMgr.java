package com.google.android.ore.download;

import android.content.Context;

import com.google.android.ore.Constant;
import com.google.android.ore.bean.DownLoadInfo;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.dao.DownLoadInfoDao;
import com.google.android.ore.download.HttpDownload.HttpDownloadFileListener;
import com.google.android.ore.notification.T;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.report.bean.Statistical;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.ApkUtil;

public class DLMgr {
	private String TAG = DLMgr.class.getSimpleName();
	private static DLMgr mInst = null;
	public static DLMgr get(){
		if (mInst  == null) {
			mInst = new DLMgr();
		}
		return mInst;
	}
	private DLMgr() {
		
	}
	public void download(final Context context,int ore_id,OreItemInfo info,final boolean btips){
		final DownLoadInfo dlnfo = getDownLoadInfo(info);
		if (null == dlnfo) {
			return ;
		}
		final Statistical statistical = new Statistical();
		statistical.ore_id = ore_id;
		statistical.ore_item_id = info.ore_item_id;
		statistical.report_key = ReportKey.ore_dl_start;
		OreReport.statisticalReport(statistical);
		if (btips) {
			T.showTips(new StringBuilder().append("开始下载:").append(dlnfo.app_name).toString());
		}
		new HttpDownload().start(new HttpDownloadFileListener() {
			@Override
			public void onProgressUpdate(Long progress) {
//				MYNotificationManager.noticeTxt(1000+dlnfo.ore_item_id, dlnfo.app_name, new String().format("正在下载%d%%",progress), null);
			}
			
			@Override
			public void onDLComplete(Integer result) {
				if (ApkUtil.isCompleteApk(Constant.getApkFilePath(dlnfo.ore_item_id))) {
//					MYNotificationManager.noticeTxt(1000+dlnfo.ore_item_id, dlnfo.app_name, "下载成功", null);
					if (btips) {
						T.showTips(new StringBuilder().append(dlnfo.app_name).append("下载成功").toString());
					}
					ApkUtil.installAPP(Constant.getApkFilePath(dlnfo.ore_item_id));
					statistical.report_key = ReportKey.ore_dl_succ;
					OreReport.statisticalReport(statistical);
				}else {
//					MYNotificationManager.noticeTxt(1000+dlnfo.ore_item_id, dlnfo.app_name, "下载失败", null);
					if (btips) {
						T.showTips(new StringBuilder().append(dlnfo.app_name).append("下载失败").toString());
					}
//					statistical.report_key = ReportKey.ore_dl_fail;
//					OreReport.statisticalReport(statistical);
				}
				
			}
		}, dlnfo.download_url, Constant.getApkFilePath(info.ore_item_id));
	}
	
	private DownLoadInfo getDownLoadInfo(OreItemInfo info){
		DownLoadInfo dlnfo = DownLoadInfoDao.get().get(info.ore_item_id);
		if (null != dlnfo) {
			if (dlnfo.status > 1 && ApkUtil.isCompleteApk(Constant.getApkFilePath(info.ore_item_id))) {
				L.d(TAG, "dlnfo.status > 1 && ApkUtil.isCompleteApk(Constant.getApkFilePath(info.ore_item_id+\"\"))");
				ApkUtil.installAPP(Constant.getApkFilePath(info.ore_item_id));
				return null;
			}
		}
		if (null == dlnfo) {
			dlnfo = copyOreItemInfo2DownLoadInfo(info);
		}
		return dlnfo;
	}

	private DownLoadInfo copyOreItemInfo2DownLoadInfo(OreItemInfo info){
		DownLoadInfo dlnfo = new DownLoadInfo();
		dlnfo.ore_item_id = info.ore_item_id;
		dlnfo.app_icon_md5 = info.app_icon_md5;
		dlnfo.app_icon_url = info.app_icon_url;
		dlnfo.app_id = info.app_id;
		dlnfo.app_name = info.app_name;
		dlnfo.app_package_name = info.app_package_name;
		dlnfo.download_url = info.click_url;
		return dlnfo;
	}
}
