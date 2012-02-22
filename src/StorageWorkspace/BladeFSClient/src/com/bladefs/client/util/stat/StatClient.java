package com.bladefs.client.util.stat;

public interface StatClient {
	void report(int state, int params);
	void doReport();
}
