/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server;

import java.util.HashMap;
import java.util.Map;

import org.restlet.representation.Representation;

/**
 * @author Samuel
 *
 */
public abstract class APIResponse {
	public Map<String, String> Headers = new HashMap<String, String>();
	public Representation  Repr = null;
	public String MediaType = "application/xml";
}
