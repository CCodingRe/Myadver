package com.google.android.ore.db.dao;

import java.sql.SQLException;

import com.google.android.ore.bean.DownLoadInfo;
import com.google.android.ore.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

public class DownLoadInfoDao {
	private Dao<DownLoadInfo, Integer> downLoadInfoDao;
	private static DownLoadInfoDao mInst;
	public static DownLoadInfoDao get(){
		if (mInst == null) {
			mInst = new DownLoadInfoDao();
		}
		return mInst;
	}
	@SuppressWarnings("unchecked")
	public DownLoadInfoDao() {
		try {
			downLoadInfoDao = DatabaseHelper.get().getDao(
					DownLoadInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public DownLoadInfo get(int id) {
		DownLoadInfo downLoadInfo = null;
		try {
			downLoadInfo = downLoadInfoDao.queryForId(id);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return downLoadInfo;
	}
}
