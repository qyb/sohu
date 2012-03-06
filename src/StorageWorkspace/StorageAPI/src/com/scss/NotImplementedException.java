/**
 * 
 */
package com.scss;

/**
 * @author Samuel
 *
 */
public class NotImplementedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2818020496784759128L;

	/**
	 * 
	 */
	public NotImplementedException() {
	}

	/**
	 * @param message
	 */
	public NotImplementedException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public NotImplementedException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}

}
