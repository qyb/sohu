package com.scss.utility;
/**
 * 操作者类型 
 * @author Jack.wu.xu
 */
public enum AccessorType {

	/**
	 * USER.
	 */
	USER("U"),

	/**
	 * GROUP.
	 */
	GROUP("G"),

	UNKNOWN("UN");

	private String value;

	AccessorType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static AccessorType getAccessorType(String in) {
		AccessorType st = null;
		if (in == null) {
			return UNKNOWN;
		}
		if (in.equals(USER.getValue())) {
			st = USER;
		} else if (in.equals(GROUP.getValue())) {
			st = GROUP;
		} else {
			st = UNKNOWN;
		}
		return st;
	}

	public String toString() {
		return value;
	}
}
