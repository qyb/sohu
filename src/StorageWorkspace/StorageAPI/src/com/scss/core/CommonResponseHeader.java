/**
 * Copyright (c) Sohu Inc. 2012
 * 
 */
package com.scss.core;

/**
 * The common headers enumerations for response.
 * 
 * @author Samuel
 * 
 */
public class CommonResponseHeader {
	
	/*
	 * The length in bytes of the body in the response.
	 * 
	 * Type: String
	 * Default: None
	 */
	public static final String CONTENT_LENGTH = "Content-Length";

	/*
	 * The content type of the resource. Example: text/plain
	 * 
	 * Type: String
	 * Default: None
	 * Required: No
	 */
	public static final String CONTENT_TYPE = "Content-Type";
	
	
	/*
	 * specifies whether the connection to the server is open or closed.
	 * 
	 * Type: Enum
	 * Valid Values: open | close
	 * Default: None
	 */
	public static final String CONNECTION = "Connection";
	
	/*
	 * The date and time we responded, 
	 * for example, Wed, 01 Mar 2009 12:00:00 GMT.
	 * 
	 * Type: String
	 * Default: None
	 */
	public static final String DATE = "Date";
	
	/*
	 * The entity tag is a hash of the object.The ETag only reflects changes to the
	 * contents of an object, not its metadata.The ETag is determined when an object
	 * is created. For objects created by the PUT Object operation and the POST Object
	 * operation, the ETag is a quoted, 32-digit hexadecimal string representing the
	 * MD5 digest of the object data. For other objects, the ETag may or may not be an
	 * MD5 digest of the object data. If the ETag is not an MD5 digest of the object data,
	 * it will contain one or more non-hexadecimal characters and/or will consist of less
	 * than 32 or more than 32 hexadecimal digits.
	 * 
	 * Type: String
	 */
	public static final String ETAG = "ETag";
	
	/*
	 * The name of the server that created the response.
	 * 
	 * Type: String
	 * Default: BFSAPI
	 */
	public static final String SERVER = "Server";
	
	/*
	 * Specifies whether the object returned was (true) 
	 * or was not (false) a Delete Marker.
	 * 
	 * Type: Boolean
	 * Valid Values: true | false
	 * Default: false
	 */
	public static final String X_SOHU_DELETE_MARKER = "x-amz-delete-marker";
	
	/*
	 * A special token that helps BFSAPI troubleshoot problems.
	 * 
	 * Type: String
	 * Default: None
	 */
	public static final String X_SOHU_ID_2 = "x-amz-id-2";
	
	/*
	 * A value created by BFSAPI that uniquely identifies the request. 
	 * In the unlikely event that you have problems with BFSAPI, 
	 * BFSAPI can use this value to troubleshoot the problem.
	 * 
	 * Type: String
	 * Default: None
	 */
	public static final String X_SOHU_REQUEST_ID = "x-amz-request-id";
	
	/*
	 * The version of the object.When you enable versioning, BFSAPI generates a
	 * random number for objects added to a bucket.The value is UTF-8 encoded and
	 * URL ready.When you PUT an object in a bucket where versioning has been
	 * suspended, the version ID is always null.
	 * 
	 * Type: String
	 * Valid Values: null | any URL-ready, UTF-8 encoded string
	 * Default: null
	 * 
	 */
	public static final String X_SOHU_VERSION_ID = "x-amz-version-id";
	
	
	public static final String STATUS = "Status";

}
