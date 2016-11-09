package com.google.android.ore;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.db.dao.OreFloatingWindowDao;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.AsyncHttpClient;
import com.google.android.ore.thinkandroid.http.FileHttpResponseHandler;
import com.google.android.ore.util.Encrypt;
import com.google.android.ore.zip.Zip;

public class FetchFloatingWindowOreMaterial {
	protected String TAG = FetchFloatingWindowOreMaterial.class.getSimpleName();
	protected volatile boolean isAliving = false;

	public boolean isAliving(){
		return isAliving;
	}
	public void fetch(Context context) {
		L.d(TAG, "fetch isAliving:"+isAliving);
		if (isAliving) {
			return ;
		}
		isAliving = true;
		syncFetch(context);
	}
	
	protected void syncFetch(final Context context){
		new AsyncTask<Object, Object, OreFloatingWindow>() {

			@Override
			protected OreFloatingWindow doInBackground(Object... params) {
				return fetchFloatingWindow(context);
			}
			protected void onPostExecute(OreFloatingWindow oreFloatingWindow) {
				if (null != oreFloatingWindow && 0 == oreFloatingWindow.status) {
					fetchResource(context, oreFloatingWindow);
				}else {
					isAliving = false;
				}
			};
			protected void onCancelled() {
				isAliving = false;
			};
		}.execute();
	}

	protected OreFloatingWindow fetchFloatingWindow(Context context) {
		List<OreFloatingWindow> OreFloatingWindowList = OreFloatingWindowDao.get().getOreFloatingWindowNeedMaterial();
		if (null != OreFloatingWindowList && OreFloatingWindowList.size() > 0) {
			L.d(TAG, "fetchFloatingWindow OreFloatingWindowList.size():"+OreFloatingWindowList.size());
			OreFloatingWindow oreFloatingWindow = OreFloatingWindowList.get(0);
			if (null != oreFloatingWindow) {
				oreFloatingWindow.fetch_material_num = oreFloatingWindow.fetch_material_num +1;
				if (oreFloatingWindow.fetch_material_num > Constant.FETCHOREMATERIAL_MAX_RETRY_NUM) {
					oreFloatingWindow.status = 6;
				}else {
					if (!TextUtils.isEmpty(oreFloatingWindow.ore_resource) && !TextUtils.isEmpty(oreFloatingWindow.ore_resource_md5)) {
						L.d(TAG, "fetchResource oreFloatingWindow.fetch_material_num:"+oreFloatingWindow.fetch_material_num + " oreFloatingWindow.ore_id:"+oreFloatingWindow.ore_id);
						boolean isLocalResZipComplete = checkLocalResZipComplete(oreFloatingWindow);
						if (isLocalResZipComplete) {
							oreFloatingWindow.status = 1;
							OreReport.statisticalReport(oreFloatingWindow.ore_id, 0, ReportKey.ore_fetch_material_succ);
						}
					}else {
						oreFloatingWindow.status = 3;
						//TODO 错误上报
					}
				}
				L.d(TAG, "fetchFloatingWindow oreFloatingWindow.status:"+oreFloatingWindow.status);
				OreFloatingWindowDao.get().update(oreFloatingWindow);
				return oreFloatingWindow;
			}
		}
		return null;
	}
	protected boolean checkLocalResZipComplete(OreFloatingWindow oreFloatingWindow){
		File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
		if (null == resDir) {
			//不能出现
			return false;
		}
		final String resFileName = resDir.getAbsolutePath()+"/"+oreFloatingWindow.ore_resource_md5;
		if (checkResExistAndComplete(resFileName,oreFloatingWindow.ore_resource_md5)) {
			L.d(TAG, "checkLocalResZipComplete checkResExistAndComplete true resFileName:"+resFileName);
			//解压文件
			Zip.unzip(new File(resFileName), Constant.getDir(Constant.RES_DIR));
			L.d(TAG, "checkLocalResZipComplete Zip.unzip");
			return true;
		}else{
			//本地存在被改动过的res zip
//			FileUtils.deleteFile(resFileName);
			L.d(TAG, "checkLocalResZipComplete FileUtils.deleteFile resFileName:"+resFileName);
		}
		return false;
	}
	
	protected FileHttpResponseHandler createHandler(final OreFloatingWindow oreFloatingWindow, final Context context, String resFileName) {
		return new FileHttpResponseHandler(resFileName){
			@Override
			public void onSuccess(int statusCode, byte[] binaryData) {
				super.onSuccess(statusCode, binaryData);
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
			}
			@Override
			public void onFinish() {
				super.onFinish();
				isAliving = false;
			}
		};
	}
	
	protected void fetchResource(final Context context,final OreFloatingWindow oreFloatingWindow) {
		File resDir = Constant.getDir(Constant.RES_ZIP_DIR);
		final String resFileName = resDir.getAbsolutePath()+"/"+oreFloatingWindow.ore_resource_md5;
		FileHttpResponseHandler fileHttpResponseHandler = createHandler(oreFloatingWindow, context, resFileName);
		fileHttpResponseHandler.setInterrupt(false);
		AsyncHttpClient syncHttpClient = new AsyncHttpClient();
		syncHttpClient.download(oreFloatingWindow.ore_resource.trim(), fileHttpResponseHandler);
	}
	
	protected static boolean checkResExistAndComplete(String resFileName,String fileMd5){
		File resFile = new File(resFileName);
		if (null != resFile && resFile.exists() && fileMd5.equalsIgnoreCase(Encrypt.getFileMd5(resFileName))) {
			return true;
		}
		return false;
	}
	

}
