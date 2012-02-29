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
import java.security.SignatureException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


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
	
	public final static String getBase64MD5(byte[] data) {
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

	  static final String HEXES = "0123456789abcdef";
	  public static String getMd5Hex( byte [] data ) {
	    if ( data == null ) {
	      return null;
	    }
	    final StringBuilder hex = new StringBuilder( 2 * data.length );
	    for ( final byte b : data ) {
	      hex.append(HEXES.charAt((b & 0xF0) >> 4))
	         .append(HEXES.charAt((b & 0x0F)));
	    }
	    return hex.toString();
	  }
	  
	  private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	  /**
	  * Computes RFC 2104-compliant HMAC signature.
	  * * @param data
	  * The data to be signed.
	  * @param key
	  * The signing key.
	  * @return
	  * The Base64-encoded RFC 2104-compliant HMAC signature.
	  * @throws
	  * java.security.SignatureException when signature generation fails
	  */
	public static String calculateRFC2104HMAC(String data, String key)
			throws java.security.SignatureException {
		String result;
		try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
					HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			byte[] b64 = Base64.encodeBase64(rawHmac);
			 
			result = new String(b64);

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : "
					+ e.getMessage());
		}
		return result;
	}
	  	  
}
