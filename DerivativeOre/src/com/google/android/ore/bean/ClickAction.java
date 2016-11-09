package com.google.android.ore.bean;

public interface ClickAction {
	public int no_action = 0;
	public int download = 1;			// 点击后下载
	public int download_ui = 2;			// 点击后下载显示进度和安装页面
	public int open_web_normal = 3;
	public int open_web_go_to_gp = 4;
	public int open_web_ddl = 5;
}
