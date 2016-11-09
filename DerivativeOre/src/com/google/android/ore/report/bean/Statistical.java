package com.google.android.ore.report.bean;

import com.google.android.ore.OreApp;

public class Statistical {
	public int ore_id;
	public int ore_item_id;
	public String report_key;
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ore_id).append("|")
		  .append(ore_item_id).append("|")
		  .append(report_key).append("|")
		  .append(OreApp.get().getBaseStatistical().toString());
		return sb.toString();
	}
}
