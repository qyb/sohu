package com.bladefs.client.util.io.imp;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.*;

import com.bladefs.client.util.io.IoSocket;


public class SocketPool {
	private final static Logger log = Logger.getLogger(SocketPool.class);
	private final BlockingQueue<Socket> sockets = new LinkedBlockingQueue<Socket>();
	private final IoSocket client;
	private final String addr;
	private final int socketNum;
	private final int timeout;
	private final AtomicInteger socketToCreate = new AtomicInteger();
	
	public SocketPool(String addr, IoSocket client, int socketNum, int timeout)
	{
		this.addr = addr;
		this.client = client;
		this.socketNum = socketNum;
		this.timeout = timeout;

		if (this.socketNum > 0) 
			// Lazily create the sockets
			socketToCreate.addAndGet(this.socketNum);
	}
	
	public Socket create() throws IOException
	{
		Socket socket = client.checkOut(timeout, true);
		if (socket == null){
			socketToCreate.incrementAndGet();
		}
		return socket;
	}
	
	public Socket checkOut() throws IOException
	{
		Socket socket = null;
		
		try {
			if(socketToCreate.getAndDecrement() > 0) {
				socket = create();
				if(socket != null) return socket;
			} else {
				socketToCreate.incrementAndGet();//no create, to recover socketToCreate
			}
			socket = sockets.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			 log.warn("[SocketPool:checkOut] addr=" + addr + ", reason=interrupted", e);
		} catch (Throwable e) {
			 log.warn("[SocketPool:checkOut] addr=" + addr + ", reason=unknown", e);
		}		
		
		if((socket != null) && (!socket.isConnected() || socket.isClosed() || socket.isInputShutdown() || socket.isOutputShutdown()))
		{
			destroy(socket);
			socket = create();
		}
		
		return socket;
	}
	
	public void checkIn(Socket socket)
	{
		sockets.add(socket);
	}
	
	public void destroy(Socket socket)
	{
		try {
			socket.close();
		} catch (Exception e) {
			log.warn("[SocketPool:destroy] addr=" + addr + ", reason=close connection error", e);
		}
	}
	
	public void close()
	{
		Socket socket;
		while((socket = sockets.poll()) != null)
		{
			destroy(socket);
		}
	}
}
