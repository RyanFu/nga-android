package com.coldmn3.nga.bean;

import java.util.List;

public class TopicFloor extends Entity {
	String content;
	String alterinfo;
	// String type;
	String authorid;
	String postdate;
	String subject;
	String pid;
	String tid;
	String fid;
	String lou;
	// String postdatetimestamp;
	// String content_length;
	String author;
	// String credit;
	// String medal;
	// String groupid;
	// String gp_lesser;
	// String level;
	String yz; // nuke?
	// String js_escap_site;
	// String js_escap_honor;
	String js_escap_avatar;
	// String regdate;
	String mute_time;
	String postnum;
	String aurvrc;
	// String money;
	String thisvisit;
	// String signature;
	// String nickname;
	List<TopicFloor> comments;

	public String getAurvrc() {
		return aurvrc;
	}

	public void setAurvrc(String aurvrc) {
		this.aurvrc = aurvrc;
	}

	public String getPostnum() {
		return postnum;
	}

	public void setPostnum(String postnum) {
		this.postnum = postnum;
	}

	public List<TopicFloor> getComments() {
		return comments;
	}

	public void setComments(List<TopicFloor> comments) {
		this.comments = comments;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAlterinfo() {
		return alterinfo;
	}

	public void setAlterinfo(String alterinfo) {
		this.alterinfo = alterinfo;
	}

	public String getAuthorid() {
		return authorid;
	}

	public void setAuthorid(String authorid) {
		this.authorid = authorid;
	}

	public String getPostdate() {
		return postdate;
	}

	public void setPostdate(String postdate) {
		this.postdate = postdate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getLou() {
		return lou;
	}

	public void setLou(String lou) {
		this.lou = lou;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getYz() {
		return yz;
	}

	public void setYz(String yz) {
		this.yz = yz;
	}

	public String getJs_escap_avatar() {
		return js_escap_avatar;
	}

	public void setJs_escap_avatar(String js_escap_avatar) {
		this.js_escap_avatar = js_escap_avatar;
	}

	public String getMute_time() {
		return mute_time;
	}

	public void setMute_time(String mute_time) {
		this.mute_time = mute_time;
	}

	public String getThisvisit() {
		return thisvisit;
	}

	public void setThisvisit(String thisvisit) {
		this.thisvisit = thisvisit;
	}

}
