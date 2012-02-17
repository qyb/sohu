package com.bladefs.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.PropertyConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBladeFSClient {
	private int len = 0;
	StringBuffer sb = new StringBuffer();
	byte[] info = null;
	byte[] data = null;
	@Before
	public void setUp() throws Exception {
		PropertyConfigurator.configure(".\\conf\\log4j.properties");
		
		File file = new File("src.jpg");
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
		byte[] buff = new byte[1024]; //buff用于存放循环读取的临时数据 
		int rc = 0; 
		while ((rc = in.read(buff, 0, 1024)) > 0) { 
			swapStream.write(buff, 0, rc); 
			len += rc;
		} 
		
		data = swapStream.toByteArray(); //in_b为转换之后的结果 
		
		
//		File file = new File("1.jpg");
//		FileInputStream in = new FileInputStream(file);
//		byte[] buf = new byte[1024*8];
//		int tmplen = 0;
//		System.out.println("!!!!!sb len:"+sb.toString().length());
//		while ((tmplen = (in.read(buf))) > 0) {
//			System.out.println("tmplen:"+tmplen);
//			len += tmplen;
//			if (tmplen == 1024*8)
//				sb.append(new String(buf,"utf-8"));
//			else {
//				byte[] tmpbuf = new byte[tmplen];
//				System.arraycopy(buf, 0, tmpbuf, 0, tmplen);
//				sb.append(new String(tmpbuf,"utf-8"));
//				break;
//			}
//		}	
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void test() throws Exception {
		BladeFSClient client = new BladeFSClient(".\\conf\\client.properties");	
		System.out.println("Data to send: "+data.length+" bytes");
		long t1 = System.currentTimeMillis();
		System.out.println("begin: "+t1);		
		long fn = client.write(data, len);
		long t2 = System.currentTimeMillis();
		System.out.println("end: "+t2);
		System.out.println("last: "+(t2-t1));
		if(fn > 0){
			System.out.println("filename returned: "+fn+"\n");
		}
//		long fn = 8589934596L;
		System.out.println("before read: "+System.currentTimeMillis());
		info = client.read(fn);
		System.out.println("after read: "+System.currentTimeMillis());
		FileOutputStream fs = new FileOutputStream("read2.jpg");
		fs.write(info, 0, info.length);
		System.out.println("Data received: "+info.length+"\n");
		fs.close();
		}
	}

