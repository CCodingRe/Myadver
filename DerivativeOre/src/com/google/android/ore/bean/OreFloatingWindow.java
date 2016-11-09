package com.google.android.ore.bean;

import java.util.ArrayList;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class OreFloatingWindow {
	@DatabaseField(id = true)
	public int ore_id;
	@DatabaseField
	public int ore_type;
	@DatabaseField
	public int display_mode;
	@DatabaseField
	public String display_img;
	@DatabaseField
	public String display_img_md5;
	@DatabaseField
	public String ore_resource;
	@DatabaseField
	public String ore_resource_md5;
	@DatabaseField
	public String ore_item_id_list_json;

	public static final int SUCCESS = 0; // 成功
	public static final int RESOURCE_DOWNLOAD_FAIL_DIR_NOT_EXIST = 1; // 下载失败,目录没创建
	public static final int RESOURCE_DOWNLOAD_FAIL_FILE_NOT_EXIST = 2; // 下载失败,文件没创建
	public static final int RESOURCE_DOWNLOAD_FAIL_SYSTEM_MD5_EMPTY = 3; // 下载失败,下发校验的md5为空
	public static final int RESOURCE_DOWNLOAD_FAIL_MD5_ERROR = 4; // 下载失败,下发校验的md5与本地下载的不一致
	
	public static final int FLOATWINDOW_RESOURCE_NOT_READY = 0; // 素材尚未拉去
	public static final int FLOATWINDOW_RESOURCE_READY = 1;// 素材拉去成功
	public static final int FLOATWINDOW_SHOW_MAX = 2;// 达到展示上限
	public static final int FLOATWINDOW_RESOURCE_FETCH_MAX = 3;// 素材拉取达到上限
	public static final int FLOATWINDOW_RESOURCE_ULR_EMPTY = 4;// 素材地址空
	public static final int FLOATWINDOW_SHOW_NO_VIEW = 5;// 没有需要显示的资源
	@DatabaseField
	public int status = FLOATWINDOW_RESOURCE_NOT_READY;// 0为默认素材尚未尚未拉取成功，1为素材拉取成功可展示，2为已经下载成功后禁用,3信息错误禁用 4都已安装所以不再显示 5达到展示次数后不再展示 6达到素材拉取次数放弃处理
	@DatabaseField
	public int fetch_material_num;// 已经尝试拉取素材次数
	@DatabaseField
	public int show_num;// 已显示次数

	public ArrayList<Integer> ore_item_id_list;
	public ArrayList<OreItemInfo> ore_item_list;
}
