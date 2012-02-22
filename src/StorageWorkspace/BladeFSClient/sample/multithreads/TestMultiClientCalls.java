package com.bladefs.client.multithreads;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;


import org.apache.log4j.PropertyConfigurator;

import com.bladefs.client.BladeFSClient;

public class TestMultiClientCalls {
	private static byte[] data = null;
	private static int len = 0;
	
	public static void main(String args[]) throws Exception {
		PropertyConfigurator.configure(".\\conf\\log4j.properties");
		data = new byte[2];
		data[0] = 0;
		data[1] = 1;
		len = 2;
		BladeFSClient client = new BladeFSClient(".\\conf\\client.properties");	
		for(int i =0; i<10; i++){
			ChildThread thread = new ChildThread(client,data,len,i);
			thread.start();
		}
	}
}

