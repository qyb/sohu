package com.scss;

public class ScssException extends RuntimeException {

	private static final long serialVersionUID = -5501969325000854340L;

	/**
     * Creates a new ScssException with the specified message and root
     * cause.
     * 
     * @param message
     *            An error message describing why this exception was thrown.
     * @param t
     *            The underlying cause of this exception.
     */
	public ScssException(String message, Throwable t) {
		super(message, t);
	}
	
	/**
     * Creates a new ScssException with the specified message and root
     * cause.
     * 
     * @param message
     *            An error message describing why this exception was thrown.
     */
	public ScssException(String message) {
		super(message);
	}
}
