/**
 * 
 */
package com.scss.server;

import java.lang.reflect.Constructor;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.VirtualHost;

/**
 * @author Leon
 * 
 * @desc: CloudServer provide interface to manage server cycle
 *
 */
public class CloudServer {
    
	private static CloudServer instance = null;
	
    public static CloudServer shared() {
    	if (null == instance) {
    		instance = new CloudServer();
    		if (null == instance) {
    			logger.error("Construct CloudServer Error!!!");
    			Common.UseOut(null);
    		}
    	}
    	return instance;
    }

	public CloudServer() {
       	PropertyConfigurator.configure(logName);
	}
    
	private static Logger logger = Logger.getLogger(CloudServer.class);
    
    private String configName = "config.xml";
    private String logName = "./log4j.ini";
    
	private ConfigBean config = null;
    private Component component = null;
    
    public ConfigBean getConfigBean() {
    	return config;
    }
    
    public Component getComponent() {
    	return component;
    }
    
    /*
     * Read Configuration to ConfigBean
     * 
     */
	protected boolean readConfig() {
        ConfigureParser cp = ConfigureParser.shared();
        if (null == cp) {
            logger.error(String.format("ConfigureParser instantiate failed."));
            return false;
        }
        
        config = cp.parseConfig(configName);
        if (null == config) {
            logger.error(String.format("ConfigureParser parse failed."));
            return false;
        }
       
        logger.debug("=================Reloading Config==============================");
        logger.debug(config.toString());
		
        return true;
	}
	
    
	protected boolean buildRestlet() {
		try {
			component = new Component();
			component.getServers().add(Protocol.HTTP, 8181);
            
            VirtualBean vb = config.getVirtual();
            for (HostBean bean : vb.getHosts().values()) {
                VirtualHost host = new VirtualHost(component.getContext());
                host.setHostDomain(bean.getDomainName());
                host.setHostPort(bean.getPort());
                for (ApplicationBean app : bean.getApps()) {
                    Class<?> specifyClass = Class.forName(app.getApplicationClass());
                    
                    Restlet instance = null;
                    try {
                        @SuppressWarnings("rawtypes")
						Class[] argsClass = new Class[1];
                        argsClass[0] = Context.class;
                        
                        Constructor<?> cons = specifyClass.getConstructor(argsClass);
                        instance = (Restlet) cons.newInstance(component.getContext());
                    } catch (Exception e) {
                        logger.error(String.format("Construct Class [%s] failed. Look up Package for Reason.", app.getApplicationClass()));
                        return false;
                    }
                    
                    if (null == instance) {
                        logger.error(String.format("Construct Class [%s] failed. Look up Package for Reason.", app.getApplicationClass()));
                    	return false;
                    }
                    
                	host.attach(app.getUriPatter(), instance);
                }
                
            	component.getHosts().add(host);
            }
            
            printComponent();
            component.start();
        } catch (Exception e) {
            logger.error(String.format("buildRestlet faield, Exception: %s", e.getMessage()));
        	return false;
        }
        
        return true;
	}
    
	protected void printComponent() {
        List<VirtualHost> hosts = component.getHosts();
            
        for (int i = 0; i < hosts.size(); i++) {
        	VirtualHost vh = hosts.get(i);
            logger.debug(String.format("vh: %s---%s---%s", vh.getHostDomain(), vh.getHostPort(), vh.getHostScheme()));
        }
            
        logger.debug(String.format("vh: %s---%s---%s", component.getDefaultHost().getHostDomain(), 
		component.getDefaultHost().getHostPort(), 
		component.getDefaultHost().getHostScheme()));
	}
    
    /* �������   */
	public boolean start(String __logFileName, String __configName) {
		if (null != __logFileName) 
			this.logName = __logFileName;
		if (null != __configName) 
			this.configName = __configName;
		
        if (null != component) {
        	if (component.isStarted()) {
                shutdown();
        	}
        }
		
		if (false == readConfig()) {
			return false;
		}
        
        logger.debug("========= config load successful ==========");
        
		if (false == buildRestlet()) {
            logger.error("!!!!! start Sohu Cloud Storage Server failed !!!!!");
			return false;
		}
		
        logger.debug("!!!!! start Sohu Cloud Storakge Server successful !!!!!");
        
        return true;
	}
    
    /* �رշ����� */
	public void shutdown() {
        if (null != component || !component.isStopped()) {
        	try {
				component.stop();
			} catch (Exception e) {
                logger.fatal(String.format("stop Restlet Component faield: %s", e.getMessage()));
			}
        } else {
			logger.fatal("Server Not Started!");
        }
	}
    
    /**
     * 
     * @param flag
     * 
     * true    load config.xml to flush allowFileName/forbidFileName.
     * false   Only flush specified filenames to memory.  
     * 
     */
	public void reloadAccess(boolean flag) {
		if (null == component || component.isStopped() || null == config) {
			logger.fatal("Start the Cloud Server first, when you want to reload access list!");
            return;
		}
		
        
        config.getAccess().reload();
	}
    
	
	public boolean reloadConfig() {
        return ConfigureParser.shared().reload(config);
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
}













