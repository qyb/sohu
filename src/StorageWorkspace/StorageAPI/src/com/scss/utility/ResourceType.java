package com.scss.utility;

/**
 * 资源类型 
 * @author Jack.wu.xu
 */
public enum ResourceType {

	/**
	 * OBJECT.
	 */
	OBJECT("O"),

	/**
	 * BUCKET.
	 */
	BUCKET("B"),

	/**
	 * ACL.
	 */
	ACL("A"),
	/**
	 * LOG.
	 */
	LOG("L"),
	/**
	 * VERSION.
	 */
	VERSION("V"),
	/**
	 * 未知
	 */
	UNKNOWN("UN");

	private String value;

	ResourceType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	/**
	 * 静态方法获得类型的枚举
	 * 
	 * @param in
	 *            类型的枚举的名称
	 * @return
	 */
	public static ResourceType getResourceType(String in) {
		ResourceType st = null;
		if (in == null) {
			return UNKNOWN;
		}
		if (in.equals(OBJECT.getValue())) {
			st = OBJECT;
		} else if (in.equals(BUCKET.getValue())) {
			st = BUCKET;
		} else if (in.equals(LOG.getValue())) {
			st = LOG;
		} else if (in.equals(ACL.getValue())) {
			st = ACL;
		} else if (in.equals(VERSION.getValue())) {
			st = VERSION;
		} else {
			st = UNKNOWN;
		}
		return st;
	}

	public String toString() {
		return value;
	}
}
