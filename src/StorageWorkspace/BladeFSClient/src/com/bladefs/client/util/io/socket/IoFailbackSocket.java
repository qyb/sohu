package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

import com.bladefs.client.util.io.IoSocket;

/**
 * 从array的第一个测试，如果失败则依次重试array后面的client<br>
 * 重试次数由tries决定，超过ttbInMillis时间重新回到第一个<br>
 * 
 * 参数：<br>
 * ttbInMillis 表示back时间, 如果无穷大，则退化为failover<br>
 * tries == 0 表示不重试<br>
 * tries > 0 表示重试次数，不包括第一次请求<br>
 * tries < 0 表示重试一圈<br>
 *
 */
public class IoFailbackSocket implements IoSocket {

	private final IoSocket[] clients;
	private final int tries;
	private final long ttbInMillis;
	private final AtomicInteger currClient = new AtomicInteger();
	
	private volatile long lastErrorTimeInMillis = 0;
	
	public IoFailbackSocket(IoSocket[] clients, long ttbInMillis, int tries) {
		this.clients = clients;
		this.ttbInMillis = ttbInMillis;
		this.tries = (tries < 0 ? clients.length-1 : tries);
	}

	@Override
	public Socket checkOut(int timeout, boolean alive) throws IOException {
		
		long let = this.lastErrorTimeInMillis;
		int index = Math.abs(currClient.get()) % clients.length;
		
		// 时间到，恢复到第一个
		if(index != 0 && System.currentTimeMillis() >= let + ttbInMillis) {
			currClient.set(0);
		}
		
		int tries = this.tries;
		Socket result = null;
		for(;result == null && tries >= 0; --tries){
			index = Math.abs(currClient.get()) % clients.length;
			result = clients[index].checkOut(timeout, alive);
			if(result == null) {
				currClient.compareAndSet(index, index+1);
				if (index == 0) this.lastErrorTimeInMillis = System.currentTimeMillis();
			}
		};
		return result;
	}

}
