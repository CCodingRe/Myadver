package com.google.android.ore.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static SimpleDateFormat YMD_HMS_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat YMD_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static SimpleDateFormat YMD_HM_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	
	public static final String getCurDate_YMD_HMS() {
		return YMD_HMS_FORMAT.format(new Date());
	}

	public static final String getCurDate_YMD() {
		return YMD_FORMAT.format(new Date());
	}
	
	public static final String getLastDate_YMD() {
		Date date = new Date();
		date.setTime(System.currentTimeMillis()-24*60*60*1000);
		return YMD_FORMAT.format(date);
	}
	
	public static long getTime(String date) {
		try {
			Date dt = YMD_HM_FORMAT.parse(date);
			return dt.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static final String getCurDate_YMD_HMS(long time) {
		Date date = new Date();
		date.setTime(time);
		return YMD_HMS_FORMAT.format(date);
	}
}
