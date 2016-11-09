package com.google.android.ore.db.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.google.android.ore.Constant;
import com.google.android.ore.OreApp;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.db.DatabaseHelper;
import com.j256.ormlite.dao.Dao;

public class OreFloatingWindowDao {
	private Dao<OreFloatingWindow, Integer> oreFloatingWindowDao;
	private static OreFloatingWindowDao mInst;

	public static OreFloatingWindowDao get() {
		if (mInst == null) {
			mInst = new OreFloatingWindowDao();
		}
		return mInst;
	}

	@SuppressWarnings("unchecked")
	private OreFloatingWindowDao() {
		try {
			oreFloatingWindowDao = DatabaseHelper.get().getDao(
					OreFloatingWindow.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public OreFloatingWindow get(int id) {
		OreFloatingWindow oreFloatingWindow = null;
		try {
			oreFloatingWindow = oreFloatingWindowDao.queryForId(id);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return oreFloatingWindow;
	}

	public void add(OreFloatingWindow oreFloatingWindow) {
		if (null == oreFloatingWindow) {
			return;
		}
		if (null != get(oreFloatingWindow.ore_id)) {
			return;
		}
		try {
			oreFloatingWindowDao.create(oreFloatingWindow);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean delete(int id) {
		int deleteResult = 0;
		try {
			deleteResult = oreFloatingWindowDao.deleteById(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return deleteResult > 0;
	}
	
	public boolean update(OreFloatingWindow oreFloatingWindow) {
		int relt = 0;
		try {
			relt = oreFloatingWindowDao.update(oreFloatingWindow);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return relt > 0;
	}

	public ArrayList<OreFloatingWindow> getOreFloatingWindowNeedMaterial() {
		try {
			return (ArrayList<OreFloatingWindow>) oreFloatingWindowDao
					.queryBuilder().orderBy("ore_id", true).limit(1L).where()
					.eq("status", 0)
					.and()
					.lt("fetch_material_num", Constant.ORE_FLOATING_WINDOW_MAX_RETRY_NUM)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<OreFloatingWindow> getOreFloatingWindowNeedMaterialNew() {
		try {
			return (ArrayList<OreFloatingWindow>) oreFloatingWindowDao
					.queryBuilder().orderBy("ore_id", true).limit(1L).where()
					.lt("status", OreFloatingWindow.FLOATWINDOW_SHOW_MAX)
					.and()
					.lt("fetch_material_num", Constant.ORE_FLOATING_WINDOW_MAX_RETRY_NUM)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<OreFloatingWindow> getOreFloatingWindowNeedShow() {
		try {
			return (ArrayList<OreFloatingWindow>) oreFloatingWindowDao
					.queryBuilder().orderBy("ore_id", true).limit(1L).where()
					.eq("status", OreFloatingWindow.FLOATWINDOW_RESOURCE_READY)
					.and()
					.lt("show_num", OreApp.get().getOreConfig().floating_window_max_show_num)
					.query();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
