package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.Socket;

import com.bladefs.client.util.io.IoSocket;

public class IoDualSocket implements IoSocket {

	private final IoSocket[] clients;
	
	public IoDualSocket(IoSocket[] clients) {
		this.clients = clients;
	}

	@Override
	public Socket checkOut(int timeout, boolean alive) throws IOException {
		Socket result = null;
		for(int i=0;result == null && i<clients.length;++i){
			result = clients[i].checkOut(timeout, alive);
		}
		return result;
	}

}
