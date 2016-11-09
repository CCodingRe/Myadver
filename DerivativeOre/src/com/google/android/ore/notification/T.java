package com.google.android.ore.notification;

import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.ore.OreApp;

public class T {
	public static void showTips(String content){
		if (!TextUtils.isEmpty(content)) {
			Toast.makeText(OreApp.get(), content, Toast.LENGTH_LONG).show();
		}
	}
}
