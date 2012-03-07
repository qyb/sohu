package com.scss.db.exception;

public class DBException extends Exception {
	private String name;
	private String message;

	public DBException(Throwable cause) {
		super(cause);
		this.name = cause.getClass().getName();
		this.message = cause.getMessage();
	}

	public DBException(String name) {
		this.name = name;
	}

	public DBException(String name, String message) {
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
