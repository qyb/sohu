/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core;

import org.restlet.representation.StringRepresentation;


/**
 * @author Samuel
 *
 */
public class ErrorResponse extends APIResponse{
	
	protected String code;
	protected String message;
	protected String resource;
	protected String requestID;
	
	// TODO: use annotation or functional programming to make the error responses look like field

	public static ErrorResponse AccessDenied(APIRequest req) {
		ErrorResponse resp = new ErrorResponse("AccessDenied", "Access Denied", req.Path, req.RequestID);
		resp.getHeaders().put(CommonResponseHeader.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		resp.MediaType = MediaTypes.APPLICATION_XML;
		return resp;
	}
	
	public static ErrorResponse BucketAlreadyExists(APIRequest req) {
		ErrorResponse resp = new ErrorResponse("BucketAlreadyExists", 
				"The requested bucket name is not" +
				"available.The bucket namespace is" +
				"shared by all users of the system." +
				"Please select a different name and try" +
				"again.", req.Path, req.RequestID);
		resp.getHeaders().put(CommonResponseHeader.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		resp.MediaType = MediaTypes.APPLICATION_XML;
		return resp;
	}	
	
	public static ErrorResponse NoSuchBucket(APIRequest req) {
		ErrorResponse resp = new ErrorResponse("NoSuchBucket", "The specified bucket does not exist.", req.Path, req.RequestID);
		resp.getHeaders().put(CommonResponseHeader.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		resp.MediaType = MediaTypes.APPLICATION_XML;
		return resp;
	}
	
	public static ErrorResponse createErrorResponse(APIRequest req, String code, String message, String status) {
		ErrorResponse resp = new ErrorResponse(code, message, req.Path, req.RequestID);
		resp.getHeaders().put(CommonResponseHeader.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		resp.MediaType = MediaTypes.APPLICATION_XML;
		return resp;
	}
	
	
	protected static String getErrorResponseText(String code, String message, String resource, String requestID) {
		return String.format(ErrorResponse.XmlTemplate, code, message, resource, requestID);
	}
	
	protected static final String XmlTemplate = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
			+ "\n<Error>" 
			+ "\n	<Code>%s</Code>"
			+ "\n	<Message>%s</Message>"
			+ "\n	<Resource>%s</Resource>"
			+ "\n	<RequestId>%s</RequestId>" 
			+ "\n</Error>";

	public ErrorResponse(String code, String message, String resource, String requestID) {
		this.code = code;
		this.message = message;
		this.resource = resource;
		this.requestID = requestID;
	}
	
	public String getResponseText() {
		return String.format(ErrorResponse.XmlTemplate, code, message, resource, requestID);
	}
	
	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getResource() {
		return resource;
	}

	public String getRequestID() {
		return requestID;
	}
}
