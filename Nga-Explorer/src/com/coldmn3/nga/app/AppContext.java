package com.coldmn3.nga.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.coldmn3.nga.api.NgaApi;
import com.coldmn3.nga.bean.TopicFloorList;
import com.coldmn3.nga.bean.TopicList;
import com.coldmn3.nga.bean.User;
import com.yulingtech.lycommon.util.CyptoUtils;
import com.yulingtech.lycommon.util.StringUtils;
import com.yulingtech.lycommon.util.ULog;

public class AppContext extends Application {

	private static final String KEY = "daxuanwo";
	private static final String LOG_TAG = "AppContext";

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	private String currentUserName;

	private Map<String, User> userList = new HashMap<String, User>();

	private boolean isLogin = false;

	private boolean isShowAvatar = false;

	private int launchCounts;

	private int floorPictureOption = 0; // 正文图片加载测量

	private int floorFontOption = 1; // 正文字号大小

	@Override
	public void onCreate() {
		super.onCreate();
		initLogin();
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	public void initSettings() {
		launchCounts = getIntProperty("launchCounts");
		if (0 == launchCounts) {
			setIntProperty("floorPictureOption", 0);
			setIntProperty("floorFontOption", 1);
			setBooleanProperty("isShowAvatar", false);
		} else {
			isShowAvatar = getBooleanProperty("isShowAvatar");
			floorPictureOption = getIntProperty("floorPictureOption");
			floorFontOption = getIntProperty("floorFontOption");
		}
		launchCounts++;
		setIntProperty("launchCounts", launchCounts);
	}

	public void initLogin() {
		String u = getUserProperty("users");
		if (!StringUtils.isEmpty(u)) {
			String[] users = u.split(";");

			for (int i = 0; i < users.length; i++) {
				String userName = users[i];
				User user = new User();
				user.setAccount(userName);
				user.setCid(getUserProperty(userName + ".cid"));
				user.setCookie(getUserProperty(userName + ".cookie"));
				user.setPassword(CyptoUtils.decode(KEY, getUserProperty(userName + ".password")));
				user.setUid(getUserProperty(userName + ".uid"));
				userList.put(userName, user);

				ULog.i(LOG_TAG, "initLogin success: " + user.toString());
			}

			currentUserName = getUserProperty("currentUser");
			ULog.d(LOG_TAG + " initLogin:", " currentUserName :" + currentUserName);
			if (userList.size() != 0) {
				isLogin = true;
			}
		}
	}

	/**
	 * 登陆验证
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 * @throws AppException
	 */
	public User LoginValidation(final String account, final String pwd) throws AppException {
		ULog.d(LOG_TAG, "start LoginValidation, account:" + account + " pwd:" + pwd);
		User user = NgaApi.login(this, account, pwd);
		saveLoginInfo(user);
		return user;
	}

	private void saveLoginInfo(User user) {
		if (user != null) {
			// 更新userList, sharedprefrence
			Set<String> accountSet = userList.keySet();
			String key = user.getAccount();
			if (accountSet.contains(key)) {
				userList.remove(key);
			}
			userList.put(key, user);
			currentUserName = key;
			AppConfig.getAppConfig(this).removeAll();
			String users = "";
			for (String key_account : accountSet) {
				users += key_account + ";";
				setUserProperties(userList.get(key_account));
			}
			setUserProperty("users", users);
			setUserProperty("currentUser", currentUserName);
		}
	}

	/**
	 * 清除登录信息
	 */
	public void cleanLoginInfo() {
		this.isLogin = false;
		AppConfig.getAppConfig(this).removeAll();
	}

	public void cleanUserInfo(String user) {
		userList.remove(user);
		removeUserProperties(user);
		if (userList.size() != 0) {
			Set<String> accountSet = userList.keySet();
			String users = "";
			for (String key_account : accountSet) {
				users += key_account + ";";
				setUserProperties(userList.get(key_account));
			}
			setUserProperty("users", users);
			currentUserName = new ArrayList<String>(accountSet).get(0);
			setUserProperty("currentUser", currentUserName);
		} else if (userList.size() == 0) {
			cleanLoginInfo();
			currentUserName = "";
			isLogin = false;
		}

	}

	public TopicList getDefaultTopicList(final String page, boolean isRefresh) throws AppException {
		TopicList list = null;
		String key = "topic_list_" + "-7_" + page;
		if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {

			try {
				list = NgaApi.getDefaultTopicList(this, page);
				if (list != null && page.equals("1")) {
					list.setCacheKey(key);
					saveObject(list, key);
				}
			} catch (AppException e) {
				e.printStackTrace();
				throw e;
			}

		} else {
			// 读取序列化
			list = (TopicList) readObject(key);

			if (list == null) {
				list = new TopicList();
			}

		}
		return list;
	}

	public TopicList getTopicList(final String page, final String fid, final String searchpost, final String favor, final String authorid, final String key,
			boolean isRefresh) throws AppException {
		TopicList list = null;
		String key_ = "topic_list_" + "fid" + fid + page;
		if (isNetworkConnected() && (!isReadDataCache(key_) || isRefresh)) {

			try {
				list = NgaApi.getTopicList(this, page, fid, searchpost, favor, authorid, key);
				if (list != null && page.equals("1")) {
					list.setCacheKey(key_);
					saveObject(list, key_);
				}
			} catch (AppException e) {
				e.printStackTrace();
				throw e;
			}

		} else {
			// 读取序列化
			list = (TopicList) readObject(key_);

			if (list == null) {
				list = new TopicList();
			}

		}
		return list;
	}

	public TopicFloorList getTopicDetailList(String page, String tid) throws AppException {
		TopicFloorList topicDetailList = null;
		String key = "topic_floor_list_" + tid + "_" + page;
		if (isNetworkConnected()) {
			try {
				topicDetailList = NgaApi.getTopicDetailList(this, page, tid);
			} catch (AppException e) {
				e.printStackTrace();
				throw e;
			}

		} else {
			// 读取序列化
		}
		return topicDetailList;
	}

	/**
	 * 收藏主题
	 * 
	 * @param tid
	 */
	public String addTopicToFav(String tid) throws AppException {
		return NgaApi.addTopicToFav(this, tid);
	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取App安装包信息
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key, "setting");
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value, "setting");
	}

	public boolean getBooleanProperty(String key) {
		return AppConfig.getAppConfig(this).getBoolean(key, "setting");
	}

	public void setBooleanProperty(String key, boolean value) {
		AppConfig.getAppConfig(this).setBoolean(key, value, "setting");
	}

	public int getIntProperty(String key) {
		return AppConfig.getAppConfig(this).getInt(key, "setting");
	}

	public void setIntProperty(String key, int value) {
		AppConfig.getAppConfig(this).setInt(key, value, "setting");
	}

	public String getUserProperty(String key) {
		return AppConfig.getAppConfig(this).get(key, "users");
	}

	public void setUserProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value, "users");
	}

	public void setUserProperties(User user) {
		if (user != null) {
			String account = user.getAccount();
			setUserProperty(account + ".cid", user.getCid());
			setUserProperty(account + ".cookie", user.getCookie());
			setUserProperty(account + ".password", CyptoUtils.encode(KEY, user.getPassword()));
			setUserProperty(account + ".uid", user.getUid());
		}
	}

	public void removeUserProperties(String name) {
		if (!StringUtils.isEmpty(name)) {
			removeUserProperty(name + ".cid");
			removeUserProperty(name + ".cookie");
			removeUserProperty(name + ".password");
			removeUserProperty(name + ".uid");
		}
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	public void removeUserProperty(String key) {
		AppConfig.getAppConfig(this).remove(key, "users");
	}

	// get set
	// //////////////////////////////////////////////////

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}

	public Map<String, User> getUserList() {
		return userList;
	}

	public void setUserList(Map<String, User> userList) {
		this.userList = userList;
	}

	public boolean isShowAvatar() {
		return isShowAvatar;
	}

	public void setShowAvatar(boolean isShowAvatar) {
		this.isShowAvatar = isShowAvatar;
		setBooleanProperty("isShowAvatar", isShowAvatar);
	}

	public int getFloorPictureOption() {
		return floorPictureOption;
	}

	public void setFloorPictureOption(int floorPictureOption) {
		this.floorPictureOption = floorPictureOption;
		setIntProperty("floorPictureOption", floorPictureOption);
	}

	public int getFloorFontOption() {
		return floorFontOption;
	}

	public void setFloorFontOption(int floorFontOption) {
		this.floorFontOption = floorFontOption;
		setIntProperty("floorFontOption", floorFontOption);
	}

}
