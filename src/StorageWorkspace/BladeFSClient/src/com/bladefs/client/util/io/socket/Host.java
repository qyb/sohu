package com.bladefs.client.util.io.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.*;

public class Host {
protected static final Logger log  = Logger.getLogger(Host.class);
	
	private final String name;
	private final long ttbInMillis;
	private final ReentrantLock lock = new ReentrantLock();
	
	private long lastFlushTime = 0;
	private boolean flushed = true;
	private InetAddress[] addrs;
	
	public Host(String name, long ttbInMillis){
		this.name = name.trim();
		this.ttbInMillis = ttbInMillis;
	}
	
	public String getName(){ return name; }
	
	/**
	 * 
	 * @return
	 */
	public boolean tryFlush() {
		long flushTime = this.lastFlushTime;
		long ct = System.currentTimeMillis();
		if (ct >= flushTime + ttbInMillis){
			lock.lock();
			try {
				if (flushed){
					flushed = false;
					AddressFactory.flushHost(name);
					return true;
				}
			}finally{
				lock.unlock();
			}
		}
		return false;
	}
	
	public InetAddress[] getInetAddresses(){
		return this.addrs;
	}
	
	/**
	 * 
	 * @return true update ,false need not update
	 * @throws UnknownHostException
	 */
	public boolean flush(){
		this.flushed = true;
		try {
			InetAddress[] ads = InetAddress.getAllByName(name);
			if (ads == null || ads.length == 0 || isTheSame(ads)) return false;
			
			this.addrs = ads;
			this.lastFlushTime = System.currentTimeMillis();
			return true;
		} catch (UnknownHostException e) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[Host.flush] unknown host. name="+name, e);
		}
		return false;
		
	}
	
	private boolean isTheSame(InetAddress[] addrs2) {
		InetAddress[] addrs1 = this.addrs;
		if (addrs1 == null || addrs2.length != addrs1.length) return false;
		for(int i=0;i<addrs2.length;++i){
			if (!addrs2[i].equals(addrs1[i]))return false;
		}
		return true;
	}

	@Override
	public String toString(){
		return new StringBuilder("[Host] name=").append(name).append(", ttbInMillis=").append(ttbInMillis)
			.append(", lastFlushTime=").append(lastFlushTime).append(", addrs=").append(Arrays.toString(addrs)).toString();
	}
}
