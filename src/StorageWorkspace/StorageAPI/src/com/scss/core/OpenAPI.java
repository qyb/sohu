/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core;


import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.scss.Headers;
import com.scss.ICallable;
import com.scss.core.bucket.DELETE_BUCKET;
import com.scss.core.bucket.GET_BUCKET;
import com.scss.core.bucket.GET_SERVICE;
import com.scss.core.bucket.PUT_BUCKET;
import com.scss.core.object.DELETE_OBJECT;
import com.scss.core.object.GET_OBJECT;
import com.scss.core.object.HEAD_OBJECT;
import com.scss.core.object.POST_OBJECT;
import com.scss.core.object.PUT_OBJECT;
import com.scss.utility.CommonUtilities;

/**
 * @author Samuel
 *
 */
public abstract class OpenAPI implements ICallable {
	
	private MD5DigestInputStream md5DigestStream = null;
	protected Logger logger = Logger.getLogger(this.getClass());
	
	public void setSystemMeta() {
	
	}
	
	public String getUserMeta(APIRequest req) {
		return "";
	}
	
	public InputStream hookMD5Stream(InputStream stream) {
	
		this.md5DigestStream = null;
        try {
            md5DigestStream = new MD5DigestInputStream(stream);
        } catch (NoSuchAlgorithmException e) {
        	logger.error("No MD5 digest algorithm available.  Unable to calculate " +
                     "checksum and verify data integrity.");
        	logger.debug(e.getMessage());
        	//e.printStackTrace();
        }
        
        return this.md5DigestStream;
		
	}
	
	public String getContentMD5() {
        return CommonUtilities.getMd5Hex(md5DigestStream.getMd5Digest());
	}

	public String getBase64ContentMD5() {
        byte[] b64 = Base64.encodeBase64(md5DigestStream.getMd5Digest());
        return new String(b64);		
	}

	public static void setCommResponseHeaders(Map<String, String> resp_headers,
			APIRequest req) {
		resp_headers
				.put(Headers.EXTENDED_REQUEST_ID, req.getUser().getSohuId());
		resp_headers.put(Headers.REQUEST_ID, UUID.randomUUID().toString());
	}
	
	// ----- open APIs -----
	
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
	public final static ICallable HEAD_OBJECT = new HEAD_OBJECT();	
}
