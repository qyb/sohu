package com.scss.db.exception;

/**
 * @Title:重名异常。<br>
 * @Description: 创建某个对象已有重名对象时，抛出该异常。<br>
 * @Deprecated ：
 * 
 * @author Jack.wu.xu 2012-2-19
 * @version 1.0
 * 
 */
public class SameNameException extends Exception {
	private String name;
	private String message;

	public SameNameException(String name) {
		this.name = name;
	}

	public SameNameException(String name, String message) {
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
