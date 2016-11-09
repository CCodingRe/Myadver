package com.google.android.ore.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class JsonUtil {
	public static String getString(JSONObject contentJSONObject, String key) {
		String rlt = "";
		if (contentJSONObject != null && !TextUtils.isEmpty(key)
				&& has(contentJSONObject, key)) {
			try {
				rlt = contentJSONObject.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
				rlt = "";
			}
		}
		return rlt;
	}

	public static int getInt(JSONObject contentJSONObject, String key) {
		int rlt = 0;
		if (contentJSONObject != null && !TextUtils.isEmpty(key)
				&& has(contentJSONObject, key)) {
			try {
				rlt = contentJSONObject.getInt(key);
			} catch (JSONException e) {
				e.printStackTrace();
				rlt = 0;
			}
		}
		return rlt;
	}
	
	public static JSONArray getJSONArray(JSONObject contentJSONObject, String key){
		JSONArray rlt = null;
		if (contentJSONObject != null && !TextUtils.isEmpty(key)
				&& has(contentJSONObject, key)) {
			try {
				rlt = contentJSONObject.getJSONArray(key);
			} catch (JSONException e) {
				e.printStackTrace();
				rlt = null;
			}
		}
		for (int i = 0; i < rlt.length(); i++) {
			
		}
		return rlt;
	}

	private static boolean has(JSONObject contentJSONObject, String key) {
		if (contentJSONObject.has(key) && !contentJSONObject.isNull(key)) {
			return true;
		}
		return false;
	}
}
