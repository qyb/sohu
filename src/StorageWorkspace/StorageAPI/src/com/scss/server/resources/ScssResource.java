/**
 * 
 * 
 * 
 * 
 */
package com.scss.server.resources;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author Leon
 *
 */
public class ScssResource extends ServerResource {
	
//	@Override
//	protected Representation get() throws ResourceException {
//		return new StringRepresentation("Scss get ..");
//	}
	
	@Get
	public String toString() {   
	 return  "ScssResource presentation.";
	} 
}








