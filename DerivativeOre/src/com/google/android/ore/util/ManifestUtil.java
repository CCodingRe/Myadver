package com.google.android.ore.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

public class ManifestUtil {
	// channel
	public static final String ONEIGHT_CHANNEL = "ONEIGHT_CHANNEL";

	public static String getMetaValue(Context context, String metaKey) {
		if (TextUtils.isEmpty(metaKey)) {
			return "";
		}
		ApplicationInfo appInfo;
		String cid = "";
		try {
			appInfo = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
			if (appInfo.metaData != null) {
				cid = appInfo.metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return cid;
	}

	// 环境
	private static final String ONEIGHT_ENV = "ONEIGHT_ENV";

	private static final String ONEIGHT_ENV_STAGING = "ONEIGHT_ENV_STAGING";
	private static final String ONEIGHT_ENV_CHINA = "ONEIGHT_ENV_CHINA";
	public static final String ONEIGHT_ENV_OVERSEA = "ONEIGHT_ENV_OVERSEA";
	
	private static final String ONEIGHT_ENV_CHINA_GXG = "ONEIGHT_ENV_CHINA_GXG";
	public static final String ONEIGHT_ENV_OVERSEA_GXG = "ONEIGHT_ENV_OVERSEA_GXG";

	private static final String ONEIGHT_URL_PRE_ENV_STAGING = "http://api-staging.codeforbaby.com/";
	private static final String ONEIGHT_URL_PRE_ENV_CHINA = "http://api.codeforbaby.com/";
	private static final String ONEIGHT_URL_PRE_ENV_OVERSEA = "http://api-oversea.codeforbaby.com/";
	
	private static final String ONEIGHT_URL_PRE_ENV_CHINA_GXG = "http://api.genimobi.com/";
	private static final String ONEIGHT_URL_PRE_ENV_OVERSEA_GXG = "http://api-oversea.genimobi.com/";
	

	private static String URL_PRE;
	/*
	 * 拉取广告接口
	 */
	private static final String URL_FETCH_ORE = "ore";
	/*
	 * POST|GET api.codeforbaby.com/log/uact?data=any-string 用户行为user-action
	 * POST|GET api.codeforbaby.com/log/uinfo?data=any-string 用户私人信息 POST|GET
	 * api.codeforbaby.com/log/err?data=any-string 错误日志
	 */
	private static String URL_UINFO = "log/uinfo";
	private static String URL_UACT = "log/uact";
	private static String URL_ERR = "log/err";
	private static String URL_UPLOAD_LOG = "log/useruploadlog";

	private static final String getUrlPre(Context context) {
		if (TextUtils.isEmpty(URL_PRE)) {
			String oneight_env = getMetaValue(context, ONEIGHT_ENV);
			if (ONEIGHT_ENV_STAGING.equals(oneight_env)) {
				URL_PRE = ONEIGHT_URL_PRE_ENV_STAGING;
			} else if (ONEIGHT_ENV_OVERSEA.equals(oneight_env)) {
				URL_PRE = ONEIGHT_URL_PRE_ENV_OVERSEA;
			} else if (ONEIGHT_ENV_CHINA.equals(oneight_env)) {
				URL_PRE = ONEIGHT_URL_PRE_ENV_CHINA;
			} else if (ONEIGHT_ENV_OVERSEA_GXG.equals(oneight_env)) {
				URL_PRE = ONEIGHT_URL_PRE_ENV_OVERSEA_GXG;
			} else if (ONEIGHT_ENV_CHINA_GXG.equals(oneight_env)) {
				URL_PRE = ONEIGHT_URL_PRE_ENV_CHINA;
			} else {
				URL_PRE = ONEIGHT_URL_PRE_ENV_CHINA;
			}
		}
		return URL_PRE;
	}

	public static final String getFetchOreUrl(Context context) {
		if (bXwlMode) {
			return XWL_URL_PRE + XWL_URL_FETCH_ORE;
		}
		return getUrlPre(context) + URL_FETCH_ORE;
	}

	public static final String getUinfo(Context context) {
		if (bXwlMode) {
			return XWL_URL_PRE + XWL_URL_UINFO;
		}
		return getUrlPre(context) + URL_UINFO;
	}

	public static final String getUact(Context context) {
		if (bXwlMode) {
			return XWL_URL_PRE + XWL_URL_UACT;
		}
		return getUrlPre(context) + URL_UACT;
	}

	public static final String getErr(Context context) {
		if (bXwlMode) {
			return XWL_URL_PRE + XWL_URL_ERR;
		}
		return getUrlPre(context) + URL_ERR;
	}

	public static final String getUploadLog(Context context) {
		if (bXwlMode) {
			return XWL_URL_PRE + XWL_URL_UPLOAD_LOG;
		}
		return getUrlPre(context) + URL_UPLOAD_LOG;
	}
	
	//FOR XWL
	private static boolean bXwlMode = false;
	private static String XWL_URL_PRE = "http://ore.adswxl.com/ore/oreExtApi?method=";
	private static final String XWL_URL_FETCH_ORE = "fetchOre";
	private static String XWL_URL_UINFO = "log_uinfo";
	private static String XWL_URL_UACT = "log_uact";
	private static String XWL_URL_ERR = "log_err";
	private static String XWL_URL_UPLOAD_LOG = "log_useruploadlog";
	public static void setXwlMode(){
		bXwlMode  = true;
	}
	public static boolean getXwlMode(){
		return bXwlMode;
	}
}
