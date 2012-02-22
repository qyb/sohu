/**
 * 
 */
package com.scss.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leon
 *
 */
public class HostBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7263827090575806296L;
    
	private String id;

    private String domainName;
    
    private String port = "80";
    
    private List<ApplicationBean> apps = new ArrayList<ApplicationBean>();
    
	public String getDomainName() {
		return domainName;
	}
    
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
    
	public String getPort() {
		return port;
	}
    
	public void setPort(String port) {
		this.port = port;
	}
    
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
        sb.append("domain: " + this.domainName);
        sb.append("  port: " + this.port);
        
        sb.append("\n");
        
        for (ApplicationBean b : apps) {
            sb.append(b.toString());
            sb.append("\n");
        }
		
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<ApplicationBean> getApps() {
		return apps;
	}

	public void setApps(List<ApplicationBean> apps) {
		this.apps = apps;
	}
    
}
