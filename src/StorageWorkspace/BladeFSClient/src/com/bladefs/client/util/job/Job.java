package com.bladefs.client.util.job;

public interface Job<T> {
	void exec(T t);
	void end();
}
