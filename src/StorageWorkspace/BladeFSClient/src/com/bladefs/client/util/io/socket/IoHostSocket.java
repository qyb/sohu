package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.Socket;

import com.bladefs.client.util.io.IoSocket;


public class IoHostSocket implements IoSocketProxy {

	private final Host host;
	
	private IoSocket client;
	
	public IoHostSocket(Host host){
		this.host = host;
	}
	
	@Override
	public Socket checkOut(int timeout, boolean alive) throws IOException {
		Socket result = (client == null ? null : client.checkOut(timeout, alive));
		if (host != null) host.tryFlush();
		return result;
	}
	
	@Override
	public boolean setSocketClient(IoSocket client){
		if (client != null) this.client = client;
		return true;
	}

}

