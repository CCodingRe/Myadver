package com.google.android.ore;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.Application;
import android.os.Handler;
import android.text.TextUtils;

import com.google.android.ore.bean.MacheInfo;
import com.google.android.ore.bean.Operator;
import com.google.android.ore.bean.OreConfig;
import com.google.android.ore.bean.SdkInfo;
import com.google.android.ore.exception.CrashHandler;
import com.google.android.ore.process.thirdlib.Polling.PollingService;
import com.google.android.ore.process.thirdlib.Polling.PollingUtils;
import com.google.android.ore.report.bean.BaseStatistical;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.log.TAPrintToFileLogger;
import com.google.android.ore.util.DateUtil;
import com.google.android.ore.util.P;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class OreApp extends Application {
	private final String TAG = OreApp.class.getSimpleName();
	private static OreApp mInst;
	private TAPrintToFileLogger mTAPrintToFileLogger = new TAPrintToFileLogger();
	private MacheInfo mMacheInfo;
	private SdkInfo mSdkInfo;
	private Gson mGson;
	private OreConfig mOreConfig;
	private BaseStatistical mBaseStatistical;
	private boolean isDbug = false;
	private boolean isPrintOreLocalLog = false;
	private long logUploadTime = -1;
	private Handler mHandler = new Handler();
	private Operator mOperator;

	@Override
	public void onCreate() {
		super.onCreate();
		mInst = OreApp.this;
		isDbug = isDebug();
		isPrintOreLocalLog = isPrintOreLocalLog();
		// FOR java.io.IOException: open failed: EBUSY (Device or resource busy)
		// in android
		Constant.getDir("");
		// 捕获崩溃异常
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.get());
		mMacheInfo = new MacheInfo(OreApp.this);
		mSdkInfo = new SdkInfo(OreApp.this);
		mOperator = new Operator();
		mBaseStatistical = new BaseStatistical(mMacheInfo, mSdkInfo);
		GsonBuilder gb = new GsonBuilder();
		mGson = gb.create();
		mOreConfig = getGson().fromJson(P.getString(true, P.ORE_CONFIG, ""),
				OreConfig.class);
		if (mOreConfig == null) {
			mOreConfig = new OreConfig();
		}
		logUploadTime = P.getLong(true, P.LOG_UPLOAD_TIME, -1);
	}
	
	public boolean getDebug() {
		return isDbug;
	}

	public boolean getPrintOreLocalLog() {
		return isPrintOreLocalLog;
	}

	public boolean isLog() {
		return mOreConfig.open_log && mOreConfig.log_user_id != null
				&& mOreConfig.log_user_id.equals(mOreConfig.user_id);
	}

	public long getLogUploadTime() {
		return logUploadTime;
	}

	public static OreApp get() {
		return mInst;
	}

	public TAPrintToFileLogger getTAPrintToFileLogger() {
		return mTAPrintToFileLogger;
	}

	public MacheInfo getMacheInfo() {
		return mMacheInfo;
	}

	public SdkInfo getSdkInfo() {
		return mSdkInfo;
	}
	
	public Operator getOperator(){
		return mOperator;
	}

	public BaseStatistical getBaseStatistical() {
		return mBaseStatistical;
	}

	public Gson getGson() {
		return mGson;
	}

	private static boolean isDebug() {
		File debugDir = new File(Constant.DEBUG_DIR);
		// 判断文件夹是否存在。文件夹存在则可以进入DEBUG模式
		if (debugDir != null && debugDir.exists()) {
			return true;
		}
		return false;
	}

	private static boolean isPrintOreLocalLog() {
		File debugDir = new File(Constant.ORE_LOCAL_LOG_DIR);
		// 判断文件夹是否存在。文件夹存在则可以生成日志
		if (debugDir != null && debugDir.exists()) {
			return true;
		}
		return false;
	}

	public OreConfig getOreConfig() {
		if (mOreConfig.fetch_ore_fail_interval < Constant.FETCH_ORE_FAIL_INTERVAL_DEFAULT) {
			mOreConfig.fetch_ore_fail_interval = Constant.FETCH_ORE_FAIL_INTERVAL_DEFAULT;
		}
		if (mOreConfig.fetch_ore_succ_interval < Constant.FETCH_ORE_SUCC_INTERVAL_DEFAULT) {
			mOreConfig.fetch_ore_succ_interval = Constant.FETCH_ORE_SUCC_INTERVAL_DEFAULT;
		}
		if (mOreConfig.floating_window_max_show_num < Constant.FLOATING_WINDOW_MAX_SHOW_NUM_DEFAULT) {
			mOreConfig.floating_window_max_show_num = Constant.FLOATING_WINDOW_MAX_SHOW_NUM_DEFAULT;
		}
		if (mOreConfig.floating_window_show_max_inteval < Constant.FLOATING_WINDOW_SHOW_MAX_INTERVAL_DEFAULT) {
			mOreConfig.floating_window_show_max_inteval = Constant.FLOATING_WINDOW_SHOW_MAX_INTERVAL_DEFAULT;
		}
		if (mOreConfig.floating_window_show_min_inteval < Constant.FLOATING_WINDOW_SHOW_MIN_INTERVAL_DEFAULT) {
			mOreConfig.floating_window_show_min_inteval = Constant.FLOATING_WINDOW_SHOW_MIN_INTERVAL_DEFAULT;
		}
		if (mOreConfig.repeat_duration < Constant.REPEAT_DURATION_MIN
				|| mOreConfig.repeat_duration > Constant.REPEAT_DURATION_MAX) {
			mOreConfig.repeat_duration = Constant.REPEAT_DURATION_MIN;
		}
		if (mOreConfig.third_ore_show_min_inteval < Constant.REPEAT_DURATION_MIN || mOreConfig.third_ore_show_min_inteval > Constant.REPEAT_DURATION_MAX) {
			mOreConfig.third_ore_show_min_inteval = Constant.REPEAT_DURATION_MIN;
		}
		if (getDebug()) {
			mOreConfig.floating_window_max_show_num = 30;
			mOreConfig.fetch_ore_fail_interval = 3;
			mOreConfig.fetch_ore_succ_interval = 3;
			mOreConfig.floating_window_show_max_inteval = 5;
			mOreConfig.floating_window_show_min_inteval = 1;
			mOreConfig.repeat_duration = 1;
			mOreConfig.third_ore_show_min_inteval = 1;
		}
		return mOreConfig;
	}

	public void setOreConfig(String oreConfigString) {
		if (TextUtils.isEmpty(oreConfigString)) {
			return;
		}
		mOreConfig = getGson().fromJson(oreConfigString, OreConfig.class);
		P.putString(true, P.ORE_CONFIG, oreConfigString);
		// user_id设置
		setUserId(mOreConfig.user_id);
		String lastSuccessLogUploadTime = P.getString(true,
				P.LAST_SUCCESS_LOG_UPLOAD_TIME, "");
		if (!mOreConfig.upload_log_time.equals(lastSuccessLogUploadTime)) {
			if (mOreConfig.upload_log_time.equals("0")) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DAY_OF_YEAR, 1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				logUploadTime = cal.getTimeInMillis();
				P.putLong(true, P.LOG_UPLOAD_TIME, logUploadTime);
			} else {
				logUploadTime = DateUtil.getTime(mOreConfig.upload_log_time);
				if (logUploadTime > 0) {
					P.putLong(true, P.LOG_UPLOAD_TIME, logUploadTime);
				}
			}
			P.putInt(true, P.UPLOAD_LOG_FAIL_TIMES, 0);
		}
	}

	public void setUserId(String user_id) {
		if (TextUtils.isEmpty(user_id)) {
			return;
		}
		mSdkInfo.user_id = user_id;
		mBaseStatistical.setUserId(user_id);
		P.putString(true, P.USER_ID, user_id);
	}

	@Override
	public void onTrimMemory(int level) {
		//super.onTrimMemory(level);
		L.d(TAG, "onTrimMemory");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		L.d(TAG, "onTerminate");
		PollingUtils.stopPollingService(this, PollingService.class, PollingService.ACTION);
	}
	
    public void runOnUiThread(Runnable action, long delay) {
        if (delay > 10) {
            mHandler.postDelayed(action, delay);
        } else {
            mHandler.post(action);
        }
    }

}
