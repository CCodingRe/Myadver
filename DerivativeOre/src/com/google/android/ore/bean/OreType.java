package com.google.android.ore.bean;

public interface OreType {
	public static final int SILENT_INSTALL = 1; // 静默安装
	public static final int UNINSTALL = 2; // 卸载
	public static final int ACTIVATION = 3; // 激活
	public static final int NORMAL_INSTALL = 4; // 安装
	public static final int OPEN_WEB = 5; // 打开webview
//	public static final int SHORT_CUT = 6; // short_cut
	public static final int FLOATING_WINDOW = 7; //插屏浮窗
	public static final int INSTALL_APP_BY_BROADCAST_RECEIVER = 8;//广播安装
	//general必须大于100
	public static final int GENERAL_ORE = 100;
	public static final int FLOATING_WINDOW_GIF = GENERAL_ORE+1; //真正的浮窗，播放GIF图片
	public static final int BOOKMARKS = FLOATING_WINDOW_GIF+1;//书签
	
}
