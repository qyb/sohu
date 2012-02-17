package com.bladefs.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBladeFSClientSimple {
	private int len = 0;
	StringBuffer sb = new StringBuffer();
	byte[] info = null;
	byte[] data = null;

	@Before
	public void setUp() throws Exception {
		PropertyConfigurator.configure(".\\conf\\log4j.properties");

		File file = new File("100k.jpg");
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[1024]; // buff用于存放循环读取的临时数据
		int rc = 0;
		while ((rc = in.read(buff, 0, 1024)) > 0) {
			swapStream.write(buff, 0, rc);
			len += rc;
		}

		data = swapStream.toByteArray(); // in_b为转换之后的结果

		// File file = new File("1.jpg");
		// FileInputStream in = new FileInputStream(file);
		// byte[] buf = new byte[1024*8];
		// int tmplen = 0;
		// System.out.println("!!!!!sb len:"+sb.toString().length());
		// while ((tmplen = (in.read(buf))) > 0) {
		// System.out.println("tmplen:"+tmplen);
		// len += tmplen;
		// if (tmplen == 1024*8)
		// sb.append(new String(buf,"utf-8"));
		// else {
		// byte[] tmpbuf = new byte[tmplen];
		// System.arraycopy(buf, 0, tmpbuf, 0, tmplen);
		// sb.append(new String(tmpbuf,"utf-8"));
		// break;
		// }
		// }
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
//		byte[] tmpdata = new byte[2];
//		tmpdata[0] = 0;
//		tmpdata[1] = 1;
		BladeFSClient client = new BladeFSClient(".\\conf\\client.properties");
//		System.out.println("Data to send: " + tmpdata.length + " bytes");
//		long t1 = System.currentTimeMillis();
//		System.out.println("begin: " + t1);
//		long fn = client.write(data, len);
		
//		int sucount = 0;
//		for(int i=0;i<10000;i++){
//			long fn = client.write("100k.jpg", data, len);
		long fn = 64424519849L;
//		byte[] retr = client.read("100k.jpg");
//		boolean ret = client.delete("100k.jpg");
		boolean ret = client.recover(fn);
		if (ret) 
//		if(retr!=null)
				System.out.println("read done!");
//		}
//		System.out.println("filename count: " +sucount+"\n");
			
//		long t2 = System.currentTimeMillis();
//		System.out.println("end: " + t2);
//		System.out.println("last: " + (t2 - t1));
		
//		
//		System.out.println("before read--1: "+System.currentTimeMillis());
//		info = client.read(fn);
//		System.out.println("after read--1: " + System.currentTimeMillis());
//		FileOutputStream fs = new FileOutputStream("read2.jpg");
//		if (info != null) {
//			fs.write(info, 0, info.length);
//			System.out.println("Data received: " + info.length + "\n");
//		}
//		fs.close();

//		System.out.println("before delete: " + System.currentTimeMillis());
//		long fn = 4294967423L;
//		long fn = 4294977352L;
//		byte[] retr = client.read(fn);
//			boolean ret = client.delete(fn);
//		if (ret) {
//		if(retr!=null){
//			System.out.println("after delete: " + System.currentTimeMillis());
//			System.out.println("delete done!");
//			
//			System.out.println("before read--2: " + System.currentTimeMillis());
//			byte[] readres = client.read(fn) ;
//			if(readres == null){
//				System.out.println("after read--2: " + System.currentTimeMillis());
//				System.out.println("delete success!");
//				ret = client.recover(fn);
//				readres = client.read(fn) ;
//				if(readres!=null)
//					System.out.println("recover success"+"length:"+readres.length);
//			} else {
//				System.out.println("delete failed!");
//				info = client.read(fn);
//				System.out.println("Data received: " + info.length + "\n");
//			}
//		} else{
//			System.out.println("after delete: " + System.currentTimeMillis());
//			System.out.println("delete failed!");
//		}
	}
}
