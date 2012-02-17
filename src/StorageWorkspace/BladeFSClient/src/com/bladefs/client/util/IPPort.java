package com.bladefs.client.util;

public class IPPort{
	private String ip = null;
	private int port = 0;
	public IPPort(String ip, int port) {
		super();
		this.ip = ip;
		this.port = port;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public String toString() {
		return "IPPort [ip=" + ip + ", port=" + port + "]";
	}
	
	public static void main(String args[]){
		
	}
}
