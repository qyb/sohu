package com.scss.db.model;

public class Log {

	public static final int ALL = 0;

	public static final int DEBUG = ALL + 100;

	public static final int INFO = DEBUG + 100;

	public static final int WARN = INFO + 100;

	public static final int ERROR = WARN + 100;

	public static final int NONE = 0x7fffffff;

}
