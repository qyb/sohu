package com.bladefs.client.util.io.imp;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import org.apache.log4j.*;

import com.bladefs.client.util.io.IoClient;
import com.bladefs.client.util.io.IoCmd;
import com.bladefs.client.util.io.IoSession;
import com.bladefs.client.util.io.socket.AddressFactory;


public class TCPClient implements IoClient {

	private final Logger log;
	
	private final String addr;
	private final SocketPool socks;
	public TCPClient(String ip, int port, int connectionNum, int timeout, int tries) throws UnknownHostException {
		this(ip, port, connectionNum, timeout, tries, "net");
	}
	
	public TCPClient(String ip, int port, int connectionNum, int timeout, int tries, String logName) throws UnknownHostException {
		this.addr = ip+":"+port;
		this.socks = new SocketPool(addr, AddressFactory.getIoSocket(ip, port), connectionNum, timeout);
		if(logName != null && !logName.isEmpty())
			log = Logger.getLogger(logName);
		else
			log = Logger.getLogger(TCPClient.class);
	}

	@Override
	public void close() {
		socks.close();
	}
	
	@Override
	public boolean serve(IoCmd cmd) throws IOException {
		
		boolean result = false;
		
		Socket sock = null;

		if(sock == null) sock = socks.checkOut();
		if(sock == null) return result;
		
		try {
			DataInputStream in = new DataInputStream( sock.getInputStream() );
			DataOutputStream out = new DataOutputStream( new BufferedOutputStream(sock.getOutputStream()) );
			
			long s = System.currentTimeMillis();
			cmd.process(new IoSession(in, out, sock.getInetAddress(), sock.getPort(), sock.getLocalAddress(), sock.getLocalPort()));
			long e = System.currentTimeMillis();
			
			if(log.isInfoEnabled()) log.info("[TCPClient:serve] " + cmd.toString() + ", time=" + (e-s));
			
			if(e-s > 100) log.warn(new StringBuffer("[TCPClient<").append(addr).append(">:serve] ").append(cmd.toString()).append(", time=").append(e-s).toString());
			
			result = true;
			
		} catch (Throwable e) {
			if(log.isEnabledFor(Level.ERROR))
				log.error(new StringBuffer("[TCPClient<").append(addr).append(">:serve] ").append(cmd.toString()).append(", reason=timeout or disconnected").toString(), e);	
			socks.destroy(sock);
			sock = socks.create();
		}

		if(sock != null) socks.checkIn(sock);
		
		return result;
	}
}
