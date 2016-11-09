package com.google.android.ore.exception;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

import com.google.android.ore.Constant;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.thinkandroid.L;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler mInst = new CrashHandler();
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private String TAG = CrashHandler.class.getSimpleName();

	private CrashHandler() {
	}

	public static CrashHandler get() {
		return mInst;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		saveuncaughtException(thread, ex);
	}

	private void saveuncaughtException(Thread thread, Throwable ex) {
		if (null == ex) {
			return;
		}
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		StringBuffer sb = new StringBuffer();
		sb.append("<-----------saveuncaughtException start -------------->");
		sb.append(result);
		sb.append("<-----------saveuncaughtException end -------------->");
		OreReport.errReport(sb.toString());
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-" + time + "-" + timestamp + ".log";

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				File dir = Constant.getDir("crash");
				FileOutputStream fos = new FileOutputStream(dir.getAbsolutePath()+"/" + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
		} catch (Exception e) {
			L.d(TAG  , "saveuncaughtException Exception "+ e.getMessage());
		}
	}
}
