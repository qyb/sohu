/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core;

import org.restlet.representation.StringRepresentation;

import com.scss.Headers;

/**
 * @author Samuel
 *
 */
public class ErrorResponse extends APIResponse{
	
	protected String code;
	protected String message;
	protected String resource;
	protected String requestID;
	protected int http_status;
	
	// TODO: use annotation or functional programming to make the error responses look like field

	public static ErrorResponse AccessDenied(APIRequest req) {
		//String text = ErrorResponse.getErrorResponseText("NoSuchBucket", "The specified bucket does not exist.", req.Path, req.RequestID);
		ErrorResponse resp = new ErrorResponse("AccessDenied", "Access Denied", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
	
	/**
	 * There is a problem with your AWS account that prevents the operation from completing successfully
	 * @param req
	 * @return
	 */
    public static ErrorResponse AccountProblem(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("AccountProblem", "There is a problem with your AWS account that prevents the operation from completing successfully.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
    /**
     * The e-mail address you provided is associated with more than one account.
     * @param req
     * @return
     */
    public static ErrorResponse AmbiguousGrantByEmailAddress(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("AmbiguousGrantByEmailAddress", "The e-mail address you provided is associated with more than one account.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
    
    /**
     * The Content-MD5 you specified did not match what we received.
     * @param req
     * @return
     */
    public static ErrorResponse BadDigest(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("BadDigest", "The Content-MD5 you specified did not match what we received.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
    
    /**
     * The requested bucket name is not available.The bucket namespace is shared by all users of the system.Please select a different name and try again.
     * @param req
     * @return
     */
    public static ErrorResponse BucketAlreadyExists(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("BucketAlreadyExists", "The requested bucket name is not available.The bucket namespace is shared by all users of the system.Please select a different name and try again.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "409 Conflict");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(409);
		return resp;
	}
    
    /**
     * Your previous request to create the named bucket succeeded and you already own it.
     * @param req
     * @return
     */
    public static ErrorResponse BucketAlreadyOwnedByYou(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("BucketAlreadyOwnedByYou", "Your previous request to create the named bucket succeeded and you already own it.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "409 Conflict");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(409);
		return resp;
	}
    /**
     * The bucket you tried to delete is not empty.
     * @param req
     * @return
     */
   public static ErrorResponse BucketNotEmpty(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("BucketNotEmpty", "The bucket you tried to delete is not empty.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "409 Conflict");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(409);
		return resp;
	}

/**
    * This request does not support credentials.
    * @param req
    * @return
    */
   public static ErrorResponse CredentialsNotSupported(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("CredentialsNotSupported", "This request does not support credentials.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Cross location logging not allowed.Buckets in one geographic location cannot log information to a bucket in another location.
    * @param req
    * @return
    */
   public static ErrorResponse CrossLocationLoggingProhibited(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("CrossLocationLoggingProhibited", "Cross location logging not allowed.Buckets in one geographic location cannot log information to a bucket in another location.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   /**
    * Your proposed upload is smaller than the minimum allowed object size.
    * @param req
    * @return
    */
   public static ErrorResponse EntityTooSmall(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("EntityTooSmall", "Your proposed upload is smaller than the minimum allowed object size.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your proposed upload exceeds the maximum allowed object size.
    * @param req
    * @return
    */
   public static ErrorResponse EntityTooLarge(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("EntityTooLarge", "Your proposed upload exceeds the maximum allowed object size.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The provided token has expired.
    * @param req
    * @return
    */
   public static ErrorResponse ExpiredToken(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("ExpiredToken", "The provided token has expired.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Indicates that the Versioning configuration specified in the request is invalid.
    * @param req
    * @return
    */
   public static ErrorResponse IllegalVersioningConfigurationException(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("IllegalVersioningConfigurationException", "Indicates that the Versioning configuration specified in the request is invalid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   
   /**
    * You did not provide the number of bytes specified by the Content-Length HTTP header.
    * @param req
    * @return
    */
   public static ErrorResponse IncompleteBody(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("IncompleteBody", "You did not provide the number of bytes specified by the Content-Length HTTP header.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * POST requires exactly one file upload per request.
    * @param req
    * @return
    */
   public static ErrorResponse IncorrectNumberOfFilesInPostRequest(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("IncorrectNumberOfFilesInPostRequest", "POST requires exactly one file upload per request.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Inline data exceeds the maximum allowed size.
    * @param req
    * @return
    */
   public static ErrorResponse InlineDataTooLarge(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InlineDataTooLarge", "Inline data exceeds the maximum allowed size.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * We encountered an internal error.Please try again.
    * @param req
    * @return
    */
   public static ErrorResponse InternalError(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InternalError", "We encountered an internal error.Please try again.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "500 Internal Server Error");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(500);
		return resp;
	}
   /**
    * The AWS Access Key Id you provided does not exist in our records.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidAccessKeyId(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidAccessKeyId", "The AWS Access Key Id you provided does not exist in our records.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   /**
    * You must specify the Anonymous role.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidAddressingHeader(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidAddressingHeader", "You must specify the Anonymous role.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "N/A");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(0);
		return resp;
	}
   /**
    * Invalid Argument.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidArgument(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidArgument", "Invalid Argument.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The specified bucket is not valid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidBucketName(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidBucketName", "The specified bucket is not valid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The request is not valid with the current state of the bucket.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidBucketState(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidBucketState", "The request is not valid with the current state of the bucket.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "409 Conflict");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(409);
		return resp;
	}
   /**
    * The Content-MD5 you specified was an invalid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidDigest(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidDigest", "The Content-MD5 you specified was an invalid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The specified location constraint is not valid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidLocationConstraint(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidLocationConstraint", "The specified location constraint is not valid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * One or more of the specified parts could not be found.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidPart(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidPart", "One or more of the specified parts could not be found.The part might not have been uploaded, or the specified entity tag might not have matched the part's entity tag.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The list of parts was not in ascending order.Parts list must specified in order by part number.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidPartOrder(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidPartOrder", "The list of parts was not in ascending order.Parts list must specified in order by part number.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * All access to this object has been disabled.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidPayer(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidPayer", "All access to this object has been disabled.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   
   /**
    * The content of the form does not meet the conditions specified in the policy document.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidPolicyDocument(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidPolicyDocument", "The content of the form does not meet the conditions specified in the policy document.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The requested range cannot be satisfied.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidRange(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidRange", "The requested range cannot be satisfied.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "416 Requested Range Not Satisfiable");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(416);
		return resp;
	}
   /**
    * SOAP requests must be made over an HTTPS connection.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidRequest(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidRequest", "SOAP requests must be made over an HTTPS connection.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The provided security credentials are not valid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidSecurity(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidSecurity", "The provided security credentials are not valid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   /**
    * The SOAP request body is invalid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidSOAPRequest(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidSOAPRequest", "The SOAP request body is invalid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The storage class you specified is not valid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidStorageClass(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidStorageClass", "The storage class you specified is not valid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The target bucket for logging does not exist, is not owned by you, or does not have the appropriate grants for the log-delivery group.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidTargetBucketForLogging(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidTargetBucketForLogging", "The target bucket for logging does not exist, is not owned by you, or does not have the appropriate grants for the log-delivery group.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The provided token is malformed or otherwise invalid.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidToken(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidToken", "The provided token is malformed or otherwise invalid.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Couldn't parse the specified URI.
    * @param req
    * @return
    */
   public static ErrorResponse InvalidURI(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("InvalidURI", "Couldn't parse the specified URI.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your key is too long.
    * @param req
    * @return
    */
   public static ErrorResponse KeyTooLong(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("KeyTooLong", "Your key is too long.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The XML you provided was not well-formed or did not validate against our published schema.
    * @param req
    * @return
    */
   public static ErrorResponse MalformedACLError(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MalformedACLError", "The XML you provided was not well-formed or did not validate against our published schema.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The body of your POST request is not well-formed multipart/form-data. 
    * @param req
    * @return
    */
   public static ErrorResponse MalformedPOSTRequest(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MalformedPOSTRequest", "The body of your POST request is not well-formed multipart/form-data.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your request was too big.
    * @param req
    * @return
    */
   public static ErrorResponse MaxMessageLengthExceeded(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MaxMessageLengthExceeded", "Your request was too big.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your POST request fields preceding the upload file were too large.
    * @param req
    * @return
    */
   public static ErrorResponse MaxPostPreDataLengthExceededError(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MaxPostPreDataLengthExceededError", "Your POST request fields preceding the upload file were too large.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your metadata headers exceed the maximum allowed metadata size.
    * @param req
    * @return
    */
   public static ErrorResponse MetadataTooLarge(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MetadataTooLarge", "Your metadata headers exceed the maximum allowed metadata size.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The specified method is not allowed against this resource.
    * @param req
    * @return
    */
   public static ErrorResponse MethodNotAllowed(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MethodNotAllowed", "The specified method is not allowed against this resource.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "405 Method Not Allowed");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(405);
		return resp;
	}
  /**
   * A SOAP attachment was expected,but none were found.
   * @param req
   * @return
   */
   public static ErrorResponse MissingAttachment(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MissingAttachment", "A SOAP attachment was expected,but none were found.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "N/A");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(0);
		return resp;
	}
   /**
    * You must provide the Content-Length HTTP header.
    * @param req
    * @return
    */
   public static ErrorResponse MissingContentLength(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MissingContentLength", "You must provide the Content-Length HTTP header.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "411 Length Required");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(411);
		return resp;
	}
   /**
    * This happens when the user sends an empty xml document as a request.
    * @param req
    * @return
    */
   public static ErrorResponse MissingRequestBodyError(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MissingRequestBodyError", "This happens when the user sends an empty xml document as a request.The error message is, 'Request bodyis empty.'", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The SOAP 1.1 request is missing a security element.
    * @param req
    * @return
    */
   public static ErrorResponse MissingSecurityElement(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MissingSecurityElement", "The SOAP 1.1 request is missing a security element.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your request was missing a required header.
    * @param req
    * @return
    */
   public static ErrorResponse MissingSecurityHeader(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("MissingSecurityHeader", "Your request was missing a required header.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * NoLoggingStatusForKey
    * @param req
    * @return
    */
   public static ErrorResponse NoLoggingStatusForKey(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NoLoggingStatusForKey", "There is no such thing as a logging status sub-resource for a key.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
    /**
     * The specified bucket does not exist.
     * @param req
     * @return
     */
	public static ErrorResponse NoSuchBucket(APIRequest req) {
		//String text = ErrorResponse.getErrorResponseText("NoSuchBucket", "The specified bucket does not exist.", req.Path, req.RequestID);
		ErrorResponse resp = new ErrorResponse("NoSuchBucket", "The specified bucket does not exist.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
	/**
	 * The specified key does not exist.
	 * @param req
	 * @return
	 */
	public static ErrorResponse NoSuchKey(APIRequest req) {
	
		ErrorResponse resp = new ErrorResponse("NoSuchKey", "The specified key does not exist.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
	/**
	 * The lifecycle configuration does not exist.
	 * @param req
	 * @return
	 */
	public static ErrorResponse NoSuchLifecycleConfiguration(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NoSuchLifecycleConfiguration", "The lifecycle configuration does not exist.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
	/**
	 * The specified multipart upload does not exist.
	 * @param req
	 * @return
	 */
    public static ErrorResponse NoSuchUpload(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NoSuchUpload", "The specified multipart upload does not exist. The upload ID might be invalid, or the multipart upload might have been aborted or completed.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
    /**
     * Indicates that the version ID specified in the request does not match an existing version.
     * @param req
     * @return
     */
   public static ErrorResponse NoSuchVersion(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NoSuchVersion", "Indicates that the version ID specified in the request does not match an existing version.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
   /**
    * A header you provided implies functionality that is not implemented.
    * @param req
    * @return
    */
   public static ErrorResponse NotImplemented(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NotImplemented", "A header you provided implies functionality that is not implemented.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "501 NotImplemented");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(501);
		return resp;
	}
   /**
    * Your account is not signed up for the Amazon S3 service.
    * @param req
    * @return
    */
   public static ErrorResponse NotSignedUp(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NotSignedUp", "Your account is not signed up for the Amazon S3 service. You must sign up before you can use Amazon S3. You can sign up at the following URL:http://aws.amazon.com/s3", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   /**
    * The specified bucket does not have a bucket policy.
    * @param req
    * @return
    */
   public static ErrorResponse NotSuchBucketPolicy(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NotSuchBucketPolicy", "The specified bucket does not have a bucket policy.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
   /**
    * A conflicting conditional operation is currently in progress against this resource. Please try again.
    * @param req
    * @return
    */
   public static ErrorResponse OperationAborted(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("OperationAborted", "A conflicting conditional operation is currently in progress against this resource. Please try again.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "409 Conflict");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(409);
		return resp;
	}
   /**
    * The bucket you are attempting to access must be addressed using the specified endpoint.
    * @param req
    * @return
    */
   public static ErrorResponse PermanentRedirect(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("PermanentRedirect", "The bucket you are attempting to access must be addressed using the specified endpoint. Please send all future requests to this endpoint.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "301 Moved Permanently");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(301);
		return resp;
	}
   /**
    * At least one of the preconditions you specified did not hold.
    * @param req
    * @return
    */
   public static ErrorResponse PreconditionFailed(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("PreconditionFailed", "At least one of the preconditions you specified did not hold.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "412 Precondition Failed");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(412);
		return resp;
	}
   /**
    * Temporary redirect. 
    * @param req
    * @return
    */
   public static ErrorResponse Redirect(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("Redirect", "Temporary redirect.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "307 Moved Temporarily");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(307);
		return resp;
	}
   /**
    * Bucket POST must be of the enclosure-type multipart/form-data.
    * @param req
    * @return
    */
   public static ErrorResponse RequestIsNotMultiPartContent(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("RequestIsNotMultiPartContent", "Bucket POST must be of the enclosure-type multipart/form-data.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * Your socket connection to the server was not read from or written to within the timeout period.
    * @param req
    * @return
    */
   public static ErrorResponse RequestTimeout(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("RequestTimeout", "Your socket connection to the server was not read from or written to within the timeout period.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The difference between the request time and the server's time is too large.
    * @param req
    * @return
    */
   public static ErrorResponse RequestTimeTooSkewed(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("RequestTimeTooSkewed", "The difference between the request time and the server's time is too large.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   /**
    * Requesting the torrent file of a bucket is not permitted.
    * @param req
    * @return
    */
   public static ErrorResponse RequestTorrentOfBucketError(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("RequestTorrentOfBucketError", "Requesting the torrent file of a bucket is not permitted.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The request signature we calculated does not match the signature you provided.
    * @param req
    * @return
    */
   public static ErrorResponse SignatureDoesNotMatch(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("SignatureDoesNotMatch", "The request signature we calculated does not match the signature you provided. Check your AWS Secret Access Key and signing method.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "403 Forbidden");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(403);
		return resp;
	}
   /**
    * Please reduce your request rate.
    * @param req
    * @return
    */
   public static ErrorResponse ServiceUnavailable(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("ServiceUnavailable", "Please reduce your request rate.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "503 Service Unavailable");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(503);
		return resp;
	}
   /**
    * Please reduce your request rate.
    * @param req
    * @return
    */
   public static ErrorResponse SlowDown(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("SlowDown", "Please reduce your request rate.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "503 Slow Down");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(503);
		return resp;
	}
   /**
    * You are being redirected to the bucket while DNS updates.
    * @param req
    * @return
    */
   public static ErrorResponse TemporaryRedirect(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("TemporaryRedirect", "You are being redirected to the bucket while DNS updates.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "307 Moved Temporarily");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(307);
		return resp;
	}
	/**
	 * The provided token must be refreshed.
	 * @param req
	 * @return
	 */
   public static ErrorResponse TokenRefreshRequired(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("TokenRefreshRequired", "The provided token must be refreshed.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * You have attempted to create more buckets than allowed.
    * @param req
    * @return
    */
   public static ErrorResponse TooManyBuckets(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("TooManyBuckets", "You have attempted to create more buckets than allowed.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * This request does not support content.
    * @param req
    * @return
    */
   public static ErrorResponse UnexpectedContent(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("UnexpectedContent", "This request does not support content.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * The e-mail address you provided does not match any account on record.
    * @param req
    * @return
    */
   public static ErrorResponse UnresolvableGrantByEmailAddress(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("UnresolvableGrantByEmailAddress", "The e-mail address you provided does not match any account on record.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
	/**
	 * The bucket POST must contain the specified field name.
	 * @param req
	 * @return
	 */
   public static ErrorResponse UserKeyMustBeSpecified(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("UserKeyMustBeSpecified", "The bucket POST must contain the specified field name. If it is specified,please check the order of the fields.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	}
   /**
    * delete the bucket before delete all objects association with it
    * @param req
    * @return
    */
   public static ErrorResponse DeleteBucketBeforeDeleteObject(APIRequest req){
	   
	    ErrorResponse resp = new ErrorResponse("DeleteBucketBeforeDeleteObject", "delete the bucket before delete all objects association with it", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "400 Bad Request");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(400);
		return resp;
	   
   }
   /**
    * The object does not exist.
    * @param req
    * @return
    */
   public static ErrorResponse NoSuchObject(APIRequest req) {
		
		ErrorResponse resp = new ErrorResponse("NoSuchObject", "The object does not exist.", req.Path, req.RequestID);
		resp.getHeaders().put(Headers.STATUS, "404 Not Found");
		resp.Repr = new StringRepresentation(resp.getResponseText());
		
		resp.setHttp_status(404);
		return resp;
	}
	
//	public static ErrorResponse createErrorResponse(APIRequest req, String code, String message, String status) {
//		//String text = ErrorResponse.getErrorResponseText("NoSuchBucket", "The specified bucket does not exist.", req.Path, req.RequestID);
//		ErrorResponse resp = new ErrorResponse(code, message, req.Path, req.RequestID);
//		resp.getHeaders().put(CommonResponseHeader.STATUS, "404 Not Found");
//		resp.Repr = new StringRepresentation(resp.getResponseText());
//		
//		resp.setHttp_status(404);
//		return resp;
//	}
//	
	
	
	
	
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
		this.MediaType = Mimetypes.MIMETYPE_XML; 
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
	
	public int getHttp_status() {
		return http_status;
	}

	public void setHttp_status(int http_status) {
		this.http_status = http_status;
	}
}
