package com.scss.core.object;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

import com.bladefs.client.BladeFSClient;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;

public class BfsClientWrapper {
	
	public final static long BFS_ERROR_UNKNOW = -1; 
	
	private final static int DEFAULT_BUFFER_CAPACITY = 4096;
	private final static int MAX_BUFFER_CAPACITY = 1024*1024*1024*16; // 16M
	
	private static BfsClientWrapper instance = null;
	
	private BladeFSClient client = null;
	
	/*
	 * Get the instance of BFS client.
	 */
	protected static BfsClientWrapper getInstance() {
		
		if (null == BfsClientWrapper.instance) {
			// TODO: make it configurated. conf.get ...
			BfsClientWrapper.instance = new BfsClientWrapper(".\\conf\\client.properties");
		}

		return BfsClientWrapper.instance;
	}

	protected BfsClientWrapper(String conf) {
		
		if (null == this.client) {
		
			try {
				this.client = new BladeFSClient(conf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BladeFSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NameServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
	}
	
	/*
	 * Write BFS file from a input stream.
	 */
	public BfsClientResult putFromStream(InputStream stream) {
		BfsClientResult result = new BfsClientResult();
		int len = 0;
		int total = 0;
		byte[] buf = new byte[512];
		
		// TODO: !!! BFS client should able to return a stream !!!
		if (null != stream) {
			ByteBuffer buffer = ByteBuffer.allocate(BfsClientWrapper.DEFAULT_BUFFER_CAPACITY);
			try {
				while (-1 != (len = stream.read(buf))) {
					if (buffer.position() + len > buffer.capacity())
						if (buffer.capacity() < BfsClientWrapper.MAX_BUFFER_CAPACITY)
							buffer.limit(buffer.capacity() * 2);
						else
							throw new BufferOverflowException();
					buffer.put(buf, 0, len);
					total += len;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				result.Size = total;
				result.FileNumber = this.client.write(buffer.array(), total);
			} catch (BladeFSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NameServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NameServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BladeFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
