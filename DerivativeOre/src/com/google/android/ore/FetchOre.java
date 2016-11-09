package com.google.android.ore;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.UUID;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.ore.bean.BaseResult;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.dao.OreFloatingWindowDao;
import com.google.android.ore.db.dao.OreGeneralDao;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.AsyncHttpClient;
import com.google.android.ore.thinkandroid.http.AsyncHttpResponseHandler;
import com.google.android.ore.thinkandroid.http.RequestParams;
import com.google.android.ore.util.FileUtils;
import com.google.android.ore.util.ManifestUtil;
import com.google.android.ore.util.P;
import com.google.android.ore.util.TeaUtil;
import com.google.gson.reflect.TypeToken;

public class FetchOre {
	protected volatile boolean isAliving = false;
	public static final String TAG = FetchOre.class.getSimpleName();

	public boolean isAliving() {
		return isAliving;
	}

	protected AsyncHttpResponseHandler createHandler(final Context context) {
		return new AsyncHttpResponseHandler() {
			private boolean isSyncHandleContent = false;

			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				if (content == null) {
					content = "";
				}
				L.d(TAG, "fetchOre onSuccess content=" + content + " statusCode=" + statusCode);
				if (statusCode == 200 && !TextUtils.isEmpty(content)) {
					// content = TeaUtil.decryptString(content,
					// TeaUtil.KEY);
					isSyncHandleContent = true;
					syncHandleContent(content);
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				if (content == null) {
					content = "";
				}
				String errMsg = "";
				if (error != null) {
					errMsg = error.getMessage();
				}
				L.d(TAG, "fetchOre onFailure content=" + content + " error=" + errMsg);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				if (!isSyncHandleContent) {
					isAliving = false;
				}
			}
		};
	}

	protected void noFetch(Context context) {

	}

