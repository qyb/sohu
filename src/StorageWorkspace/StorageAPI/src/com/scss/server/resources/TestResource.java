/**
 * 
 */
package com.scss.server.resources;

import org.apache.log4j.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.scss.server.apps.TestApplication;

/**
 * @author Leon
 *
 */
public class TestResource extends ServerResource {

	private Logger logger = Logger.getLogger(TestApplication.class);
    
    public TestResource() {
        super();
    }
  
    @Get  
    public String toString() {   
        return "Account of user \"" + "\"";   
    }   
    
    
}
