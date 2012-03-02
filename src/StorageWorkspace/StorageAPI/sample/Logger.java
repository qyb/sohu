package com.scss.utility;

import org.apache.log4j.spi.LoggerFactory;


public class Logger extends org.apache.log4j.Logger {

	public Logger(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public static Logger getLogger(Class<?> clazz) {
		return (Logger)org.apache.log4j.Logger.getLogger(clazz);
	}
	
	public static Logger getLogger(String name) {
		return (Logger)org.apache.log4j.Logger.getLogger(name);
	}
	
	public static Logger getLogger(String name, LoggerFactory factory) {
		return (Logger)org.apache.log4j.Logger.getLogger(name, factory);
	}

	public static Logger getRootLogger() {
		return (Logger)org.apache.log4j.Logger.getRootLogger();
	}


	public void debug(String format, Object... args) {
		debug(String.format(format, args));
	}

	public void info(String format, Object... args) {
		info(String.format(format, args));
	}

	public void warn(String format, Object... args) {
		warn(String.format(format, args));
	}

	public void error(String format, Object... args) {
		error(String.format(format, args));
	}

	public void fatal(String format, Object... args) {
		fatal(String.format(format, args));
	}
	
}
