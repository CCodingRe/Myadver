package com.google.android.ore.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.ore.bean.ClickAction;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.db.dao.OreItemInfoDao;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.ui.Advert;

public class OreWebActivity extends Activity {
	private String TAG = OreWebActivity.class.getSimpleName();
	private WebView mWebView;
	public static String FROM = "FROM";
	public static String FROM_SHORT = "FROM_SHORT";
	public static String FROM_WEB = "FROM_WEB";
	public static String WEB_URL = "WEB_URL";
	public static String ORE_ID = "ORE_ID";
	public static String ORE_ITEM_ID = "ORE_ITEM_ID";
	
	int ore_id = -1;
	int ore_item_id = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		L.d(TAG, "onCreate");
		Intent intent = getIntent();
		doSomeThing(intent);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		L.d(TAG, "onNewIntent");
		doSomeThing(intent);
	}

	private void doSomeThing(Intent intent) {
		if (intent != null) {
			String from = intent.getStringExtra(FROM);
			ore_id = intent.getIntExtra(ORE_ID, -1);
			ore_item_id = intent.getIntExtra(ORE_ITEM_ID, -1);
			if (FROM_SHORT.equalsIgnoreCase(from)) {
				if (ore_id > 0 && ore_item_id > 0) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							final OreItemInfo oreItemInfo = OreItemInfoDao
									.get().get(ore_item_id);
							runOnUiThread(new Runnable() {
								public void run() {
									if (oreItemInfo.click_action == ClickAction.open_web_ddl
											|| oreItemInfo.click_action == ClickAction.open_web_normal) {
										openWebView(oreItemInfo.click_url);
									} else {
										L.d(TAG, "Advert.onClick oreItemInfo.ore_item_type:"+oreItemInfo.ore_item_type);
										Advert.onClick(OreWebActivity.this,
												ore_id, oreItemInfo);
										OreWebActivity.this.finish();
									}
								}
							});
						}
					}).start();
				}
			} else {
				String url = intent.getStringExtra(WEB_URL);
				openWebView(url);
			}
		} else {
			finish();
		}
	}

	private void openWebView(String url) {
		if (TextUtils.isEmpty(url)) {
			url = "http://codeforbaby.com/";
		}
		L.d(TAG, "openWebView:"+url);
		mWebView = new WebView(this);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
		mWebView.getSettings().setSupportZoom(false);
		mWebView.getSettings().setBuiltInZoomControls(false);
		mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebView.getSettings().setDomStorageEnabled(true);
		mWebView.getSettings().setDatabaseEnabled(true);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				OreReport.statisticalReport(ore_id, ore_item_id,
						ReportKey.ore_open_web_page_finished);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.loadUrl(url);
		LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mWebView.setLayoutParams(lp);
		setContentView(mWebView);
	}
}
