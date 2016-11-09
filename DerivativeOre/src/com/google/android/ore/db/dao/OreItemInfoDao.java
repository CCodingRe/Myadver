package com.google.android.ore.db.dao;

import java.sql.SQLException;

import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

public class OreItemInfoDao {
	private Dao<OreItemInfo, Integer> oreItemInfoDao;
	private static OreItemInfoDao mInst;
	public static OreItemInfoDao get(){
		if (mInst == null) {
			mInst = new OreItemInfoDao();
		}
		return mInst;
	}
	@SuppressWarnings("unchecked")
	private OreItemInfoDao() {
		try {
			oreItemInfoDao = DatabaseHelper.get().getDao(
					OreItemInfo.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public OreItemInfo get(int id) {
		OreItemInfo oreItemInfo = null;
		try {
			oreItemInfo = oreItemInfoDao.queryForId(id);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return oreItemInfo;
	}

	public boolean add(OreItemInfo oreItemInfo) {
		if (null == oreItemInfo) {
			return false;
		}
		if (null != get(oreItemInfo.ore_item_id)) {
			return false;
		}
		int insertResult = 0;
		try {
			insertResult = oreItemInfoDao.create(oreItemInfo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertResult > 0;
	}
	public boolean update(OreItemInfo oreItemInfo) {
		if (null == oreItemInfo) {
			return false;
		}
		int insertResult = 0;
		try {
			insertResult = oreItemInfoDao.update(oreItemInfo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertResult > 0;
	}
}
