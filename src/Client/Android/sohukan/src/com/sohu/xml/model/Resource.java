package com.sohu.xml.model;

import java.io.Serializable;

public class Resource implements Serializable {
	
	private String bookmarkid;
	private String type;
	private String key;
	private int is_download;
	private String userid;
	
	public String getBookmarkId() {
		return bookmarkid;
	}
	public void setBookmarkId(String bookmarkid) {
		this.bookmarkid = bookmarkid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getIsDownload() {
		return is_download;
	}
	public void setIsDownload(int is_download) {
		this.is_download = is_download;
	}
	public String getUserId() {
		return userid;
	}
	public void setUserId(String userid) {
		this.userid = userid;
	}
	
}
