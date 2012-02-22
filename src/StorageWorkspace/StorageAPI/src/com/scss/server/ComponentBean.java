/**
 * 
 */
package com.scss.server;

import java.io.Serializable;
import java.util.List;


/**
 * @author leonzhou
 *
 */
public class ComponentBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2532143325137491954L;
    
	private List<ApplicationBean> applications;
    
    private List<HostBean> hosts;
    
	public List<ApplicationBean> getApplications() {
		return applications;
	}
    
	public void setApplications(List<ApplicationBean> applications) {
		this.applications = applications;
	}
    
	public List<HostBean> getHosts() {
		return hosts;
	}
    
	public void setHosts(List<HostBean> hosts) {
		this.hosts = hosts;
	}
	
    public String toString() {
        StringBuilder sb = new StringBuilder();
    	for (ApplicationBean bean : getApplications()) {
            sb.append(bean.toString());
        }
        
        return sb.toString(); 
    }
	
}
