/*
 * Main entrance.
 */
package com.bfsapi;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import com.bfsapi.server.Handler;

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
		 
		 new Server(Protocol.HTTP, 80, Handler.class).start();
	}

}
