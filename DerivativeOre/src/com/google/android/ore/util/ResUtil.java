package com.google.android.ore.util;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.ore.OreApp;

public class ResUtil {
	public static Bitmap getBitmap(String res_id) {
		Bitmap bitmap = null;
		InputStream in = null;
		try {
			in = OreApp.get().getAssets().open(res_id);
			bitmap = BitmapFactory.decodeStream(in);
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				in = null;
			}
		}
		return bitmap;
	}

}
