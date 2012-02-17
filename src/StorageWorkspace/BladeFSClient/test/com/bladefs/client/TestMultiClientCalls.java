package com.bladefs.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class TestMultiClientCalls {
	private static byte[] data = null;
	private static int len = 0;
	
	public static void main(String args[]) throws Exception {
		File file = new File("src3.jpg");
		FileInputStream in = new FileInputStream(file);
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream(); 
		byte[] buff = new byte[1024]; //buff用于存放循环读取的临时数据 
		int rc = 0; 
		while ((rc = in.read(buff, 0, 1024)) > 0) { 
			swapStream.write(buff, 0, rc); 
			len += rc;
		} 		
		data = swapStream.toByteArray(); //in_b为转换之后的结果 
		
		BladeFSClient client = new BladeFSClient(".\\conf\\client.properties");	
		Thread1 t1 = new Thread1(client,data,len);
		Thread2 t2 = new Thread2(client,data,len);
		t1.start();
		t2.start();
	}
}

