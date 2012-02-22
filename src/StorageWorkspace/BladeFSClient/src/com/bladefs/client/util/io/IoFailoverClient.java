package com.bladefs.client.util.io;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class IoFailoverClient  implements IoClient {
	
	private final IoClient[] clients;
	private final AtomicInteger currClient = new AtomicInteger();
	
	public IoFailoverClient(IoClient[] clients) {
		this.clients = clients;
	}
	
	@Override
	public void close() {
		for(IoClient client : clients) client.close();
	}
	
	@Override
	public boolean serve(IoCmd cmd) throws IOException {
		boolean success = clients[Math.abs(currClient.get() % clients.length)].serve(cmd);
		if(!success) currClient.incrementAndGet();
		return success;
	}
}