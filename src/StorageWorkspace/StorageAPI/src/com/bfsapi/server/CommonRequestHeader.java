/**
 * Copyright (c) Sohu Inc. 2012
 * 
 */
package com.bfsapi.server;

/**
 * The common headers enumerations for request.
 * 
 * @author Samuel
 * 
 */
public class CommonRequestHeader {
	/*
	 * The information required for request authentication.
	 * 
	 * Type: String
	 * Default: None
	 * Required: Yes
	 */
	public static final String AUTHORIZATION = "Authorization";
	
	/*
	 * Length of the message (without the headers) according to RFC 2616.
	 * 
	 * Type: String
	 * Default: None
	 * Condition: Required for PUTs and operations that load
	 *            XML, such as logging and ACLs.
	 * Required: Conditional
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
	 * The current date and time according to the requester.
	 * Example:Wed, 01 Mar 2009 12:00:00 GMT
	 * 
	 * Type: String
	 * Default: None
	 * Requrired: Yes
	 */
	public static final String DATE = "Date";
	
	/*
	 * For path-style requests, the value is bfsapi.sohu.com. 
	 * For virtual-style requests, the value is BucketName.bfsapi.sohu.com. 
	 * For more information, go to Developer Guide .
	 * 
     * Type: String
	 * Default: None
	 * Condition: Required for HTTP 1.1 (most toolkits add this header 
	 *            automatically); optional for HTTP/1.0 requests.
	 * Require: Conditional
	 * 
	 */
	public static final String HOST = "Host";
	
	/*
	 * This header can be used in the following scenarios:
	 * • Provide security tokens for Sohu Payment operations—Each request 
	 *   that uses Sohu Payment requires two x-sohu-security-token headers: 
	 *   one for the product token and one for the user token. When BFSAPI 
	 *   receives an authenticated request, it compares the computed signature
	 *   with the provided signature. Improperly formatted multi-value headers
	 *   used to calculate a signature can cause authentication issues
	 * • Provide security token when using temporary security credentials—When 
	 *   making requests using temporary security credentials you obtained from
	 *   IAM you must provide a security token using this header.To learn more 
	 *   about temporary security credentials, go to Making Requests.
	 *   s
	 * Type: String
	 * Default: None
	 * Condition: Required for requests that use Sohu Payment and requests 
	 *            that are signed using temporary security credentials.
	 * Required: Condition
	 */
	public static final String X_SOHU_SECURITY_TOKEN = "x-amz-security-token";
	
}
