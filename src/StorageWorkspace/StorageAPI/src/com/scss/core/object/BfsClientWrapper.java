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
	
	private final static int DEFAULT_BUFFER_CAPACITY = 8192;
	private final static int MAX_BUFFER_CAPACITY = 1024*1024*1024*16; // 16M
	
	public static BfsClientResult putFromStream(InputStream stream) {
		BfsClientResult result = new BfsClientResult();
		int len = 0;
		int total = 0;
		byte[] buf = new byte[512];
		
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
				// TODO: Conf.get
				BladeFSClient client = new BladeFSClient(".\\conf\\client.properties");
				result.Size = total;
				result.FileNumber = client.write(buffer.array(), total);
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
}
