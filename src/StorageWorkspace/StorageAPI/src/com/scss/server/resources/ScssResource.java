/**
 * 
 * 
 * 
 * 
 */
package com.scss.server.resources;

import org.apache.log4j.Logger;
import org.restlet.data.LocalReference;
import org.restlet.data.Reference;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author Leon
 *
 */
public class ScssResource extends ServerResource {
	private static Logger logger = Logger.getLogger(ScssResource.class);
	
//	@Override
//	protected Representation get() throws ResourceException {
//		return new StringRepresentation("Scss get ..");
//	}
	
	@Get
	public String toString() {   
		logger.debug("1." + new Reference(getReference(), "..").getTargetRef().toString());
		logger.debug("2." + LocalReference.createClapReference(getClass().getPackage()).toString());
		
		
		
		return  "ScssResource presentation.";
	} 
}








