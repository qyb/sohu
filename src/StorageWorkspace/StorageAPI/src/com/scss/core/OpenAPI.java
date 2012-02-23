/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core;


import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

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

/**
 * @author Samuel
 *
 */
public abstract class OpenAPI implements ICallable {
	
	private MD5DigestInputStream md5DigestStream = null;
	
	
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
        	System.out.println("No MD5 digest algorithm available.  Unable to calculate " +
                     "checksum and verify data integrity.");
        	e.printStackTrace();
        }
        
        return this.md5DigestStream;
		
	}
	
	public byte[] getContentMD5() {
        return md5DigestStream.getMd5Digest();
	}

	public String getBase64ContentMD5() {
        byte[] b64 = Base64.encodeBase64(md5DigestStream.getMd5Digest());
        return new String(b64);		
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
