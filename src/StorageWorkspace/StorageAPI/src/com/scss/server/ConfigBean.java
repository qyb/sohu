/**
 * 
 */
package com.scss.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * @author Leon
 *
 */
public class ConfigBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1744461728629770755L;
    private static Logger logger = Logger.getLogger(ConfigBean.class);
    
	private NetworkBean    network     = null;
    private VirtualBean    virtual     = null;
    private AccessBean      access     = null;
    private XMLConfiguration config	   = null;
    
    public NetworkBean getNetwork() { 
    	return network; 
    }
    
    public VirtualBean getVirtual() {
    	return virtual;
    }
    
    public AccessBean getAccess() {
    	return access;
    }
    
    public XMLConfiguration getXMLConfig() {
    	return config;
    }
    
    public boolean Parse(XMLConfiguration __config) {
    	if (null == __config) {
            Common.error("Config Bean parse Error: for config is null");
    		return false;
    	}
    	
    	config = __config;
        
        if (null == parseNetwork()) {
        	return false;
        }
        
        if (null == parseVirtual()) {
        	return false;
        }
        
        if (false == parseComponent()) {
        	return false;
        }
        
        if (false == parseList()) {
        	return false;
        }
    	
    	return true;
    }
    
    protected NetworkBean parseNetwork() {
        network = new NetworkBean();
        if (null == network) {
        	Common.UseOut(null);
        	return null;
        }
        
        try {
            String ip = config.getString("network.ip");
            short port = config.getShort("network.port");
            
            network.setIp(ip);
            network.setPort(port);
        } catch (Exception e) {
        	Common.error("parseNetwork Error For: %s", e.getMessage());
            return null;
        }
    	
        return network;
    }
    
    protected boolean parseComponent() {
        if (null == this.virtual) {
            Common.error("Should successfully parseVirtual first");
            return false;
        }
        
        try {
            List<HierarchicalConfiguration> hcs = config.configurationsAt("components.application");
            
            for (HierarchicalConfiguration hc : hcs) {
                ApplicationBean bean = new ApplicationBean();
                
                bean.setUriPatter(hc.getString("uri"));
                bean.setApplicationClass(hc.getString("class"));
                bean.setResource(hc.getString("resource"));
                
                String id = hc.getString("vid");
                HostBean host = this.virtual.getHosts().get(id);
                if (host != null) {
                	host.getApps().add(bean);
                } else {
                	Common.trace("parseComponent: VirtualHost(id=%s) could not find!", id);
                }
            }
        } catch (Exception e) {
        	Common.error("parseComponent Error For: %s", e.getMessage());
            return false;
        }
    	
        return true;
    }
    
    protected VirtualBean parseVirtual() {
   		virtual = new VirtualBean();
        if (null == virtual) {
        	Common.UseOut(null);
        	return null;
        }
        
        virtual.setHosts(new HashMap<String, HostBean>());
        
        try {
            List<HierarchicalConfiguration> hcs = config.configurationsAt("virtuals.host");
                
            for (HierarchicalConfiguration hc : hcs) {
                HostBean bean = new HostBean();
                bean.setId(hc.getString("id"));
                bean.setDomainName(hc.getString("domain"));
                bean.setPort(hc.getString("port"));
                    
                virtual.getHosts().put(bean.getId(), bean);
            }
        } catch (Exception e) {
        	Common.error("parseVirtual Error For: %s", e.getMessage());
            return null;
        }
    	
        return virtual;
    }
    
    protected boolean parseList() {
    	if (null == access)
    		access = new AccessBean();
        if (null == access) {
        	Common.UseOut(null);
        	return false;
        }
        
        try {
            String allow = config.getString("access.allow");
            String forbid= config.getString("access.forbid");
            
            int order = config.getInt("access.order");
            
            access.setAllowFileName(allow);
            access.setForbidFileName(forbid);
            access.setOrder(order);
            logger.debug(String.format("=============== Access Order: %d ============== [%s]", order, access.toString()));
            access.reload();
        } catch (Exception e) {
        	logger.error(String.format("parseNetwork Error For: %s", e.getMessage()));
            return false;
        }
    	
        return true;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
    	if (null != network) {
            sb.append("Network Bean: ");
    		sb.append(network.toString());
            sb.append("\n");
    	}
        
    	if (null != virtual) {
    		sb.append(virtual.toString());
            sb.append("\n");
    	}
        
    	return sb.toString();
    }
}


