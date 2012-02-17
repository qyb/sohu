package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.log4j.*;

import com.bladefs.client.util.io.IoSocket;

public class IoSimpleSocket implements IoSocket {

	private final static Logger log = Logger.getLogger("net");

	private final InetSocketAddress svrAddr;
	public IoSimpleSocket(InetSocketAddress svrAddr) {
		this.svrAddr = svrAddr;
	}

	@Override
	public Socket checkOut(int timeout, boolean alive) throws IOException {
		Socket socket = new Socket();
		socket.connect(svrAddr, timeout);
		socket.setSoTimeout(timeout);
		socket.setKeepAlive(alive);
		return socket;
//		try {
//			
//		} catch (Throwable e) {
//			if(log.isEnabledFor(Level.ERROR))
//				log.error(new StringBuilder("[IoSimpleSocket:checkOut] svrAddr=").append(svrAddr)
//					.append(", reason=connection refused").toString(), e);
//		}
//			return null;
	}

}