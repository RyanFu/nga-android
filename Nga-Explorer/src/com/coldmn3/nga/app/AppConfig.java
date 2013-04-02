package com.coldmn3.nga.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 应用程序配置类，用于保存用户配置
 * 
 * @author session
 * @date 2013-3-1
 * @version v1.0
 */
public class AppConfig {
	public final static String CONF_LOAD_IMAGE = "perf_loadimage";
	public final static String CONF_APP_UNIQUEID = "APP_UNIQUEID";

	private static AppConfig appConfig;
	private Context mContext;

	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}

	public String get(String key, String pref_name) {
		SharedPreferences settings = mContext.getSharedPreferences(pref_name, 0);
		return settings.getString(key, "");
	}

	public void set(String key, String value, String pref_name) {
		SharedPreferences sp = mContext.getSharedPreferences(pref_name, 0);
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public boolean getBoolean(String key, String pref_name) {
		SharedPreferences settings = mContext.getSharedPreferences(pref_name, 0);
		return settings.getBoolean(key, false);
	}

	public void setBoolean(String key, boolean value, String pref_name) {
		SharedPreferences sp = mContext.getSharedPreferences(pref_name, 0);
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public int getInt(String key, String pref_name) {
		SharedPreferences settings = mContext.getSharedPreferences(pref_name, 0);
		return settings.getInt(key, 0);
	}

	public void setInt(String key, int value, String pref_name) {
		SharedPreferences sp = mContext.getSharedPreferences(pref_name, 0);
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void removeAll() {
		SharedPreferences sp = mContext.getSharedPreferences("users", 0);
		Editor editor = sp.edit();
		editor.clear();
		editor.commit();
	}

	public void remove(String key, String pref_name) {
		SharedPreferences sp = mContext.getSharedPreferences(pref_name, 0);
		Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

	public void remove(String... key) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
		Editor editor = sp.edit();
		for (String k : key)
			editor.remove(k);
		editor.commit();
	}

}
