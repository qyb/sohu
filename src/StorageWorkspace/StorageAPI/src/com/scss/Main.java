/*
 * Main entrance.
 */
package com.scss;

import java.util.HashSet;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jetty.HttpServerHelper;
import org.restlet.ext.jetty.JettyServerHelper;
import org.restlet.routing.Router;

import com.scss.config.ConfigManager;
import com.scss.config.GeneralConfig;
import com.scss.core.Handler;
import com.scss.server.CloudServer;

/*
 * @author Samuel
 */
public class Main{
	
	private static final Logger logger = Logger.getRootLogger();

	/**
	 * @param args
	 * 		1. -P## (## is port number. e.g. 8080)
	 * 		2. -D
	 * 		3. -S
	 * @author Samuel
	 * @throws Exception 
	 * 
	 */
	public static void main(String[] args)  throws Exception {
		
		int port = 80;
		
		HashSet<String> params = new HashSet<String>();
		for(String arg: args) {
			String a = arg.toUpperCase();
			if (a.startsWith("-P"))
				port = Integer.parseInt(a.substring(2));
			params.add(a);
		}
		
		Boolean serving = params.contains("-S");
		Boolean debug = params.contains("-D");
		String prefix = debug ? "debug." : "";
		
					
		ConfigManager cm = ConfigManager.getInstance();
		cm.setPrefix(prefix);
		cm.register(GeneralConfig.class);
		
		 Component component = new Component();
		 component.getDefaultHost().attachDefault(Handler.class);
		 
		 DOMConfigurator.configureAndWatch("log4j.xml");
		 
		 
		 if (serving) {
			 logger.info("Starting server...");
			 CloudServer.shared().start(null, null);
		 }
		 else if (debug) {
			 logger.info("Starting DEBUG server...");
//			 Server server = new Server(Protocol.HTTP, Integer.parseInt(args[0]), Handler.class);
//			 component.getServers().add(server);
//			 component.getDefaultHost().attachDefault(Handler.class)
//			 server.getContext().getParameters().add("useForwardedForHeader", "true");
//			 server.start();
			 ServeWithJetty(port);
		 }
		 else {
			 logger.info("Starting DEBUG server...");
			//new Server(Protocol.HTTP, 80, Handler.class).start();
			 ServeWithJetty(port);
		 }
	}
	
	protected static void ServeWithJetty(int port) {
		ServeWithJetty(port, false);
	}
	protected static void ServeWithJetty(int port, boolean in_secure) {
		Component component = new Component();
		component.getDefaultHost().attachDefault(Handler.class);		
        Application application=new Application(component.getContext()){
            @Override
            public Restlet createRoot(){
                Router router=new Router(getContext());
                router.attachDefault(Handler.class);
                return router;
            }
        };

        //create embedding jetty server
        Server embedingJettyServer=new Server(
                component.getContext(),
                in_secure ? Protocol.HTTPS : Protocol.HTTP,
                port,
                component
            );
        
        //construct and start JettyServerHelper
        JettyServerHelper jettyServerHelper=new HttpServerHelper(embedingJettyServer);
        try {
			jettyServerHelper.start();
		} catch (Exception e) {
			logger.error(e, e);
		}
	
	}

//	protected static void ServeWithJettyAjp(int port, boolean in_secure) {
//	 Component component=new Component();
//
//        Application application=new Application(component.getContext()){
//            @Override
//            public Restlet createRoot(){
////	                final String DIR_ROOT_URI="static_files/";
//
//                Router router=new Router(getContext());
////	                Directory dir=new Directory(getContext(),DIR_ROOT_URI);
////	                dir.setListingAllowed(true);
////	                dir.setDeeplyAccessible(true);
////	                dir.setNegotiateContent(true);
////	                router.attach("/www/",dir);
//                return router;
//            }
//        };
//
//        //create embedding AJP Server
//        Server embedingJettyAJPServer=new Server(
//                component.getContext(),
//                in_secure ? Protocol.HTTPS : Protocol.HTTP,
//                port,
//                component
//            );
//
//        //construct and start AjpServerHelper
//        AjpServerHelper ajpServerHelper=new AjpServerHelper(embedingJettyAJPServer);
//        try {
//			ajpServerHelper.start();
//		} catch (Exception e) {
//			logger.error(e, e);
//			//e.printStackTrace();
//		}		
//	}	

}
