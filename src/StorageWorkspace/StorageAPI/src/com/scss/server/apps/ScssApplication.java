/**
 * 
 */
package com.scss.server.apps;

import org.restlet.Application;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import com.scss.server.resources.ScssResource;

/**
 * @author Leon
 *
 */
public class ScssApplication extends Application {
    
	public ScssApplication(Context context__) {
    }
    
	public synchronized Restlet createRoot()  {
    	Router router = new Router(getContext());
        router.attachDefault(ScssResource.class);
        return router;
	}

}
