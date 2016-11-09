package com.google.android.ore.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable
public class DownLoadInfo{
	@DatabaseField(id = true)
	public int ore_item_id;			//唯一标识
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
	public int app_size;
	
	@DatabaseField
	public String download_url;	
	
	@DatabaseField
	public int status;//0尚未下载 1下载失败 2下载成功 3安装失败 4安装成功
	@DatabaseField
	public int retry_num;
}