	public void fetch(final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				uploadLog(context);
			}
			
		}).start();
		
		if (isAliving && !canForceFetch()) {
			L.d(TAG, "fetch fail, isAliving: " + isAliving);
			return;
		}
		if (!canFetch()) {
			L.d(TAG, "fetch fail, don't fetch");
			noFetch(context);
			return;
		}
		isAliving = true;
		P.putLong(false, P.CURR_DAY_FETCH_ORE_TIME, System.currentTimeMillis());
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(ManifestUtil.getFetchOreUrl(OreApp.get()), getOreRequestParams(context), createHandler(context));
	}

	private boolean canFetch() {
		if (OreApp.get().getDebug()) {
			return true;
		}
		long fetchOreTime = P.getLong(false, P.CURR_DAY_FETCH_ORE_TIME, 0L);
		if (System.currentTimeMillis() - fetchOreTime > OreApp.get().getOreConfig().fetch_ore_succ_interval * 60
				* 1000) {
			return true;
		}
		return false;
	}

	/*
	 * 如果1个小时都未曾向服务器发起请求，强制发起一次,针对异常情况的强制处理
	 */
	private boolean canForceFetch() {
		long fetchOreTime = P.getLong(false, P.CURR_DAY_FETCH_ORE_TIME, 0L);
		if (System.currentTimeMillis() - fetchOreTime > 1 * 60 * 60 * 1000) {
			return true;
		}
		return false;
	}

	private RequestParams getOreRequestParams(Context context) {
		String mache_info = OreApp.get().getGson().toJson(OreApp.get().getMacheInfo());
		String sdk_info = OreApp.get().getGson().toJson(OreApp.get().getSdkInfo());
		String uuid = UUID.randomUUID().toString();
		String operator = OreApp.get().getGson().toJson(OreApp.get().getOperator());
		L.d(TAG, "mache_info=" + mache_info);
		L.d(TAG, "sdk_info=" + sdk_info);
		L.d(TAG, "uuid=" + uuid);
		if (ManifestUtil.getXwlMode()) {
			 mache_info = TeaUtil.encryptString(mache_info, TeaUtil.KEY);
			 sdk_info = TeaUtil.encryptString(sdk_info, TeaUtil.KEY);
			 uuid = TeaUtil.encryptString(uuid, TeaUtil.KEY);
			 operator = TeaUtil.encryptString(operator, TeaUtil.KEY);
		}
		RequestParams requestParams = new RequestParams();
		requestParams.put("m", mache_info);
		requestParams.put("s", sdk_info);
		requestParams.put("uuid", uuid);
		requestParams.put("operator", operator);
		if (OreApp.get().getDebug()) {
			requestParams.put("debug", "2");
		}
		return requestParams;
	}

	protected void syncHandleContent(final String content) {
		new AsyncTask<Object, Object, Object>() {

			@Override
			protected Object doInBackground(Object... params) {
				handleContent(content);
				return null;
			}

			protected void onPostExecute(Object result) {
				isAliving = false;
			};

			@Override
			protected void onCancelled() {
				super.onCancelled();
				isAliving = false;
			}
		}.execute();
	}

	public void handleContent(String content) {
		if (ManifestUtil.getXwlMode()) {
			content = TeaUtil.decryptString(content, TeaUtil.KEY);
			L.d(TAG, "handleContent content=" + content);
		}
		final BaseResult baseResult = new BaseResult(content);
		if (0 == baseResult.ret && !TextUtils.isEmpty(baseResult.content)) {
			// 更新配置信息
			OreApp.get().setOreConfig(baseResult.ore_config);
			// 浮窗广告
			handleFloatingWindow(baseResult);
			// 一般性广告
			handleGeneralList(baseResult);
		}
	}

	/*
	 * 一般性广告
	 */
	private void handleGeneralList(BaseResult baseResult) {
		if (!TextUtils.isEmpty(baseResult.ore_general_list)) {
			Type type = new TypeToken<ArrayList<OreGeneral>>() {
			}.getType();
			ArrayList<OreGeneral> ore_general_list = OreApp.get().getGson().fromJson(baseResult.ore_general_list, type);
			if (null != ore_general_list && !ore_general_list.isEmpty()) {
				int count = ore_general_list.size();
				int insertSuccNum = 0;
				int havedNum = 0;
				for (int i = 0; i < count; i++) {
					OreGeneral ore_general_item = ore_general_list.get(i);
					if (null != ore_general_item) {
						boolean rlt = OreGeneralDao.get().add(ore_general_item);
						if (rlt) {
							insertSuccNum = insertSuccNum + 1;
							if (null != ore_general_item.ore_item) {
								boolean rltOreItemInfo = OreItemInfoDao.get().add(ore_general_item.ore_item);
								if (rltOreItemInfo) {
									L.d(TAG, "handleGeneralList OreItemInfoDao.get().add succ "
											+ ore_general_item.ore_item.ore_item_id);
								}
							}
						} else {
							havedNum = havedNum + 1;
						}
					}
				}
				L.d(TAG, "handleGeneralList OreGeneral insertSuccNum=" + insertSuccNum + " total:" + count
						+ " havedNum:" + havedNum);
			}
		}
	}

	/*
	 * 浮窗广告
	 */
	private void handleFloatingWindow(BaseResult baseResult) {
		if (!TextUtils.isEmpty(baseResult.ore_floating_window)) {
			OreFloatingWindow oreFloatingWindow = OreApp.get().getGson().fromJson(baseResult.ore_floating_window,
					OreFloatingWindow.class);
			if (null == oreFloatingWindow.ore_item_id_list || null == oreFloatingWindow.ore_item_list) {
				return;
			}
			oreFloatingWindow.ore_item_id_list_json = OreApp.get().getGson().toJson(oreFloatingWindow.ore_item_id_list);
			OreFloatingWindowDao.get().add(oreFloatingWindow);
			int count = oreFloatingWindow.ore_item_list.size();
			int insertSuccNum = 0;
			int havedNum = 0;
			for (int i = 0; i < count; i++) {
				OreItemInfo ore_item_list_item = oreFloatingWindow.ore_item_list.get(i);
				if (null != ore_item_list_item) {
					boolean rlt = OreItemInfoDao.get().add(ore_item_list_item);
					if (rlt) {
						insertSuccNum = insertSuccNum + 1;
					} else {
						havedNum = havedNum + 1;
					}
				}
			}
			L.d(TAG, "handleContent OreInfo insertSuccNum=" + insertSuccNum + " total:" + count + " havedNum:"
					+ havedNum);
		}
	}

	volatile boolean isUploading = false;
	int MAX_UPLOAD_TIMES = 5;
	private void uploadLog(Context context) {
		String lastSuccessLogUploadTime = P.getString(true, P.LAST_SUCCESS_LOG_UPLOAD_TIME, "");
		boolean isUploaded = lastSuccessLogUploadTime.equals(OreApp.get().getOreConfig().upload_log_time);
		int time = P.getInt(true, P.UPLOAD_LOG_FAIL_TIMES, 0);
		if (time >= MAX_UPLOAD_TIMES) {
			return;
		}
		if (isUploading) {
			return;
		}
		if (isUploaded) {
			return;
		}
		if (TextUtils.isEmpty(OreApp.get().getOreConfig().user_id)) {
			return;
		}
		long curTime = System.currentTimeMillis();
		long uploadTime = OreApp.get().getLogUploadTime();
		if (curTime >= uploadTime) {
			isUploading = true;
			String fileName = getLogFileName();
			if (!TextUtils.isEmpty(fileName)) {
				AsyncHttpClient client = new AsyncHttpClient();
				client.post(ManifestUtil.getUploadLog(OreApp.get()), getOreUploadLogRequestParams(context, fileName), new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						super.onSuccess(statusCode, content);
						FileUtils.clearDirectory(L.getLogFolder());
						P.putString(true, P.LAST_SUCCESS_LOG_UPLOAD_TIME, OreApp.get().getOreConfig().upload_log_time);
						P.putInt(true, P.UPLOAD_LOG_FAIL_TIMES, 0);
					}
					
					@Override
					public void onFailure(Throwable error, String content) {
						super.onFailure(error, content);
						int time = P.getInt(true, P.UPLOAD_LOG_FAIL_TIMES, 0);
						P.putInt(true, P.UPLOAD_LOG_FAIL_TIMES, time++);
					}
					
					@Override
					public void onFinish() {
						super.onFinish();
						isUploading = false;
					}
				});
			}
			
			
		}
	}
	
	private String getLogFileName() {
		String fileName = "";
		File folder = new File(L.getLogFolder());
		if (folder.exists()) {
			File subFile[] = folder.listFiles();
			int fileNum = subFile.length;
			TreeMap<Long,File> tm = new TreeMap<Long,File>();
			for (int i = 0; i < fileNum; i++) {
				Long tempLong = new Long(subFile[i].lastModified());
				tm.put(tempLong, subFile[i]);
			}
			if (!tm.isEmpty()) {
				fileName = tm.get(tm.lastKey()).getPath();
			}
		}
		return fileName;
	}

	private RequestParams getOreUploadLogRequestParams(Context context, String fileName) {
		RequestParams requestParams = new RequestParams();
		requestParams.put("user_id", OreApp.get().getOreConfig().user_id);
		String content = FileUtils.read(fileName);
		requestParams.put("data", content);
		return requestParams;
	}
}
