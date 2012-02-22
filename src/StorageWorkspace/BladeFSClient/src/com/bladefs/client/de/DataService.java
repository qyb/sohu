package com.bladefs.client.de;

public class DataService {
	private String host = null; // 节点主机 IP 地址
	private int port = -1; // 节点主机上服务的端口
	private boolean master = false; // 是否为 Master 节点
	
	public DataService(String host, int port, boolean master) {
		super();
		this.host = host;
		this.port = port;
		this.master = master;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isMaster() {
		return master;
	}
	public void setMaster(boolean master) {
		this.master = master;
	}
	@Override
	public String toString() {
		return "DataService [host=" + host + ", port=" + port + ", master="
				+ master + "]";
	}
}
