/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.restlet.engine.util.DateUtils;


/**
 * @author Samuel
 *
 */
public class CommonUtilities {
	public final static String formatResponseHeaderDate(Date date) {
		// sample date string: Wed, 01 Mar 2009 12:00:00 GMT
		SimpleDateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ");
		return fmt.format(date);
	}

	public final static String formatResponseTextDate(Date date) {
		// sample date string 2006-02-03T16:45:09.000Z
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		return fmt.format(date);
	}	
	
	public final static Date parseResponseDatetime(String date) {
		Date rc = new Date();
		if (null != date)
			rc = DateUtils.parse(date);
		return rc;
	}
}
