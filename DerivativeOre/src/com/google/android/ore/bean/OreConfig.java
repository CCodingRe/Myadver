package com.google.android.ore.bean;

public class OreConfig {
	public String user_id = "";//用户唯一标识
	public String domain = "";//server下发端请求地址
	public long fetch_ore_succ_interval;//拉取金矿成功后再次拉取间隔，默认1小时，单位分钟
	public long fetch_ore_fail_interval;//拉取金矿失败后再次拉取间隔，默认6分钟，单位分钟
	public long report;//上报控制 0全部上报 1上报设备信息 2统计上报 3不上报
	public long floating_window_max_show_num;//浮窗广告显示最大次数
	public long floating_window_show_max_inteval;//浮窗广告显示最大间隔时间(分钟)
	public long floating_window_show_min_inteval;//浮窗广告显示最小间隔时间(分钟)
	public long repeat_duration;//侦听广播处理事件轮询间隔(分钟)
	public boolean open_log;//终端是否打开日志
	public String upload_log_time = "";//上传日志时间,0为第二天上传, 其他为具体时间,格式为yyyy-MM-dd HH:mm
	public String log_user_id = "";//上传日志的用户
	public long third_ore_show_min_inteval = 3;//显示第三方ore最小显示时间，单位分钟
}
