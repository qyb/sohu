/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server;

import com.bfsapi.ICallable;

import com.bfsapi.server.bucket.GET_SERVICE;
import com.bfsapi.server.bucket.DELETE_BUCKET;
import com.bfsapi.server.bucket.GET_BUCKET;
import com.bfsapi.server.bucket.PUT_BUCKET;
import com.bfsapi.server.object.DELETE_OBJECT;
import com.bfsapi.server.object.GET_OBJECT;
import com.bfsapi.server.object.POST_OBJECT;
import com.bfsapi.server.object.PUT_OBJECT;

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
