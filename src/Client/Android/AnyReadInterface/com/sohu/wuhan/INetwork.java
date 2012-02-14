/**
 * 
 * @desc: INetwork provides network relative interface Only
 * 
 * 
 * 
 */
package com.sohu.wuhan;

import java.net.Proxy;

/**
 * @author Leon
 *
 */
public interface INetwork {
	
	boolean setDomain(String domain);
	
	String getDomain();	
	
	public void setProxy(Proxy __proxy);
	
	public void delProxy(); 
	
	public boolean isProxy();
	
	public String getEncoding();
	
	public void setEncoding(String __encoding);

}
