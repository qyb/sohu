package com.sohu.wuhan;


import android.os.Handler;

class Task {
	
	private String target;
	private String method;
	private String content;
	private Handler handler;
	
	public Task(String __target, String __method, String __content, Handler __handler) {
		target 		= __target;
		method 		= __method;
		content		= __content;
		handler 	= __handler;
	}
	
	public String getTarget()		{ return target; }
	public String getMethod() 		{ return method; }
	public String getContent() 		{ return content; }
	public Handler getHandler() 	{ return handler; }
}