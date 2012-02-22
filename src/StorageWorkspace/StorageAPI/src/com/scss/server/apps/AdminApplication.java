/**
 * 
 */
package com.scss.server.apps;

import org.apache.log4j.Logger;
import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Router;

import com.scss.server.CloudServer;
import com.scss.server.Common;
import com.scss.server.filters.AccessFilter;

/**
 * @author leonzhou
 *
 */
public class AdminApplication extends Application {
    
	private static Logger logger = Logger.getLogger(AdminApplication.class);
    
    public AdminApplication(Context __context) {
    	super(__context.createChildContext());
    }
	
    @SuppressWarnings("unused")
	public synchronized Restlet createRoot() {
    	Router router = new Router(getContext());
        
    	/* 
    	 *  1.  show version to every body.
    	 */
    	router.attach("/show", new Restlet() {
    		public void handle(Request request, Response response) {
    			CloudServer.shared().shutdown();
    		}
    	});
        
    	/* 
    	 *  2.  develop history
    	 */
    	router.attach("/history", new Restlet() {
    		public void handle(Request request, Response response) {
    			CloudServer.shared().shutdown();
    		}
    	});
    	
    	/* 
    	 *  3.  stop server
    	 */
    	router.attach("/stop", new Restlet() {
    		public void handle(Request request, Response response) {
    			CloudServer.shared().shutdown();
    		}
    	});
        
    	/* 
    	 *  4.  restart server 
    	 */
    	router.attach("/restart", new Restlet() {
    		public void handle(Request request, Response response) {
    			CloudServer.shared().shutdown();
    			CloudServer.shared().start(null, null);
    		}
    	});
        
    	/* 
    	 *  5.  read config
    	 */
        
    	Restlet configRestlet = new Restlet() {
    		public void handle(Request request, Response response) {
    			String entity = "Method       : " + request.getMethod()
                        + "\nResource URI : " + request.getResourceRef()
                        + "\nIP address   : "
                        + request.getClientInfo().getAddress()
                        + "\nAgent name   : "
                        + request.getClientInfo().getAgentName()
                        + "\nAgent version: "
                        + request.getClientInfo().getAgentVersion();
    			
    			entity += String.format("\nPort: %d", request.getClientInfo().getPort());
                
                response.setEntity(entity, MediaType.TEXT_PLAIN);
    		}
    	};
        
    	AccessFilter access = new AccessFilter(CloudServer.shared().getConfigBean().getAccess());
        if (null == access) {
            logger.fatal("!!!!! Instantiate AccessFilter failed !!!!!");
            Common.UseOut(null);
        }
        
        access.setNext(configRestlet);
    	router.attach("/config", access);
        
    	return router;
    }
}

