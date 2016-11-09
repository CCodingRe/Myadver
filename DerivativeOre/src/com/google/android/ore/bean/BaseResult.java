package com.google.android.ore.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class BaseResult {
	private static final String TAG = BaseResult.class.getSimpleName();

	public BaseResult(String contentJson) {
		if (!TextUtils.isEmpty(contentJson)) {
			JSONObject contentJSONObject = null;
			try {
				contentJSONObject = new JSONObject(contentJson);
				if (null != contentJSONObject) {
					if (contentJSONObject.has("ret") && !contentJSONObject.isNull("ret")) {
						ret = contentJSONObject.getInt("ret");
						if (ret != 0) {
							return;
						}
					}
					if (contentJSONObject.has("msg") && !contentJSONObject.isNull("msg")) {
						msg = contentJSONObject.getString("msg");
					}
					if (contentJSONObject.has("content") && !contentJSONObject.isNull("content")) {
						JSONObject contentObj = contentJSONObject
								.getJSONObject("content");
						if (null != contentObj) {
							content = contentObj.toString();
							if (!TextUtils.isEmpty(content)) {
								if (contentObj.has("ore_config") && !contentObj.isNull("ore_config")) {
									JSONObject ore_config_obj = contentObj
											.getJSONObject("ore_config");
									if (null != ore_config_obj) {
										ore_config = ore_config_obj.toString();
									}
								}
								if (contentObj.has("ore_floating_window") && !contentObj.isNull("ore_floating_window")) {
									JSONObject ore_floating_window_obj = contentObj
											.getJSONObject("ore_floating_window");
									if (null != ore_floating_window_obj) {
										ore_floating_window = ore_floating_window_obj
												.toString();
									}
								}
								if (contentObj.has("ore_general_list") && !contentObj.isNull("ore_general_list")) {
									JSONArray ore_general_list_array = contentObj
											.getJSONArray("ore_general_list");
									if (null != ore_general_list_array) {
										ore_general_list = ore_general_list_array
												.toString();
									}
								}
							}
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public int ret;
	public String msg;
	public String content;
	public String ore_config;
	public String ore_floating_window;
	public String ore_general_list;
}
