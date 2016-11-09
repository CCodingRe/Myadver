package com.google.android.ore.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.text.TextUtils;

import com.google.android.ore.OreApp;

public class MYNotificationManager {
	private static NotificationManager nm = (NotificationManager) OreApp.get()
			.getSystemService(Context.NOTIFICATION_SERVICE);

	public static void noticeTxt(int id, String title, String content,
			PendingIntent deleteIntent) {
		if (TextUtils.isEmpty(title)) {
			title = "";
		}
		if (TextUtils.isEmpty(content)) {
			content = "";
		}
		Notification noti = new Notification.Builder(OreApp.get())
				.setContentTitle(title)
				.setContentText(content)
				.setTicker(title)
				.setSmallIcon(android.R.drawable.stat_sys_download_done)
				.setAutoCancel(true)
				.setDeleteIntent(deleteIntent)
				.build();
		nm.notify(id, noti);
	}
}
