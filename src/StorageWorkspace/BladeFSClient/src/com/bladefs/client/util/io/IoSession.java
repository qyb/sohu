package com.bladefs.client.util.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;

public class IoSession {
	private final DataInputStream in;
	private final DataOutputStream out;
	
	private final InetAddress peerAddr;
	private final int peerPort;
	private final InetAddress localAddr;
	private final int localPort;
	
	public IoSession(DataInputStream in, DataOutputStream out, InetAddress peerAddr, int peerPort, InetAddress localAddr, int localPort) {
		this.in = in;
		this.out = out;
		this.peerAddr = peerAddr;
		this.peerPort = peerPort;
		this.localAddr = localAddr;
		this.localPort = localPort;
	}
	
	public DataInputStream getInputStream() { return in; }
	public DataOutputStream getOutputStream() { return out; }
 	public InetAddress getPeerAddr() { return peerAddr; }
 	public int getPeerPort() { return peerPort; }
 	public InetAddress getLocalAddr() { return localAddr; }
 	public int getLocalPort() { return localPort; }
}
