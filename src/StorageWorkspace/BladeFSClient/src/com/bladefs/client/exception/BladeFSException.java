package com.bladefs.client.exception;

public class BladeFSException extends Exception {
	private static final long serialVersionUID = 6010485704578761703L;
	public BladeFSException(){
		super();
	}
	public BladeFSException(String msg){
		super(msg);
	}
}
