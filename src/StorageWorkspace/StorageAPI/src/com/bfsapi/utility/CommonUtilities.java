/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.utility;

import java.text.SimpleDateFormat;
import java.util.Date;


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
}
