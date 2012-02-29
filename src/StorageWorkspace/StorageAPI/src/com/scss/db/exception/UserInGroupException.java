package com.scss.db.exception;

public class UserInGroupException extends Exception {

	private String group;
	private String user;

	public UserInGroupException(String user, String group) {
		this.user = user;
		this.group = group;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
