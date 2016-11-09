package com.google.android.ore.bean;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.android.ore.OreApp;

public class Operator {
	public String networkOperatorName;
	public String networkOperator;
	public String networkCountryIso;
	public String simCountryIso;
	public String simOperator;
	public String simOperatorName;
	public String simSerialNumber;

	public  Operator() {
	TelephonyManager tm = (TelephonyManager) OreApp.get()
				.getSystemService(Context.TELEPHONY_SERVICE);
	//运营商信息
	//返回注册的网络运营商的名字
	networkOperatorName = tm.getNetworkOperatorName();
	//返回的MCC +跨国公司的注册网络运营商
	networkOperator = tm.getNetworkOperator();
	//返回注册的网络运营商的国家代码
	networkCountryIso = tm.getNetworkCountryIso();
	//返回SIM卡运营商的国家代码 READ_PHONE_STATE
	simCountryIso = tm.getSimCountryIso();
	//返回SIM卡运营商的单个核细胞数+冶 READ_PHONE_STATE
	simOperator = tm.getSimOperator();
	//返回SIM卡运营商的名字 READ_PHONE_STATE
	simOperatorName = tm.getSimOperatorName();
	//返回SIM卡的序列号 READ_PHONE_STATE
	simSerialNumber = tm.getSimSerialNumber();
	}
}
