package com.bladefs.client.multithreads;

import java.net.UnknownHostException;

import com.bladefs.client.BladeFSClient;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;

public class ChildThread extends Thread {
	private byte[] data = null;
	private int len = -1;
	private int flag = -1;
	private BladeFSClient client = null;
	public ChildThread(BladeFSClient cc, byte[] info, int length, int flag){
		this.client = cc;
		this.data = info;
		this.len = length;
		this.flag = flag;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		System.out.println("Thread" + flag + " Data to send :"+data.length+" bytes");
		long t1 = System.currentTimeMillis();
//		System.out.println("Thread" + flag + "  begin: "+t1);		
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
//		System.out.println("Thread" + flag + "  end: "+t2);
//		System.out.println("Thread" + flag + "  last: "+(t2-t1));
		if(fn > 0){
			System.out.println("--------Thread" + flag + "  filename returned: "+fn+"\n");
		}
		else
			System.out.println("--------Thread" + flag + "  failed ");
	}
}