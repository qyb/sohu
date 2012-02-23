/*
 * Main entrance.
 */
package com.scss;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import com.scss.core.Handler;
import com.scss.server.CloudServer;

/*
 * @author Samuel
 */
public class Main {

	/**
	 * @param args
	 * @author Samuel
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args) throws Exception {
//		 Component component = new Component();
//		 component.getServers().add(Protocol.HTTP, 8080);        
//		 component.getDefaultHost().attach("/api", Handler.class);
//		 component.start();
		 
		 if (args.length > 1 && args[1].equals("serve")) {
			 CloudServer.shared().start(null, null);
		 }
		 else
			 new Server(Protocol.HTTP, 80, Handler.class).start();
	}

}
