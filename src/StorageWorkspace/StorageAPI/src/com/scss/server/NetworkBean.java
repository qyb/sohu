/**
 * 
 */
package com.scss.server;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author Leon
 * 
 * @version simple one, need implement some host name not only IP dot string
 *          format
 */
public class NetworkBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4881848728928938087L;
    
	public static final String IP = "127.0.0.1";
	public static final short PORT = 80;
    public static final int VERSION = Constant.IPV4;

	private String ip;

	private short port;
    
	private int version;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}
    
	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public NetworkBean() {
		ip = IP;
		port = PORT;
	}

	public NetworkBean(int __ip, short __port) {
		ip = ipv4N2A(__ip);
		port = __port;
	}
    
	public NetworkBean(String __ip, short __port) {
        InetAddress addr = null;        
        try {
			addr = InetAddress.getByName(__ip);
    		ip = addr.getHostAddress();
		} catch (UnknownHostException e) {
            
		}
		port = __port;
	}

	public String toString() {
		return "ip:" + ip + "   port:" + Integer.toString(port) + " version: " + getVersion();
	}

    /*
     * Pay attention here, IP should be a big endien
     * 
     */
	public static String ipv4N2A(int ip) {
		StringBuffer sb = new StringBuffer();

		sb.append(String.valueOf(ip >>> 24));
		sb.append(".");
		sb.append(String.valueOf((ip & 0x00ffffff) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((ip & 0x0000ffff) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((ip & 0x000000ff)));

		return sb.toString();
	}
}

