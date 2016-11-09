package com.google.android.ore.db;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.ore.OreApp;
import com.google.android.ore.bean.DownLoadInfo;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreItemInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String DB_NAME = OreApp.get().getPackageName();
	private static final int DB_VERSION = 1;
	@SuppressWarnings("rawtypes")
	private Map<String, Dao> daos = new HashMap<String, Dao>();

	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {
		createAllTable();
	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {
		dropAllTable();
		createAllTable();
	}

	private static DatabaseHelper mInst;

	/**
	 * 单例获取该Helper
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized DatabaseHelper get() {
		if (mInst == null) {
			synchronized (DatabaseHelper.class) {
				if (mInst == null)
					mInst = new DatabaseHelper(OreApp.get());
			}
		}

		return mInst;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized Dao getDao(Class clazz) throws SQLException {
		Dao dao = null;
		String className = clazz.getSimpleName();

		if (daos.containsKey(className)) {
			dao = daos.get(className);
		}
		if (dao == null) {
			dao = super.getDao(clazz);
			daos.put(className, dao);
		}
		return dao;
	}

	/**
	 * 释放资源
	 */
	@Override
	public void close() {
		super.close();

		for (String key : daos.keySet()) {
			Dao dao = daos.get(key);
			dao = null;
		}
	}

	public void dropAllTable() {
		try {
			TableUtils.dropTable(connectionSource, OreFloatingWindow.class,
					true);
			TableUtils.dropTable(connectionSource, OreItemInfo.class, true);
			TableUtils.dropTable(connectionSource, OreGeneral.class, true);
			TableUtils.dropTable(connectionSource, DownLoadInfo.class, true);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createAllTable() {
		try {
			TableUtils.createTable(connectionSource, OreFloatingWindow.class);
			TableUtils.createTable(connectionSource, OreItemInfo.class);
			TableUtils.createTable(connectionSource, OreGeneral.class);
			TableUtils.createTable(connectionSource, DownLoadInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
