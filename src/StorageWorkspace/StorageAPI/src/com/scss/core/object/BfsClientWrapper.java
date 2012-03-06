package com.scss.core.object;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.bladefs.client.BladeFSClient;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;

public class BfsClientWrapper {
	private final static Logger logger = Logger.getLogger(BfsClientWrapper.class);
	public final static long BFS_ERROR_UNKNOW = -1; 
	
	public final static int DEFAULT_BUFFER_CAPACITY = 4096;
	public final static int MAX_BUFFER_CAPACITY = 16777216; // 16M
	
	public final static int MAX_SIZE = 16777216; // 16M
	
	private static BfsClientWrapper instance = null;
	
	private BladeFSClient client = null;
	
	/*
	 * Get the instance of BFS client.
	 */
	public static BfsClientWrapper getInstance() {
		
		if (null == BfsClientWrapper.instance) {
			// TODO: make it configurated. conf.get ...
			BfsClientWrapper.instance = new BfsClientWrapper("client.properties");
		}

		return BfsClientWrapper.instance;
	}

	protected BfsClientWrapper(String conf) {
		
		if (null == this.client) {
		
			try {
				Properties prop = new Properties();
				InputStream ins = Thread.currentThread().getContextClassLoader().getResourceAsStream("client.properties");
				prop.load(ins);
				this.client = new BladeFSClient(prop);
			} catch (IOException e) {
				logger.error(e, e);
			} catch (BladeFSException e) {
				logger.error(e, e);
			} catch (NameServiceException e) {
				logger.error(e, e);
			}

		}
		
	}
	
	/*
	 * Write BFS file from a input stream.
	 * TODO: we need a buffer pool
	 */
	public BfsClientResult putFromStream(InputStream stream, int size) {
		BfsClientResult result = new BfsClientResult();
		int len = 0;
		int off = 0;
		//byte[] buf = new byte[512];
		
		logger.info(String.format("data length to write to BFS : %d\n", size));
		
		// TODO: !!! BFS client should able to return a stream !!!
		if (null != stream) {
			byte[] buf = new byte[size];
			//ByteBuffer buffer = ByteBuffer.allocate(BfsClientWrapper.DEFAULT_BUFFER_CAPACITY);
			try {
				while ((len = stream.read(buf, off, size - off)) > 0) {
					off += len;
				}
			} catch (IOException e) {
				logger.error(e, e);
			}
			
			try {
				result.Size = off;
				result.FileNumber = this.client.write(buf, off);
			} catch (BladeFSException e) {
				logger.error(e, e);
			} catch (NameServiceException e) {
				logger.error(e, e);
			} catch (InterruptedException e) {
				logger.error(e, e);
			} catch (UnknownHostException e) {
				logger.error(e, e);
			}
			
		}
		
		return result;
	}
	
	public BfsClientResult getFile(long file_num) {
		BfsClientResult result = new BfsClientResult();
		int len = 0;
		int total = 0;
		byte[] buf = new byte[512];
		
		result.FileNumber = file_num;
		byte[] data;
		try {
			// TODO: !!! BFS client should able to return a stream !!!
			data = this.client.read(file_num);
			result.Size = data.length;
			result.File = data;	
		} catch (UnknownHostException e) {
			logger.error(e, e);
		} catch (InterruptedException e) {
			logger.error(e, e);
		} catch (NameServiceException e) {
			logger.error(e, e);
		} catch (BladeFSException e) {
			logger.error(e, e);
		}
		return result;
	}
	
	public void deleteFile(long file_num) {
		try {
			this.client.delete(file_num);
		} catch (UnknownHostException e) {
			logger.error(e, e);
		} catch (InterruptedException e) {
			logger.error(e, e);
		} catch (NameServiceException e) {
			logger.error(e, e);
		} catch (BladeFSException e) {
			logger.error(e, e);
		}
	}
}
