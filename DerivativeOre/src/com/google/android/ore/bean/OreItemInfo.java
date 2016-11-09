package com.google.android.ore.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable
public class OreItemInfo{

	@DatabaseField(id = true)
	public int ore_item_id;			//唯一标识
	@DatabaseField
	public int ore_item_type;		//金矿类型
	@DatabaseField
	public String ore_item_desc;		//金矿文字信息描述
	@DatabaseField
	public String ore_item_img_url;	//金矿图片信息链接
	@DatabaseField
	public String ore_item_img_md5;
	@DatabaseField
	public String click_url;	//点击行为需要链接
	@DatabaseField
	public int click_action;	//点击行为见ClickAction类

	// app info
	@DatabaseField
	public int app_id;
	@DatabaseField
	public String app_name;
	@DatabaseField
	public String app_icon_url;
	@DatabaseField
	public String app_icon_md5;
	@DatabaseField
	public String app_package_name;
	@DatabaseField
	public String app_main_activity_name;//主activity入口点
	@DatabaseField
	public String app_launcher_action;//隐式启动的action
	@DatabaseField
	public int app_size;
	@DatabaseField
	public String d1;//冗余字段1   用于书签
	@DatabaseField
	public String d2;//冗余字段2

	//客户端使用
	@DatabaseField
	public int status;//0正常，1下载成功，2下载失败，3安装失败 4 安装成功 5已经安装 6已经上报
	@DatabaseField
	public int retry_num;
	@DatabaseField
	public String response_string;
	@DatabaseField
	public String extra_str;

	@Override
	public String toString() {
		return super.toString();
	}
}