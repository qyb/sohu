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
	public final static String formatResponseDatetime(Date date) {
		// sample date string 2006-02-03T16:45:09.000Z
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSZ");
		return fmt.format(date);
	}
	
	public final static Date parseResponseDatetime(String date) {
		Date rc = new Date();
		if (null != date)
			rc = DateUtils.parse(date);
		return rc;
	}
}
