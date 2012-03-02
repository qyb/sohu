/*
 * Main entrance.
 */
package com.scss;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.restlet.Server;
import org.restlet.data.Protocol;

import com.scss.core.Handler;
import com.scss.server.CloudServer;

/*
 * @author Samuel
 */
public class Main {
	
	private static final Logger logger = Logger.getRootLogger();

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
		 
		 if (args.length >= 1) {
			 if (args[0].equals("serve"))
				 CloudServer.shared().start(null, null);
			 else if (Integer.parseInt(args[0]) > 0)
				 new Server(Protocol.HTTP, Integer.parseInt(args[0]), Handler.class).start();
		 } 
		 else {
			 DOMConfigurator.configureAndWatch("./log4j.xml");
			 logger.info("Starting DEBUG server...");
			 new Server(Protocol.HTTP, 80, Handler.class).start();
		 }
	}

}
