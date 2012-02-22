package com.scss.server;


import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * @author Leon
 * 
 * 
 * Threading Safe Caution: do not and no need gurentee it.
 *
 */
public class ConfigureParser {
    private static Logger logger = Logger.getLogger(ConfigureParser.class);
    private static ConfigureParser instance = new ConfigureParser();
    
    private String configName = "config.xml";
    private XMLConfiguration xml = new XMLConfiguration();
    
    protected ConfigureParser() {
        logger.info("Construct ConfigureParser successfully.");
    }
    
    public static ConfigureParser shared() {
    	return instance;
    }
    
    public ConfigBean parseConfig(String __filename) {
        ConfigBean rtn = null;
        
        if (null != __filename && "" != __filename) { 
        	configName = __filename;
        }
            
        try {
            xml.load(configName);
        } catch (Exception e) {
            logger.error(String.format("Configuration load failed: %s", e.getMessage()));
            return null;       	
        }
        
        rtn = new ConfigBean();
        if (false == rtn.Parse(xml)) {
        	return null;
        }
        
        return rtn;
    }
}



