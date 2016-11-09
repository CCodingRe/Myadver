package com.google.android.ore.util;

import java.util.HashMap;
import java.util.Iterator;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Browser;
import android.util.Log;

public class BookmarkUtil_BackUp {
	static String[] title;
	static String[] url;
	static HashMap<Integer, String> listTitle;
	static int x;
	public static String chromeUri = "content://"
			+ "com.android.chrome.ChromeBrowserProvider" + "/" + "bookmarks";

	/*
	 * 添加浏览器书签
	 */

	public static void shuqian(Context context) {
		try {
			shuqianappInfo(context);
			queryNativeBookmarks(context);
			String shuju = null;
			qwe: for (int w = 0; w <= title.length; w++) {
				int i = 1;
				Iterator<?> iter = listTitle.entrySet().iterator();
				while (iter.hasNext()) {
					if (i < x) {
						shuju = listTitle.get(i++);
						if (title[w].equals(shuju)) {
							// Toast.makeText(this, "2", 1).show();
							continue qwe;
						}
					} else {
						break;
					}

				}

				if (!title[w].equals(shuju)) {
					addNativeBookmark(title[w], url[w],null, context);
				} else {

				}
			}
			return;
		} catch (Exception e) {
			// TODO: handle exception

		}
	}
	public static void addBookmark(Context context){
		/*
		 * 增加
		 * shuqianappInfo(context);
		for (int i = 0; i < title.length; i++) {
			
			addChromeBookmark(title[i], url[i], context);
		}*/
		
//		updateChromeBookmark(context, "myTitle2","Http://www.baidu.com", 26);
//		deleteChromeBookmark(context, 26);
		
//		updateNativeBookmark("haoya", "Http://www.baidu.com", context);
//		deleteNativeBookmark(context);
		deleteAllNativeBookmark(context);
	}

