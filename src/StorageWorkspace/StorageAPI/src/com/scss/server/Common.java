/**
 * 
 */
package com.scss.server;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * @author Le()n
 *
 * Wrapper class for future extends. :-)    Love you baby. God Bless my decorate will be greate than expectation!
 * 
 */
public class Common {
    
    static Logger logger = null;
    
    private static void log(Priority priority, String format, Object...args) {
    	if (null == logger) {
            Common.logger = Logger.getLogger("SCSS");
    		
            System.out.print(priority.toString());
    		System.out.printf(format, args);
            System.out.println();
    	} else {
            String message = String.format(format, args);
    		logger.log(priority, message);
    	}
    }
    
    @SuppressWarnings("deprecation")
	public static void debug(String format, Object...args) {
        log(Priority.DEBUG, format, args);
    }
	
    @SuppressWarnings("deprecation")
    public static void error(String format, Object...args) { 
    	log(Priority.ERROR, format, args);
    }
    
    @SuppressWarnings("deprecation")
    public static void trace(String format, Object...args) { 
    	log(Priority.INFO, format, args);
    }
    
    @SuppressWarnings("deprecation")
    public static void fatal(String format, Object...args) { 
    	log(Priority.FATAL, format, args);
    }
    
    public static void UseOut(String log)  {
        if (null == log)
        	log = "Memory use out, some other kind of solution should be deployed when i\'m not that busy.";
    	trace(log);
        System.exit(1);
    }
}

