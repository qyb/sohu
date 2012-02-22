package com.bladefs.client.exception;

public class DataServiceException  extends Exception{
	private static final long serialVersionUID = -9223221446789353403L;
	
	public DataServiceException(){
		super();
	}
	public DataServiceException(String msg){
		super(msg);
	}
}
