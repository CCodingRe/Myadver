package com.google.android.ore.util;

import java.util.Collection;
import java.util.Map;

import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.android.ore.OreApp;

public class Utils {

	public static final long ONE_DAY = 24 * 60 * 60 * 1000;
	private static int screenHeight;
	private static int screenWidth;
	private static float density;

	public static boolean isEmpty(Collection<?> coll) {
		return coll == null || coll.isEmpty();
	}

	public static boolean isEmpty(Object[] arr) {
		return arr == null || arr.length == 0;
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public static int getHeight() {
		if (screenHeight <= 0) {
			getScreenInfo();
		}
		return screenHeight;
	}

	public static int getWidth() {
		if (screenWidth <= 0) {
			getScreenInfo();
		}
		return screenWidth;
	}

	public static int dp2px(float dp) {
		if (density <= 0) {
			getScreenInfo();
		}
		return (int) (dp * density);
	}

	private static void getScreenInfo() {
		WindowManager wm = (WindowManager) OreApp.get().getSystemService(OreApp.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		screenHeight = Math.max(metrics.heightPixels, metrics.widthPixels);
		screenWidth = Math.min(metrics.heightPixels, metrics.widthPixels);
		density = metrics.density;
	}
}
