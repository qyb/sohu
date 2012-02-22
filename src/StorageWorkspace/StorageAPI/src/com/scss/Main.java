/*
 * Main entrance.
 */
package com.scss;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import com.scss.core.Handler;

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
