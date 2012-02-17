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
import com.bladefs.client.util.io.IoSocket;
import com.bladefs.client.util.io.socket.AddressFactory;

public class TCPShortClient implements IoClient {
	
	private static Logger log = null;
	private final IoSocket client;
	private final String addr;
	private final int timeout;
	private final int tries;                    //connCount
	
	public TCPShortClient(String ip, int port, int timeout, int tries) throws UnknownHostException {
		this(ip, port, timeout, tries, "net");
	}
	public TCPShortClient(String ip, int port, int timeout, int tries, String logName) {
		this.addr = ip+":"+port;
		this.client = AddressFactory.getIoSocket(ip, port);
		this.timeout = timeout;
		this.tries = tries;

		if(logName != null && !logName.isEmpty())
			log = Logger.getLogger(logName);
		else
			log = Logger.getLogger(TCPShortClient.class);
	}
	
	@Override
	public void close() {}
	
	@Override
	public boolean serve(IoCmd cmd) throws IOException {
		
		Socket sock = null;
		int tryNum = 0;
		
		/*
		 * 如果tries >= 0，那么如果写失败要进行tries+1次尝试，如果写成功只一次
		 * 如果tries < 0，那么如果写失败要进行无限次尝试，如果写成功只一次
		 * */
		do {
			sock = client.checkOut(timeout, false);
			if(sock == null) continue;
			
			try {
				DataInputStream in = new DataInputStream( sock.getInputStream() ); 
				DataOutputStream out = new DataOutputStream( new BufferedOutputStream(sock.getOutputStream()) ); 
				
				long s = System.currentTimeMillis();
				cmd.process(new IoSession(in, out, sock.getInetAddress(), sock.getPort(), sock.getLocalAddress(), sock.getLocalPort()));
				long e = System.currentTimeMillis();
				
				if(log.isInfoEnabled()) log.info("[TCPShortClient:serve] " + cmd.toString() + ", time=" + (e-s));

				if(e-s > 100){
					log.warn(new StringBuffer("[TCPShortClient<").append(addr).append(">:serve] ").append(cmd.toString()).append(", time=").append(e-s).toString());
				}
				
				return true;
				
			} catch (IOException e) {
				if(log.isEnabledFor(Level.ERROR))
					log.error(new StringBuffer("[TCPShortClient<").append(addr).append(">:serve] ").append(cmd.toString()).append(", reason=unknown net error").toString(), e);
			} catch (Throwable e) {
				if(log.isEnabledFor(Level.ERROR))
					log.error(new StringBuffer("[TCPShortClient<").append(addr).append(">:serve] ").append(cmd.toString()).append(", reason=timeout or disconnected").toString(), e);
			} finally {
				try {
					if(sock != null) sock.close();
				} catch (Throwable e) {
					log.warn(new StringBuffer("[TCPShortClient<").append(addr).append(">:serve] reason=close connection error").toString(), e);
				}
			}			
		} while( tries<0 || tryNum++<tries); //先比较后++
		
		return false;
	}
}

