/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core;

import java.util.HashMap;
import java.util.Map;

import org.restlet.representation.Representation;

/**
 * @author Samuel
 *
 */
public class APIResponse {
	
	public Representation  Repr = null;
	public String MediaType = MediaTypes.APPLICATION_XML;
	
	protected Map<String, String> headers = null;
	
	public Map<String, String> getHeaders() {
		if (null == headers)
			headers = new HashMap<String, String>();
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
}
