package com.google.android.ore.report.bean;

public interface ReportKey {
	public static final String ore_fetch_material_succ = "ORE_FETCH_MATERIAL_SUCC";//广告资源拉取成功 
	public static final String ore_fetch_material_retry_max = "ORE_FETCH_MATERIAL_RETRY_MAX";//广告资源拉取失败达到上限
	public static final String ore_fetch_material_url_empty = "ORE_FETCH_MATERIAL_URL_EMPTY";//广告资源拉取地址为空 
	public static final String ore_fetch_material_download_dir_not_exist = "ORE_FETCH_MATERIAL_DOWNLOAD_FAIL_DIR_NOT_EXIST";//广告资源拉取失败，目录不存在
	public static final String ore_fetch_material_download_file_not_exist = "ORE_FETCH_MATERIAL_DOWNLOAD_FAIL_FILE_NOT_EXIST";//广告资源拉取失败，文件不存在
	public static final String ore_fetch_material_not_apk_error = "ORE_FETCH_MATERIAL_NOT_APK_ERROR";//广告资源拉取失败，不是有效的安装程序
	
	public static final String ore_fetch_material_download_system_md5_empty = "ORE_FETCH_MATERIAL_DOWNLOAD_SYSTEM_MD5_EMPTY";
	public static final String ore_fetch_material_download_md5_error = "ORE_FETCH_MATERIAL_DOWNLOAD_MD5_ERROR";
	public static final String check_apk_and_install_ret_fail = "CHECK_APK_AND_INSTALL_RET_FAIL";//静默安装过程失败
	
	
	public static final String ore_show = "ORE_SHOW";//广告显示
	public static final String ore_close_click = "ORE_CLOSE_CLICK";//关闭按钮点击
	public static final String ore_click = "ORE_CLICK";//广告点击
	
	public static final String ore_open_web = "ORE_OPEN_WEB";//广告点击后，打开webview
	public static final String ore_open_gp = "ORE_OPEN_GP";//广告点击后,打开谷歌市场
	public static final String ore_open_ddl = "ORE_OPEN_DDL";//广告点击后，打开DDL
	
	public static final String ore_open_web_page_finished = "ore_open_web_page_finished";//广告点击后，打开webview,页面加载完成
	
	public static final String ore_dl_start = "ORE_DL_START";//广告点击后，开始下载
	public static final String ore_dl_succ = "ORE_DL_SUCC";//广告点击后,下载成功
	public static final String ore_app_install = "ORE_APP_INSTALL";//安装应用
	public static final String ore_app_install_succ = "ORE_APP_INSTALL_SUCC";//安装成功
	public static final String ore_app_install_fail = "ORE_APP_INSTALL_FAIL";//安装失败
	public static final String ore_app_open = "ORE_APP_OPEN";//打开应用
	
	public static final String ore_uninstall = "ORE_UNINSTALL";//卸载
}
