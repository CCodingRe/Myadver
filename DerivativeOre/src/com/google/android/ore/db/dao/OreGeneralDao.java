package com.google.android.ore.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.LogManager;

import com.google.android.ore.Constant;
import com.google.android.ore.SlientInstall;
import com.google.android.ore.bean.OreGeneral;
import com.google.android.ore.bean.OreType;
import com.google.android.ore.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.Where;

public class OreGeneralDao {
	private Dao<OreGeneral, Integer> oreGeneralDao;
	private static OreGeneralDao mInst;

	public static OreGeneralDao get() {
		if (mInst == null) {
			mInst = new OreGeneralDao();
		}
		return mInst;
	}

	@SuppressWarnings("unchecked")
	private OreGeneralDao() {
		try {
			oreGeneralDao = DatabaseHelper.get().getDao(OreGeneral.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public OreGeneral get(int id) {
		OreGeneral OreGeneral = null;
		try {
			OreGeneral = oreGeneralDao.queryForId(id);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return OreGeneral;
	}

	public boolean add(OreGeneral oreGeneral) {
		if (null == oreGeneral) {
			return false;
		}
		if (null != get(oreGeneral.ore_id)) {
			return false;
		}
		int insertResult = 0;
		try {
			insertResult = oreGeneralDao.create(oreGeneral);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertResult > 0;
	}

	public boolean update(OreGeneral oreGeneral) {
		if (null == oreGeneral) {
			return false;
		}
		int insertResult = 0;
		try {
			insertResult = oreGeneralDao.update(oreGeneral);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertResult > 0;
	}

	public ArrayList<OreGeneral> getOreGeneralActivation() {
		try {
			return (ArrayList<OreGeneral>) oreGeneralDao.queryBuilder()
					.orderBy("ore_id", true).limit(1L).where().eq("status", 0)
					.and().eq("ore_type", OreType.ACTIVATION).query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public OreGeneral getOreGeneral() {
		try {
			ArrayList<OreGeneral> oreGeneralList = (ArrayList<OreGeneral>) oreGeneralDao
					.queryBuilder().orderBy("ore_id", true).limit(1L).where()
					.lt("status", 2).and().gt("ore_type", OreType.GENERAL_ORE)
					.and().lt("retry_num", Constant.OREGENERAL_MAX_RETRY_NUM)
					.query();
			if (null != oreGeneralList && oreGeneralList.size() > 0) {
				return oreGeneralList.get(0);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public OreGeneral getOreGeneralSlient() {
		try {
			Where<OreGeneral, Integer> where = oreGeneralDao.queryBuilder()
					.orderBy("ore_id", true).limit(1L).where();
			where.and(
					where.lt("status", SlientInstall.SLIENT_APK_INSTALL_SUCCESS)
							.and()
							.lt("retry_num", Constant.OREGENERAL_MAX_RETRY_NUM),
					where.or(where.eq("ore_type", OreType.SILENT_INSTALL),
							where.eq("ore_type",
									OreType.INSTALL_APP_BY_BROADCAST_RECEIVER)));
			ArrayList<OreGeneral> oreGeneralList = (ArrayList<OreGeneral>) where
					.query();
			if (null != oreGeneralList && oreGeneralList.size() > 0) {
				return oreGeneralList.get(0);
			}
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
