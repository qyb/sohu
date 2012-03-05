/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.utility;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.restlet.engine.util.DateUtils;

import com.scss.Const;
import com.scss.ScssException;


/**
 * @author Samuel
 *
 */
public class CommonUtilities {
	
	private final static Logger logger = Logger.getLogger(CommonUtilities.class);
	
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
	
	
//    protected static final DateUtils dateUtils = new DateUtils();

//    public static Date parseIso8601Date(String dateString) throws ParseException {
//        return dateUtils.parseIso8601Date(dateString);
//    }
//
//    public static String formatIso8601Date(Date date) {
//        return dateUtils.formatIso8601Date(date);
//    }
//
//    public static Date parseRfc822Date(String dateString) throws ParseException {
//        return dateUtils.parseRfc822Date(dateString);
//    }
//
//    public static String formatRfc822Date(Date date) {
//        return dateUtils.formatRfc822Date(date);
//    }

    /**
     * Returns true if the specified ETag was from a multipart upload.
     *
     * @param eTag
     *            The ETag to test.
     *
     * @return True if the specified ETag was from a multipart upload, otherwise
     *         false it if belongs to an object that was uploaded in a single
     *         part.
     */
    public static boolean isMultipartUploadETag(String eTag) {
        return eTag.contains("-");
    }

    /**
     * Safely converts a string to a byte array, first attempting to explicitly
     * use our preferred encoding (UTF-8), and then falling back to the
     * platform's default encoding if for some reason our preferred encoding
     * isn't supported.
     *
     * @param s
     *            The string to convert to a byte array.
     *
     * @return The byte array contents of the specified string.
     */
    public static byte[] toByteArray(String s) {
        try {
            return s.getBytes(Const.DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.warn("Encoding " + Const.DEFAULT_ENCODING + " is not supported", e);
            return s.getBytes();
        }
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
	  
		/**
		 * Computes RFC 2104-compliant HMAC signature. * 
		 * 
		 * @param data 
		 * 				The data to be signed.
		 * 
		 * @param key
		 * 				The signing key.
		 * 
		 * @param algo
		 * 				The algorithmic
		 * 
		 * @return The Base64-encoded RFC 2104-compliant HMAC signature.
		 * @throws java.security.SignatureException
		 *             when signature generation fails
		 */
		public static String calculateRFC2104HMAC(byte[] data, byte[] key, String algo)
				throws java.security.SignatureException {
			
			assert(null != key);
			assert(null != data);
			
			String result;
			try {

				// get an hmac_sha1 key from the raw key bytes
				SecretKeySpec signingKey = new SecretKeySpec(key,
						algo);

				// get an hmac_sha1 Mac instance and initialize with the signing key
				Mac mac = Mac.getInstance(algo);
				mac.init(signingKey);

				// compute the hmac on input data bytes
				byte[] rawHmac = mac.doFinal(data);

				// base64-encode the hmac
				byte[] b64 = Base64.encodeBase64(rawHmac);
				 
				result = new String(b64);

			} catch (Exception e) {
				throw new SignatureException("Failed to generate HMAC : "
						+ e.getMessage());
			}
			return result;
		}

	  
	    /**
	     * Removes any surrounding quotes from the specified string and returns a
	     * new string.
	     *
	     * @param s
	     *            The string to check for surrounding quotes.
	     *
	     * @return A new string created from the specified string, minus any
	     *         surrounding quotes.
	     */
	    public static String removeQuotes(String s) {
	        if (s == null) return null;

	        s = s.trim();
	        if (s.startsWith("\"")) s = s.substring(1);
	        if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);

	        return s;
	    }

	    /**
	     * URL encodes the specified string and returns it.  All keys specified by
	     * users need to URL encoded.  The URL encoded key needs to be used in the
	     * string to sign (canonical resource path).
	     *
	     * @param s
	     *            The string to URL encode.
	     *
	     * @return The new, URL encoded, string.
	     */
	    public static String urlEncode(String s) {
	        if (s == null) return null;

	        try {
	            String encodedString = URLEncoder.encode(s, Const.DEFAULT_ENCODING);
	            // Web browsers do not always handle '+' characters well, use the
	            // well-supported '%20' instead.
	            return encodedString.replaceAll("\\+", "%20");
	        } catch (UnsupportedEncodingException e) {
	            throw new ScssException("Unable to encode path: " + s, e);
	        }
	    }

	    /**
	     * Converts the specified request object into a URL, containing all the
	     * specified parameters, the specified request endpoint, etc.
	     *
	     * @param request
	     *            The request to convert into a URL.
	     * @return A new URL representing the specified request.
	     *
	     * @throws ScssException
	     *             If the request cannot be converted to a well formed URL.
	     */
//	    public static URL convertRequestToUrl(Request<?> request) {
//	        String urlString =  request.getEndpoint()
//	            + "/" + request.getResourcePath();
//
//	        boolean firstParam = true;
//	        for (String param : request.getParameters().keySet()) {
//	            if (firstParam) {
//	                urlString += "?";
//	                firstParam = false;
//	            } else {
//	                urlString += "&";
//	            }
//
//	            String value = request.getParameters().get(param);
//	            urlString += param + "=" + CommonUtilities.urlEncode(value);
//	        }
//
//	        try {
//	            return new URL(urlString);
//	        } catch (MalformedURLException e) {
//	            throw new ScssException(
//	                    "Unable to convert request to well formed URL: " + e.getMessage(), e);
//	        }
//	    }

	    /**
	     * Returns a new string created by joining each of the strings in the
	     * specified list together, with a comma between them.
	     *
	     * @param strings
	     *            The list of strings to join into a single, comma delimited
	     *            string list.
	     * @return A new string created by joining each of the strings in the
	     *         specified list together, with a comma between strings.
	     */
	    public static String join(List<String> strings) {
	        String result = "";

	        boolean first = true;
	        for (String s : strings) {
	            if (!first) result += ", ";

	            result += s;
	            first = false;
	        }

	        return result;
	    }
	  	  
}
