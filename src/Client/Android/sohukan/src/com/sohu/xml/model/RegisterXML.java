package com.sohu.xml.model;


public class RegisterXML {
	
	private String uid;
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	private int status;
	private String token;
	private String uniqname;

	public String getUniqname() {
		return uniqname;
	}

	public void setUniqname(String uniqname) {
		this.uniqname = uniqname;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
