/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.restlet.engine.util.DateUtils;


/**
 * @author Samuel
 *
 */
public class CommonUtilities {
	
	private final static SimpleDateFormat ResponseHeaderDateFormatter = 
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ");
	public final static String formatResponseHeaderDate(Date date) {
		// sample date string: Wed, 01 Mar 2009 12:00:00 GMT
		String rc = null;
		synchronized(ResponseHeaderDateFormatter) {
			rc = ResponseHeaderDateFormatter.format(date);
		}
		return rc;
	}

	private final static SimpleDateFormat ResponseTextDateFormatter = 
			new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	public final static String formatResponseTextDate(Date date) {
		// sample date string 2006-02-03T16:45:09.000Z
		String rc = null;
		synchronized(ResponseTextDateFormatter) {
			rc = ResponseTextDateFormatter.format(date);
		}
		return rc;
	}	
	
	public final static Date parseResponseDatetime(String date) {
		if (null != date)
			return DateUtils.parse(date);
		else
			return new Date();
	}
	
	public final static String getMD5(byte[] data) {
		String rc = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(data);
	        byte[] b64 = Base64.encodeBase64(digest.digest());
	        rc = new String(b64);					
		} catch (NoSuchAlgorithmException e) {
			
		}
		return rc;
	}
}
