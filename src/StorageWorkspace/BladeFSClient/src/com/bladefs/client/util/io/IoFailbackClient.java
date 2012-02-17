package com.bladefs.client.util.io;

import java.io.IOException;

public class IoFailbackClient implements IoClient {

	private final IoClient first;
	private final IoClient second;
	private final long ttbInMillis;
	
	private volatile long lastErrorTimeInMillis = 0;
	
	public IoFailbackClient(IoClient first, IoClient second, long ttbInMillis) {
		this.first = first;
		this.second = second;
		this.ttbInMillis = ttbInMillis;
	}

	@Override
	public void close() {
		first.close();
		second.close();
	}

	@Override
	public boolean serve(IoCmd cmd) throws IOException {
		
		boolean doFirst = true;
		final long let = lastErrorTimeInMillis;
		
		if(let != 0) {
			if(System.currentTimeMillis() - let < ttbInMillis) doFirst = false;
			else lastErrorTimeInMillis = 0;
		}
		
		boolean result = true;
		if(doFirst) {
			result = first.serve(cmd);
			if(!result) {
				// 指示下一次serve将切至second
				lastErrorTimeInMillis = System.currentTimeMillis();
				// 尝试在second上再执行一次当前cmd
				result = second.serve(cmd);
			}
		} else {
			result = second.serve(cmd);
			if(!result) {
				// 指示下一次serve将切至first
				lastErrorTimeInMillis = 0;
				// 尝试在first上再执行一次当前cmd
				result = first.serve(cmd);
			}
		}
		
		return result;
	}
}
