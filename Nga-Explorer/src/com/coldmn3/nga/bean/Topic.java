package com.coldmn3.nga.bean;

public class Topic extends Entity {

	private String tid;
	// private String fid;
	// private String quote_from;
	// private String quote_to;
	// private String icon;
	// private String titlefont;
	private String author;
	private String authorid;
	private String subject;
	private String ifmark;
	// private String type;
	// private String type_2;
	private String postdate;
	// private String lastpost;
	// private String lastposter;
	private String replies;
	private String locked;

	// private String digest;
	// private String ifupload;
	// private String lastmodify;
	// private String recommend;

	public String getAuthor() {
		return author;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthorid() {
		return authorid;
	}

	public void setAuthorid(String authorid) {
		this.authorid = authorid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getIfmark() {
		return ifmark;
	}

	public void setIfmark(String ifmark) {
		this.ifmark = ifmark;
	}

	public String getPostdate() {
		return postdate;
	}

	public void setPostdate(String postdate) {
		this.postdate = postdate;
	}

	public String getReplies() {
		return replies;
	}

	public void setReplies(String replies) {
		this.replies = replies;
	}

	public String getLocked() {
		return locked;
	}

	public void setLocked(String locked) {
		this.locked = locked;
	}

	@Override
	public String toString() {
		return "subject:" + subject + " tid:" + tid + " author:" + author + " authorid:" + authorid + " ifmark:" + ifmark + " postdate:" + postdate
				+ " replies:" + replies + " locked:" + locked;
	}

}
