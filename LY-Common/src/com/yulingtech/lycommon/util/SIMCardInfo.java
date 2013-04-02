package com.yulingtech.lycommon.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 读取SIM卡信息 需要权限 <uses-permission android:name="android.permission.READ_PHONE_STATE" />
 * 
 * @author session
 * @date 2013-3-18
 * @version v1.0
 */
public class SIMCardInfo {
	/**
	 * TelephonyManager提供设备上获取通讯服务信息的入口。 应用程序可以使用这个类方法确定的电信服务商和国家 以及某些类型的用户访问信息。
	 * 应用程序也可以注册一个监听器到电话收状态的变化。不需要直接实例化这个类
	 * 使用Context.getSystemService(Context.TELEPHONY_SERVICE)来获取这个类的实例。
	 */
	private TelephonyManager telephonyManager;
	/**
	 * 国际移动用户识别码
	 */
	private String IMSI;

	public SIMCardInfo(Context context) {
		telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	}

	/**
	 * 获取当前设置的电话号码
	 * 
	 * @return
	 */
	public String getNativePhoneNumber() {
		String NativePhoneNumber = null;
		NativePhoneNumber = telephonyManager.getLine1Number();
		return NativePhoneNumber;
	}

	/**
	 * Telecom service providers获取手机服务商信息 需要加入权限<uses-permission
	 * android:name="android.permission.READ_PHONE_STATE"/>
	 * 
	 * @return
	 */
	public String getProvidersName() {
		String ProvidersName = null;
		// 返回唯一的用户ID;就是这张卡的编号神马的
		IMSI = telephonyManager.getSubscriberId();
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}

	/**
	 * 获取IMSI
	 * 
	 * @return
	 */
	public String getIMSI() {
		return telephonyManager.getSubscriberId();
	}
}
