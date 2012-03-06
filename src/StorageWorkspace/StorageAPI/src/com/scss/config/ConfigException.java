/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.config;

/**
 * @author Samuel
 *
 */
public class ConfigException extends RuntimeException {
	private static final long serialVersionUID = 3209650626836262402L;

	/**
	 * 
	 */
	public ConfigException() {
	}

	/**
	 * @param message
	 */
	public ConfigException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ConfigException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

}
