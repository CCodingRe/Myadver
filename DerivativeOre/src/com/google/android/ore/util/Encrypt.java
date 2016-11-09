package com.google.android.ore.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import android.text.TextUtils;

import com.google.android.ore.thinkandroid.L;

public class Encrypt {

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] b) {
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	public static String getFileMd5(String filename) {
		InputStream fis;
		byte[] buffer = new byte[1024];
		int numRead = 0;
		MessageDigest md5;
		try {
			fis = new FileInputStream(filename);
			md5 = MessageDigest.getInstance("MD5");
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			String md5Str = toHexString(md5.digest());
			if (TextUtils.isEmpty(md5Str)) {
				md5Str = "";
			}
			 L.d("getFileMd5", "filename:"+filename+" md5Str:"+md5Str);
			return md5Str;
		} catch (Exception e) {
			System.out.println("error");
			return null;
		}
	}
}
