package com.bladefs.client.util.io;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class IoRoundRobinClient implements IoClient {
	
	private final IoClient[] clients;
	private final AtomicInteger currClient = new AtomicInteger();
	
	public IoRoundRobinClient(IoClient[] clients) {
		this.clients = clients;
	}
	
	@Override
	public void close() {
		for(IoClient client : clients) client.close();
	}
	
	@Override
	public boolean serve(IoCmd cmd) throws IOException {
		return clients[Math.abs(currClient.getAndIncrement() % clients.length)].serve(cmd);
	}
}
