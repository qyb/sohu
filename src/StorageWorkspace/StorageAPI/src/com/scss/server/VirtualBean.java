/**
 * 
 */
package com.scss.server;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Leon
 *
 */
public class VirtualBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6139823472899136116L;

	private HashMap<String, HostBean> hosts;

	public HashMap<String, HostBean> getHosts() {
		return hosts;
	}

	public void setHosts(HashMap<String, HostBean> hosts) {
		this.hosts = hosts;
	}
	
	public String toString() {
	    StringBuilder sb = new StringBuilder();
    	for (Object id : getHosts().keySet().toArray()) {
    		sb.append(hosts.get(id).toString());
        }
        
        return sb.toString(); 	
	}
}
