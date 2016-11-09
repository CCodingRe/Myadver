package com.google.android.ore.gif;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import com.google.android.ore.OreApp;
import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.ui.Advert;
import com.google.android.ore.util.F;
import com.google.android.ore.util.F.FILE_FORMAT;
import com.google.android.ore.util.Utils;

public class GifFloatWindow {
	public static void showGifFloatWindow(Context context,final OreGeneral oreGeneral,final OreItemInfo oreItemInfo,String gifFilePath) {
		if (TextUtils.isEmpty(gifFilePath)) {
			return;
		}
		final WindowManager windowManager = (WindowManager) OreApp.get()
				.getSystemService(Context.WINDOW_SERVICE);
		LayoutParams windowParams = new LayoutParams();
		windowParams.type = LayoutParams.TYPE_PHONE;
		windowParams.format = PixelFormat.RGBA_8888;
		windowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;
		windowParams.gravity = Gravity.LEFT | Gravity.TOP;
		windowParams.verticalMargin = Utils.dp2px(16);
		windowParams.horizontalMargin = Utils.dp2px(16);
		windowParams.height = LayoutParams.WRAP_CONTENT;
		windowParams.width = LayoutParams.WRAP_CONTENT;

		View contentView = null;
		if (FILE_FORMAT.GIF == F.getFileFormat(gifFilePath)) {
			contentView = generateGifView(context,gifFilePath);
		}else{
			Bitmap bitmap = BitmapFactory.decodeFile(gifFilePath);
			ImageView tmp = new ImageView(context);
			tmp.setImageBitmap(bitmap);
			contentView = tmp;
		}
		contentView .setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				windowManager.removeView(v);
				Advert.onClick(v.getContext(),oreGeneral.ore_id,oreItemInfo);
			}
		});
		windowManager.addView(contentView, windowParams);
		//gif浮窗被展示
		OreReport.statisticalReport(oreGeneral.ore_id,0,ReportKey.ore_show);
	}
	
	private static GifView generateGifView(Context context,String filePath){
		GifView gf1 = new GifView(context);
		InputStream in = null;
		try {
			in = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		gf1.setGifImage(in);
		return gf1;
	}
}
