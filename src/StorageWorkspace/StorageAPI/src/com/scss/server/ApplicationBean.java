package com.scss.server;

import java.io.Serializable;

/*
 * @author Leon
 * 
 */
public class ApplicationBean implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -743320996636257047L;

	private String uriPatter;
    private String applicationClass;
    private String Resource;
    
	public String getUriPatter() {
		return uriPatter;
	}
    
	public void setUriPatter(String uriPatter) {
		this.uriPatter = uriPatter;
	}
    
	public String getResource() {
		return Resource;
	}
    
	public void setResource(String resource) {
		Resource = resource;
	}

	public String getApplicationClass() {
		return applicationClass;
	}

	public void setApplicationClass(String applicationClass) {
		this.applicationClass = applicationClass;
	}
    
	public String toString() {
        StringBuilder sb = new StringBuilder();
		
  		sb.append("Application: uri-" + getUriPatter() + " class-" + getApplicationClass() + 
    				" resource-" + getResource());
        return sb.toString();
	}
}
