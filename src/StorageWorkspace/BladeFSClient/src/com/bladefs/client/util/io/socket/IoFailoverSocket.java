package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import com.bladefs.client.util.io.IoSocket;

/**
 * tries == 0 表示不重试<br>
 * tries > 0 表示重试次数，不包括第一次请求<br>
 * tries < 0 表示重试一圈<br>
 *
 */
public class IoFailoverSocket implements IoSocket {
	
	private final IoSocket[] clients;
	private final int tries;
	private final AtomicInteger currClient = new AtomicInteger();
	
	public IoFailoverSocket(IoSocket[] clients, int tries) {
		this.clients = clients;
		this.tries = (tries < 0 ? clients.length-1 : tries);
	}
	
	@Override
	public Socket checkOut(int timeout, boolean alive) throws IOException {
		int tries = this.tries;
		Socket result = null;
		for(;result == null && tries >= 0; --tries){
			int index = Math.abs(currClient.get()) % clients.length;
			result = clients[index].checkOut(timeout, alive);
			if(result == null) {
				currClient.compareAndSet(index, index+1);
			}
		};
		return result;
	}
}
