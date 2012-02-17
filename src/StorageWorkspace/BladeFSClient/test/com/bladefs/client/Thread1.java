package com.bladefs.client;

import java.net.UnknownHostException;

import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;

public class Thread1 extends Thread {
	private byte[] data = null;
	private int len = 0;
	private BladeFSClient client = null;
	public Thread1(BladeFSClient cc, byte[] info, int length){
		client = cc;
		data = info;
		len = length;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Thread1 Data to send :"+data.length+" bytes");
		long t1 = System.currentTimeMillis();
		System.out.println("Thread1 begin: "+t1);		
		long fn = -1;
		try {
			fn = client.write(data, len);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BladeFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameServiceException e) {
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis();
		System.out.println("Thread1 end: "+t2);
		System.out.println("Thread1 last: "+(t2-t1));
		if(fn > 0){
			System.out.println("Thread1 filename returned: "+fn+"\n");
		}
	}
}