package com.bladefs.client.util.io;

import java.io.IOException;

public class IoFailbacksClient implements IoClient {

	private final IoClient[] clients;
	
	public IoFailbacksClient(IoClient[] clients) {
		this.clients = clients;
	}

	@Override
	public void close() {
		for(int i = 0; i < clients.length; i++){
			clients[i].close();
		}
	}

	@Override
	public boolean serve(IoCmd cmd) throws IOException {		
		boolean result = false;
		for(int i = 0; i < clients.length; i++){
			if(clients[i].serve(cmd)){
				result = true;
				break;
			}
		}
		return result;
	}
}
