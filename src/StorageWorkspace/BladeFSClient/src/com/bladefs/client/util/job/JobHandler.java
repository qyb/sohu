package com.bladefs.client.util.job;

public interface JobHandler<T> {
	void handle(T t);
}

