package com.google.android.ore;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.db.dao.OreFloatingWindowDao;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.FileHttpResponseHandler;
import com.google.android.ore.util.Encrypt;
import com.google.android.ore.zip.Zip;

public class FetchFloatingWindowOreMaterialNew extends FetchFloatingWindowOreMaterial {
	OreShowStrategyNew mOreShowStrategyNew;

	public FetchFloatingWindowOreMaterialNew() {
		super();
		mOreShowStrategyNew = new OreShowStrategyNew();
	}

	@Override
	protected FileHttpResponseHandler createHandler(final OreFloatingWindow oreFloatingWindow, final Context context,
			String resFileName) {
		return new FileHttpResponseHandler(resFileName) {
			@Override
			public void onFinish() {
				super.onFinish();
				L.d(TAG, "FileHttpResponseHandler finish");
				
				// 更改状态
				updateStatus(context, oreFloatingWindow);
				// 展示
				mOreShowStrategyNew.show(context);
				isAliving = false;
			}
		};
	}

	private void updateStatus(Context context, OreFloatingWindow oreFloatingWindow) {
		if (null != oreFloatingWindow) {
			oreFloatingWindow.fetch_material_num = oreFloatingWindow.fetch_material_num + 1;
			if (oreFloatingWindow.fetch_material_num > Constant.FETCHOREMATERIAL_MAX_RETRY_NUM) {
				L.d(TAG, "updateStatus retry max time");
				oreFloatingWindow.status = OreFloatingWindow.FLOATWINDOW_RESOURCE_FETCH_MAX;
				OreReport.statisticalReport(oreFloatingWindow.ore_id, 0, ReportKey.ore_fetch_material_retry_max);
			} else {
				if (!TextUtils.isEmpty(oreFloatingWindow.ore_resource)
						&& !TextUtils.isEmpty(oreFloatingWindow.ore_resource_md5)) {
					L.d(TAG, "fetchResource oreFloatingWindow.fetch_material_num:"
							+ oreFloatingWindow.fetch_material_num + " oreFloatingWindow.ore_id:"
							+ oreFloatingWindow.ore_id);
					int result = checkLocalResZipCompleteNew(oreFloatingWindow);
					L.d(TAG, "updateStatus check result: " + result);
					switch (result) {
					case OreFloatingWindow.SUCCESS: {
						oreFloatingWindow.status = OreFloatingWindow.FLOATWINDOW_RESOURCE_READY;
						OreReport.statisticalReport(oreFloatingWindow.ore_id, 0, ReportKey.ore_fetch_material_succ);
						break;
					}
					case OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_DIR_NOT_EXIST: {
						OreReport.statisticalReport(oreFloatingWindow.ore_id, 0,
								ReportKey.ore_fetch_material_download_dir_not_exist);
						break;
					}
					case OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_FILE_NOT_EXIST: {
						OreReport.statisticalReport(oreFloatingWindow.ore_id, 0,
								ReportKey.ore_fetch_material_download_file_not_exist);
						break;
					}
					case OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_SYSTEM_MD5_EMPTY: {
						OreReport.statisticalReport(oreFloatingWindow.ore_id, 0,
								ReportKey.ore_fetch_material_download_system_md5_empty);
						break;
					}
					case OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_MD5_ERROR: {
						OreReport.statisticalReport(oreFloatingWindow.ore_id, 0,
								ReportKey.ore_fetch_material_download_md5_error);
						break;
					}
					}
				} else {
					L.d(TAG, "updateStatus download resource url is empty");
					oreFloatingWindow.status = OreFloatingWindow.FLOATWINDOW_RESOURCE_ULR_EMPTY;
					OreReport.statisticalReport(oreFloatingWindow.ore_id, 0, ReportKey.ore_fetch_material_url_empty);
				}
			}
			L.d(TAG, "fetchFloatingWindow oreFloatingWindow.status:" + oreFloatingWindow.status);
			OreFloatingWindowDao.get().update(oreFloatingWindow);
		} else {
			L.d(TAG, "updateStatus oreFloatingWindow is null");
		}
	}

	protected int checkLocalResZipCompleteNew(OreFloatingWindow oreFloatingWindow) {
		int result = OreFloatingWindow.SUCCESS;
		File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
		if (resDir == null) {
			return OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_DIR_NOT_EXIST;
		}
		final String resFileName = resDir.getAbsolutePath() + "/" + oreFloatingWindow.ore_resource_md5;
		File resFile = new File(resFileName);
		if (resFile == null || !resFile.exists()) {
			return OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_FILE_NOT_EXIST;
		}
		if (TextUtils.isEmpty(oreFloatingWindow.ore_resource_md5)) {
			return OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_SYSTEM_MD5_EMPTY;
		}
		String md5 = Encrypt.getFileMd5(resFileName);
		String serverMd5 = oreFloatingWindow.ore_resource_md5;
		L.d(TAG, "checkLocalResZipComplete md5:" + md5 + " | serverMd5: " + serverMd5);
		if (!serverMd5.equalsIgnoreCase(md5)) {
			return OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_MD5_ERROR;
		}
		L.d(TAG, "checkLocalResZipComplete checkResExistAndComplete true resFileName:" + resFileName);
		Zip.unzip(new File(resFileName), Constant.getDir(Constant.RES_DIR));
		L.d(TAG, "checkLocalResZipComplete Zip.unzip");
		return result;
	}

	@Override
	protected void syncFetch(Context context) {
		try {
			OreFloatingWindow oreFloatingWindow = fetchFloatingWindow(context);
			if (null != oreFloatingWindow) {
				L.d(TAG, "syncFetch oreFloatingWindow.status:" + oreFloatingWindow.status);
				switch (oreFloatingWindow.status) {
				case OreFloatingWindow.FLOATWINDOW_RESOURCE_NOT_READY: {
					fetchResource(context, oreFloatingWindow);
					break;
				}
				case OreFloatingWindow.FLOATWINDOW_RESOURCE_READY: {
					mOreShowStrategyNew.show(context);
					isAliving = false;
					break;
				}
				default:
					isAliving = false;
					break;
				}
			} else {
				L.d(TAG, "syncFetch oreFloatingWindow is null");
				isAliving = false;
			}
		} catch(Exception e) {
			isAliving = false;
		}
	}

	@Override
	protected OreFloatingWindow fetchFloatingWindow(Context context) {
		List<OreFloatingWindow> OreFloatingWindowList = OreFloatingWindowDao.get()
				.getOreFloatingWindowNeedMaterialNew();
		if (null != OreFloatingWindowList && OreFloatingWindowList.size() > 0) {
			OreFloatingWindow oreFloatingWindow = OreFloatingWindowList.get(0);
			return oreFloatingWindow;
		}
		return null;
	}

	
}
