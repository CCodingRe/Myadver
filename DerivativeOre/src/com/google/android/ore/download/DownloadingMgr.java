package com.google.android.ore.download;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.text.TextUtils;

public class DownloadingMgr {
	@SuppressLint("UseSparseArrays") private static HashMap<Integer, String> downloading_url = new HashMap<Integer, String>();

	public static boolean isDownloading(String url) {
		if (downloading_url.containsKey(url.hashCode())) {
			return true;
		}
		return false;
	}

	public static boolean addDownloading(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		}
		if (isDownloading(url)) {
			return false;
		} else {
			downloading_url.put(url.hashCode(), url);
			return true;
		}
	}

	public static void removeDownloading(String url) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		downloading_url.remove(url.hashCode());
	}
}
