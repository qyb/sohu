/**
 * Copyright Sohu Inc. 2012
 */
package com.scss;

/**
 * @author Samuel
 *
 */
public final class Const {
    /** Service name for Amazon S3 */
    public static String S3_SERVICE_NAME = "Sohu S3";

    /** Default encoding used for text data */
    public static String DEFAULT_ENCODING = "UTF-8";

    /** HMAC/SHA1 Algorithm per RFC 2104, used when signing S3 requests */
    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    /** XML namespace URL used when sending S3 requests containing XML */
    public static final String XML_NAMESPACE = "http://s3.amazonaws.com/doc/2006-03-01/";

    /** Represents a null S3 version ID */
    public static final String NULL_VERSION_ID = "null";

    /**
     * HTTP status code indicating that preconditions failed and thus the
     * request failed.
     */
    public static final int FAILED_PRECONDITION_STATUS_CODE = 412;

    /** Kilobytes */
    public static final int KB = 1024;

    /** Megabytes */
    public static final int MB = 1024 * KB;

    /** Gigabytes */
    public static final long GB = 1024 * MB;

    /** The maximum allowed parts in a multipart upload. */
    public static final int MAXIMUM_UPLOAD_PARTS = 10000;

    /**
     * The default size of the buffer when uploading data from a stream. A
     * buffer of this size will be created and filled with the first bytes from
     * a stream being uploaded so that any transmit errors that occur in that
     * section of the data can be automatically retried without the caller's
     * intervention.
     */
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 128 * KB;
    
	public class REQUEST_METHOD {
		public static final String GET = "GET";
		public static final String PUT = "PUT";
		public static final String POST = "POST";
		public static final String DELETE = "DELETE";
		public static final String HEAD = "HEAD";
	}
}