	public static Uri addNativeBookmark(String title, String url,String icon, Context con) {
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
		Uri uri=null; 
		Log.e("BookmarkUtil", "updateNativeBookmark isExistBookmarks(con, url)=" + isExistBookmarks(con, url)); 
		
       if (!isExistBookmarks(con, url)) {
    	   try {
    		   uri= cr.insert(Browser.BOOKMARKS_URI, inputValue);
    	   } catch (Exception e) {
    		   // TODO Auto-generated catch block
    		   e.printStackTrace();
    	   }
	} ;
		Log.e("BookmarkUtil", "addNativeBookmark uri=" + uri); 
		return uri;
	}
	public static int updateNativeBookmark(String newTitle, String newUrl,String icon, Context con,String oldUrl) {
		// TODO Auto-generated method stub
		ContentValues inputValue = new ContentValues();
		
		// Bookmark值为1
		inputValue.put(Browser.BookmarkColumns.BOOKMARK, 1);
		// 添加书签Title
		inputValue.put(Browser.BookmarkColumns.TITLE, newTitle);
		
		// 添加书签URL
		inputValue.put(Browser.BookmarkColumns.URL, newUrl);
		ContentResolver cr = con.getContentResolver();
		
		// 向浏览器添加该书签
		int code=0;
		try {
			code=cr.update(Browser.BOOKMARKS_URI, inputValue, Browser.BookmarkColumns.URL+"=?", new String[]{oldUrl});
			Log.e("BookmarkUtil", "updateNativeBookmark code=" + code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return code;
	}
	public static int deleteNativeBookmark(Context con,String url) {
		// TODO Auto-generated method stub
		ContentResolver cr = con.getContentResolver();
		
		// 向浏览器添加该书签
	   int code=0;
	   try {
		code=cr.delete(Browser.BOOKMARKS_URI, Browser.BookmarkColumns.URL+"=?", new String[]{url});
		   Log.e("BookmarkUtil", "deleteNativeBookmark code=" + code);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	   return code;
	}
	public static int deleteAllNativeBookmark(Context con) {
		// TODO Auto-generated method stub
		ContentResolver cr = con.getContentResolver();
		
		// 向浏览器添加该书签
		int code=0;
		try {
			code=cr.delete(Browser.BOOKMARKS_URI, Browser.BookmarkColumns.URL+"=?", new String[]{"#"});
			Log.e("BookmarkUtil", "deleteAllNativeBookmark code=" + code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return code;
	}

	@SuppressLint("UseSparseArrays")
	public static void queryNativeBookmarks(Context con) {
		ContentResolver contentResolver = con.getContentResolver();
		String orderBy = Browser.BookmarkColumns.VISITS + " DESC";
		String whereClause = Browser.BookmarkColumns.BOOKMARK + " = 1";
		Cursor cursor = contentResolver.query(Browser.BOOKMARKS_URI,
				Browser.HISTORY_PROJECTION, whereClause, null, orderBy);
		if (cursor == null) {
		}
		listTitle = new HashMap<Integer, String>();
		x = 1;
		while (cursor != null && cursor.moveToNext()) {
			listTitle.put(x++, cursor.getString(cursor
					.getColumnIndex(Browser.BookmarkColumns.TITLE)));
		}

	}
	public static boolean isExistBookmarks(Context con,String urlString){
		boolean bl=false;
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
			String string= cursor.getString(cursor
					.getColumnIndex(Browser.BookmarkColumns.URL));
			Log.e("BookmarkUtil", "Browser.BookmarkColumns.URL=" +string);
			if (string!=null&&!string.equals("")&&string.equals(urlString)) {
				bl=true;
				break;
			}
		}
		Log.e("BookmarkUtil", "isExistBookmarks bl=" + bl);
		return bl;
	}

	/**
	 * 
	 * @param title 书签标题
	 * @param url   书签地址	
	 * @param icon  
	 * @param con   针对Chrome包名的上下文Context
	 * @return
	 */
	public static Uri addChromeBookmark(String title, String url,String icon, Context con) {
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
		Uri uri =null;
		try {
			uri= cr.insert(Uri.parse(chromeUri), inputValue);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("BookmarkUtil", "addChromeBookmark uri=" + uri);
		return uri;
	}
	/**
	 * 
	 * @param con   针对Chrome包名的上下文Context
	 * @param title testtitle
	 * @param url http://www.baidu.com
	 * @param bookID 需要替换的书签ID
	 */
    public static int updateChromeBookmark(Context con,String title,String url,String icon,int bookID){

        ContentValues inputValue = new ContentValues();
			//Bookmark值为1 BOOKMARK_PARENT_ID_PARAM
			inputValue.put("parentId", 3);
			//添加书签Title
			inputValue.put("title", title);
			//是否是书签夹
			inputValue.put("isFolder", false);
			inputValue.put("parentName", "Mobile bookmarks");
			
			//添加书签URL
			inputValue.put("url", url);
			ContentResolver cr = con.getContentResolver();
			Log.e("BookmarkUtil", "con="+con.getPackageName());
			Log.e("BookmarkUtil", "chromeUri="+chromeUri);
			Log.e("BookmarkUtil", "cr="+cr);
			int code=0;
			try {
				code=cr.update(Uri.parse(chromeUri+"/"+bookID), inputValue, null, null);
				Log.e("BookmarkUtil", "code="+code);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return code;
    }
    /**
     * 
     * @param con     针对Chrome包名的上下文Context
     * @param bookID  要删除的书签ID
     * @return
     */
	public static int deleteChromeBookmark(Context con,int bookID){
		ContentResolver cr = con.getContentResolver();
		Log.e("BookmarkUtil", "con="+con.getPackageName());
		Log.e("BookmarkUtil", "chromeUri="+chromeUri);
		Log.e("BookmarkUtil", "cr="+cr);
		int code=0;
		try {
			code=cr.delete(Uri.parse(chromeUri+"/"+bookID), null, null);
			Log.e("BookmarkUtil", "code="+code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	public static void shuqianappInfo(Context mCtx) {
		ApplicationInfo appInfo;
		try {
			appInfo = mCtx.getPackageManager().getApplicationInfo(
					mCtx.getPackageName(), PackageManager.GET_META_DATA);
			String sac = appInfo.metaData.getString("SPACE_ADS_CHANNEL")
					+ "&type=bookmarks";
			String url1 = "http://www.2048kg.com/html/detail.php?gameId=25&channelId="
					+ sac;
			String url2 = "http://www.2048kg.com/?channelId=" + sac;
			String url3 = "http://www.2048kg.com/html/detail.php?gameId=26&channelId="
					+ sac;
			String url4 = "http://www.2048kg.com/html/detail.php?gameId=28&channelId="
					+ sac;
			String url5 = "http://www.2048kg.com/html/detail.php?gameId=7&channelId="
					+ sac;
			String url6 = "http://www.2048kg.com/html/detail.php?gameId=41&channelId="
					+ sac;
			String url7 = "http://www.2048kg.com/html/detail.php?gameId=34&channelId="
					+ sac;
			String url8 = "http://www.2048kg.com/html/detail.php?gameId=8&channelId="
					+ sac;
			String url9 = "http://www.2048kg.com/html/detail.php?gameId=22&channelId="
					+ sac;
			String url10 = "http://www.2048kg.com/html/detail.php?gameId=31&channelId="
					+ sac;
			url = new String[] { url1, url2, url3, url4, url5, url6, url7,
					url8, url9, url10 };
			title = new String[] { "JUMP HIGER", "2048kg", "BUBBLEFISH",
					"SHADOW MISSION", "2048", "Super Brain", "Fruit Ninja",
					"Cross Road", "Ninja", "Super Bulbs" };

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Context  initContext(Context con,String browserName){
		try {
			Context context = con.createPackageContext(browserName, Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
			if (context!=null) {
				Log.e("BookmarkUtil", "start initContext context="+context);
				return context;
			}else{
				Log.e("BookmarkUtil", "start initContext context="+context);
				return null;
			}
			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
