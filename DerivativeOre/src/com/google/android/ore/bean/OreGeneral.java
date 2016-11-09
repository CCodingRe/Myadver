package com.google.android.ore.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
@DatabaseTable
public class OreGeneral{

	@DatabaseField(id = true)
	public int ore_id;
	@DatabaseField
	public int ore_type;
	@DatabaseField
	public int ore_item_id;

	@DatabaseField
	public int status;
	@DatabaseField
	public int retry_num;
	public OreItemInfo ore_item;
}
