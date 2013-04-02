package com.coldmn3.nga.bean;

/**
 * 登陆用户信息
 * @author session
 * @date 2013-3-20
 * @version v1.0
 */
public class User {

	private String uid;
	private String cid;
	private String account;
	private String password;
	private String cookie;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	@Override
	public String toString() {
		return "uid:" + uid + " cid:" + cid + " account:" + account + " password:" + password + " cookie:" + cookie;
	}

}
