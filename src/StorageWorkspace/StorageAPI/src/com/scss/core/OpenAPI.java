/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core;


import com.scss.ICallable;
import com.scss.core.bucket.DELETE_BUCKET;
import com.scss.core.bucket.GET_BUCKET;
import com.scss.core.bucket.GET_SERVICE;
import com.scss.core.bucket.PUT_BUCKET;
import com.scss.core.object.DELETE_OBJECT;
import com.scss.core.object.GET_OBJECT;
import com.scss.core.object.POST_OBJECT;
import com.scss.core.object.PUT_OBJECT;

/**
 * @author Samuel
 *
 */
public abstract class OpenAPI implements ICallable {
	
	public void setSystemMeta() {
	
	}
	
	public String getUserMeta(APIRequest req) {
		return "";
	}
	
	// all open APIs
	
	// service APIs
	public final static ICallable GET_SERVICE = new GET_SERVICE();
	
	// bucket APIs
	public final static ICallable GET_BUCKET = new GET_BUCKET();
	public final static ICallable PUT_BUCKET = new PUT_BUCKET();
	public final static ICallable DELETE_BUCKET = new DELETE_BUCKET();
	
	// object APIs
	public final static ICallable GET_OBJECT = new GET_OBJECT();
	public final static ICallable PUT_OBJECT = new PUT_OBJECT();
	public final static ICallable POST_OBJECT = new POST_OBJECT();
	public final static ICallable DELETE_OBJECT = new DELETE_OBJECT();	
}
