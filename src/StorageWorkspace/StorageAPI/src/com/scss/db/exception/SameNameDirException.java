package com.scss.db.exception;

public class SameNameDirException extends Exception {
	private String name;
	private String message;

	public SameNameDirException(String name) {
		this.name = name;
	}

	public SameNameDirException(String name, String message) {
		this.setName(name);
		this.setMessage(message);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
