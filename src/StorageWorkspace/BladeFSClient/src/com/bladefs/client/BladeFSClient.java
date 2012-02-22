package com.bladefs.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;
import com.bladefs.client.io.DataServiceClient;
import com.bladefs.client.io.NameServiceClient;
import com.bladefs.client.util.BladeFSConfigFactory;

public class BladeFSClient {

	private DecisionEngine de = null;
	private DataServiceClient dataServiceClient = null;
	
	public BladeFSClient(Properties p) throws BladeFSException, NameServiceException {
		BladeFSConfigFactory.createBladeFSConfig(p);
		NameServiceClient client = NameServiceClient.getInstance();
		de = client.getDecisionEngine();
		dataServiceClient = new DataServiceClient(de);
	}
	
	public BladeFSClient(String fileName) throws IOException, BladeFSException, NameServiceException{
		BladeFSConfigFactory.createBladeFSConfig(fileName);
		NameServiceClient client = NameServiceClient.getInstance();
		de = client.getDecisionEngine();
		dataServiceClient = new DataServiceClient(de);

	}

	/**
	 * reads file from data server,
	 * 
	 * @param    fileName 
	 * @return   the buffer into which the data is read, or null if no file is found.
	 * @throws   UnknownHostException 
	 * @throws   InterruptedException
	 * @throws   BladeFSException
	 */
	public byte[] read(long fileName) throws InterruptedException, UnknownHostException, NameServiceException, BladeFSException {
		if (fileName > 0)
			return dataServiceClient.read(fileName);
		throw new BladeFSException("File [" + Long.toString(fileName) + "] format error");
		
	}
	
	public long write(byte[] data, int len) throws UnknownHostException, BladeFSException, NameServiceException, InterruptedException {
		long fileName = -1;
		if (data == null || data.length < 1 || len < 1) {
			throw new BladeFSException("file content is empty!");
		} else {
			fileName = dataServiceClient.write(data, len);
		}

		return fileName;
	}
	
	public boolean delete(long fileName) throws UnknownHostException, InterruptedException, NameServiceException, BladeFSException { 
		if (fileName > 0) {
			return dataServiceClient.delete(fileName);
		}

		throw new BladeFSException("File [" + Long.toString(fileName) + "] format error");
	}
	
	public boolean recover(long fileName) throws UnknownHostException, InterruptedException, NameServiceException, BladeFSException {
		if (fileName > 0)
			return dataServiceClient.recover(fileName);
		throw new BladeFSException("File [" + Long.toString(fileName) + "] format error");
	}	
}
