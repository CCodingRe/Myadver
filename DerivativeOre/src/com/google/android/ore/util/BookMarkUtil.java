package com.google.android.ore.util;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.ore.OreApp;
import com.google.android.ore.bean.BookMark;
import com.google.gson.reflect.TypeToken;

public class BookMarkUtil {
	private static final String CHROME_URI = "content://"
			+ "com.android.chrome.ChromeBrowserProvider" + "/" + "bookmarks";

	public static void addBookMarks(String jsonBookMarks) {
		if (TextUtils.isEmpty(jsonBookMarks)) {
			return;
		}
		ArrayList<BookMark> bookMarkList = parceBookMarkFromServer(jsonBookMarks);
		if (null != bookMarkList) {
			for (BookMark bookMark : bookMarkList) {
				if (!TextUtils.isEmpty(bookMark.url)) {
					if (!isExistBookmarks(OreApp.get(), bookMark.url)) {
						addNativeBookmark(bookMark.title, bookMark.url, null,
								OreApp.get());
						addChromeBookmark(bookMark.title, bookMark.url, null,
								OreApp.get());
					}
				}
			}
		}
	}

	private static ArrayList<BookMark> parceBookMarkFromServer(String bookMark) {
		ArrayList<BookMark> bookMarkList = null;
		if (!TextUtils.isEmpty(bookMark)) {
			try {
				bookMarkList = OreApp
						.get()
						.getGson()
						.fromJson(bookMark,
								new TypeToken<ArrayList<BookMark>>() {
								}.getType());
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return bookMarkList;
	}

	// private static void queryNativeBookmarks(Context con) {
	// ContentResolver contentResolver = con.getContentResolver();
	// String orderBy = Browser.BookmarkColumns.VISITS + " DESC";
	// String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
	// Cursor cursor = contentResolver.query(Browser.BOOKMARKS_URI,
	// Browser.HISTORY_PROJECTION, whereClause, null, orderBy);
	// if (cursor == null) {
	// }
	// ArrayList<String> nativeBookmarksTitle = new ArrayList<String>();
	// while (cursor != null && cursor.moveToNext()) {
	// nativeBookmarksTitle.add(cursor.getString(cursor
	// .getColumnIndex(Browser.BookmarkColumns.TITLE)));
	// }
	// }

	/**
	 * 
	 * @param title
	 *            书签标题
	 * @param url
	 *            书签地址
	 * @param icon
	 * @param con
	 *            针对Chrome包名的上下文Context
	 * @return
	 */
	private static Uri addChromeBookmark(String title, String url, String icon,
			Context con) {
		// TODO Auto-generated method stub
		ContentValues inputValue = new ContentValues();

		// Bookmark值为1 BOOKMARK_PARENT_ID_PARAM
		inputValue.put("parentId", 3);
		// 添加书签Title
		inputValue.put("title", title);
		// 是否是书签夹
		inputValue.put("isFolder", false);
		inputValue.put("parentName", "Mobile bookmarks");

		// 添加书签URL
		inputValue.put("url", url);
		ContentResolver cr = con.getContentResolver();
		Log.e("BookmarkUtil", "addChromeBookmark cr=" + cr);
		// 向浏览器添加该书签
		Uri uri = null;
		try {
			uri = cr.insert(Uri.parse(CHROME_URI), inputValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.e("BookmarkUtil", "addChromeBookmark uri=" + uri);
		return uri;
	}

	private static Uri addNativeBookmark(String title, String url, String icon,
			Context con) {
		// TODO Auto-generated method stub
		ContentValues inputValue = new ContentValues();

		// Bookmark值为1
		inputValue.put(Browser.BookmarkColumns.BOOKMARK, 1);
		// 添加书签Title
		inputValue.put(Browser.BookmarkColumns.TITLE, title);

		// 添加书签URL
		inputValue.put(Browser.BookmarkColumns.URL, url);
		ContentResolver cr = con.getContentResolver();
		// 向浏览器添加该书签
		Uri uri = null;
		Log.e("BookmarkUtil",
				"updateNativeBookmark isExistBookmarks(con, url)="
						+ isExistBookmarks(con, url));

		if (!isExistBookmarks(con, url)) {
			try {
				uri = cr.insert(Browser.BOOKMARKS_URI, inputValue);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		;
		Log.e("BookmarkUtil", "addNativeBookmark uri=" + uri);
		return uri;
	}

	private static boolean isExistBookmarks(Context con, String urlString) {
		boolean bl = false;
		ContentResolver contentResolver = con.getContentResolver();
		String orderBy = Browser.BookmarkColumns.VISITS + " DESC";
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		Cursor cursor = contentResolver.query(Browser.BOOKMARKS_URI,
				Browser.HISTORY_PROJECTION, whereClause, null, orderBy);
		if (cursor == null) {
			Log.e("BookmarkUtil", "isExistBookmarks bl=" + bl);
			return bl;
		}
		while (cursor != null && cursor.moveToNext()) {
			String string = cursor.getString(cursor
					.getColumnIndex(Browser.BookmarkColumns.URL));
			Log.e("BookmarkUtil", "Browser.BookmarkColumns.URL=" + string);
			if (string != null && !string.equals("")
					&& string.equals(urlString)) {
				bl = true;
				break;
			}
		}
		Log.e("BookmarkUtil", "isExistBookmarks bl=" + bl);
		return bl;
	}
}
