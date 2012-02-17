package com.bladefs.client.util.job;

import java.util.concurrent.LinkedBlockingQueue;

public class AsyncJob<T> implements Job<T> {

	protected final LinkedBlockingQueue<T> elements = new LinkedBlockingQueue<T>();
	protected final JobHandler<T> handler;
	protected final Worker worker;
	
	/**
	 * @deprecated please use constructor with name
	 */
	@Deprecated
	public AsyncJob(JobHandler<T> handler) {
		this("AsyncJob:handler="+handler.getClass().getName(), handler, false);
	}
	
	public AsyncJob(String name, JobHandler<T> handler) {
		this(name, handler, false);
	}
	
	/**
	 * @deprecated please use constructor with name
	 */
	@Deprecated
	public AsyncJob(JobHandler<T> handler, boolean daemon) {
		this("AsyncJob:handler="+handler.getClass().getName(), handler, daemon);
	}
	
	public AsyncJob(String name, JobHandler<T> handler, boolean daemon) {
		this.handler = handler;
		this.worker = new Worker(name);
		if(daemon) worker.setDaemon();
		worker.begin();
	}
	
	@Override
	public void exec(T t) {
		if(t != null) elements.offer(t);
	}

	@Override
	public void end() {
		worker.end();
	}

	public int size() {
		return elements.size();
	}

	protected class Worker extends Servo {
		public Worker(String name){super(name);}
		@Override
		public void fin() {
			T t = null;
			while((t = elements.poll()) != null) {
				handler.handle(t);
			}
		}
		@Override
		public void serve() throws Exception {
			handler.handle(elements.take());
		}
	}
}

