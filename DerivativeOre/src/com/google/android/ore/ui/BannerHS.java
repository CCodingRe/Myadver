package com.google.android.ore.ui;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.view.ViewPager.PageTransformer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.ore.Constant;
import com.google.android.ore.bean.OreFloatingWindow;
import com.google.android.ore.bean.OreItemInfo;
import com.google.android.ore.report.OreReport;
import com.google.android.ore.report.bean.ReportKey;
import com.google.android.ore.report.bean.Statistical;
import com.google.android.ore.thinkandroid.L;
import com.google.android.ore.util.ResUtil;
import com.google.android.ore.util.Utils;

/**
 * 半屏广告，HalfScreen，可以轮播，UI样式为右上角有个叉叉。
 * 
 * @author zanetzli
 * 
 */
public class BannerHS extends RelativeLayout implements OnPageChangeListener {
	private static final String TAG = BannerHS.class.getSimpleName();
	public static int LEFT_RIGHTM_MARGIN = 20;
	public static int TOP_BOTTOM_MARGIN = 3;
	private ViewPager mViewPager;
	private ArrayList<ImageView> viewlist = new ArrayList<ImageView>();
	private ImageAdapter mAdapter;
	private Handler mMainHandler = new Handler(Looper.getMainLooper());
	private long DELAYED_S = 4 * 1000;
	private int PLAY_NUM = 0;
	private int PLAY_NUM_MAX = 3;

	public BannerHS(Context context) {
		super(context);
		initView();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		setBackgroundColor(Color.TRANSPARENT);
		mViewPager = new ViewPager(getContext());
		LayoutParams lp = new LayoutParams(getW(), getH());
		lp.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		addView(mViewPager, lp);

		ImageView closeImg = new ImageView(getContext());
		closeImg.setImageBitmap(ResUtil.getBitmap("oneight_close.png"));
		LayoutParams lpClose = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lpClose.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		lpClose.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		lpClose.rightMargin = Utils.dp2px(LEFT_RIGHTM_MARGIN+TOP_BOTTOM_MARGIN);
		lpClose.topMargin = Utils.dp2px(TOP_BOTTOM_MARGIN);
		closeImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HSHelper.dismiss();
				Statistical statistical = new Statistical();
				statistical.ore_id = mOreId;
				statistical.report_key = ReportKey.ore_close_click;
				OreReport.statisticalReport(statistical);
			}
		});
		addView(closeImg, lpClose);

		mAdapter = new ImageAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setPageTransformer(false, new ZoomOutPageTransformer());
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		mMainHandler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	private Runnable mAutoPlayRunnable = new Runnable() {
		@Override
		public void run() {
			if (mViewPager.getCurrentItem() == mViewPager.getChildCount()) {
				mViewPager.setCurrentItem(0, true);
			} else {
				mViewPager
						.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
			}

			if (PLAY_NUM < PLAY_NUM_MAX * viewlist.size()) {
				PLAY_NUM = PLAY_NUM + 1;
				L.d(TAG, "AutoPlayRunnable PLAY_NUM : " + PLAY_NUM);
				mMainHandler.postDelayed(mAutoPlayRunnable, DELAYED_S);
			} else {
				HSHelper.dismiss();
			}
		}
	};
	private int mOreId;

	public boolean initDta(OreFloatingWindow oreFloatingWindow,
			ArrayList<OreItemInfo> list) {
		if (null == oreFloatingWindow || Utils.isEmpty(list)) {
			return false;
		}
		PLAY_NUM = 0;
		mOreId = oreFloatingWindow.ore_id;
		mViewPager.removeAllViews();
		mMainHandler.removeCallbacks(mAutoPlayRunnable);
		viewlist.clear();

		for (final OreItemInfo info : list) {
			if (info.status < 4) {
				generateImgItem(viewlist, info);
			}
		}
		if (Utils.isEmpty(viewlist)) {
			return false;
		}
		// 再添加一遍，支持循环滑动
		if (list.size() > 1) {
			for (final OreItemInfo info : list) {
				if (info.status < 4) {
					generateImgItem(viewlist, info);
				}
			}
		}
		mAdapter.notifyDataSetChanged();
		if (viewlist.size() > 1) {
			int off = (Integer.MAX_VALUE / 2) % viewlist.size();
			mViewPager.setCurrentItem(Integer.MAX_VALUE / 2 - off);
			mMainHandler.postDelayed(mAutoPlayRunnable, DELAYED_S);
		} else {
			mViewPager.setCurrentItem(0);
		}
		L.d(TAG, "initDta viewlist.size() : " + viewlist.size());
		return true;
	}

	private void generateImgItem(ArrayList<ImageView> viewlist,
			final OreItemInfo info) {
		ImageView im = new ImageView(getContext());
		im.setBackgroundColor(Color.TRANSPARENT);
		File resDir = Constant.getDir(Constant.RES_DIR);
		File imgFile = new File(resDir.getAbsolutePath() + "/"
				+ info.ore_item_img_md5);
		if (imgFile != null && imgFile.exists()) {
			Bitmap img = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			im.setImageBitmap(img);
			im.setScaleType(ImageView.ScaleType.CENTER_CROP);
			im.setTag(info);
			im.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					HSHelper.dismiss();
					Advert.onClick(v.getContext(),mOreId,info);
				}
			});
			viewlist.add(im);
		}
	}



	private class ImageAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// 设置成最大，使用户看不到边界
			if (viewlist.size() == 1) {
				return 1;
			}
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// 对ViewPager页号求模取出View列表中要显示的项
			if (position < 0) {
				position = -position;
			}
			if (position >= viewlist.size()) {
				position = position % viewlist.size();
			}
			View view = viewlist.get(position);
			try {
				((ViewGroup) container).removeView(view);
			} catch (Throwable e) {

			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// 对ViewPager页号求模取出View列表中要显示的项
			if (position < 0) {
				position = -position;
			}
			if (position >= viewlist.size()) {
				position = position % viewlist.size();
			}
			View addView = viewlist.get(position);
			try {
				LayoutParams lp = new LayoutParams(getW(), getH());
				((ViewPager) container).addView(addView, 0, lp);
			} catch (Exception e) {
				//
			}
			return addView;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		if (mViewPager.getChildCount() > 1) {

		}
	}

	public static int getW() {
		return (int) ((Utils.getWidth() - Utils.dp2px(LEFT_RIGHTM_MARGIN) * 2));
	}

	public static int getH() {
		return (int) (getW() / 1);
	}

	public class ZoomOutPageTransformer implements PageTransformer {
		private static final float MIN_SCALE = 0.85f;

		private static final float MIN_ALPHA = 0.5f;

		@Override
		public void transformPage(View view, float position) {
			// int pageWidth = view.getWidth();
			// int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
									// This page is way off-screen to the left.
				view.setAlpha(0);
			} else if (position <= 1) { // [-1,1]
										// Modify the default slide transition
										// to
										// shrink the page as well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				// float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				// float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				// if (position < 0) {
				// view.setTranslationX(horzMargin - vertMargin / 2);
				// } else {
				// view.setTranslationX(-horzMargin + vertMargin / 2);
				// }
				// Scale the page down (between MIN_SCALE and 1)
				// view.setScaleX(scaleFactor);
				// view.setScaleY(scaleFactor);
				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));
			} else { // (1,+Infinity]
						// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}
}
