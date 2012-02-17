package com.bladefs.client.util.job;

import org.apache.log4j.*;

public abstract class Servo {
	private final static Logger log = Logger.getLogger(Servo.class);
	
	private final Worker worker;
	
	/**
	 * @deprecated please use constructor with name
	 */
	@Deprecated
	public Servo() {
		this("com.soso.wenwen.util.tool.Servo", false);
	}
	
	public Servo(String name) {
		this(name, false);
	}
	
	/**
	 * @deprecated please use constructor with name
	 */
	@Deprecated
	public Servo(boolean daemon) {
		this("com.soso.wenwen.util.tool.Servo", daemon);
	}
	
	public Servo(String name, boolean daemon) {
		worker = new Worker(name);
		if(daemon) setDaemon();
	}
	
	public void setDaemon() {
		worker.setDaemon(true);
	}

	public void begin() {
		worker.start();
	}
	public void end() {
		worker.close();
	}
	
	public abstract void serve() throws Exception;
	public void fin() {}
	
	private class Worker extends Thread {
		private volatile boolean stop = false;
	
		public Worker(String name){super(name);}
		@Override
		public void run() {
			while(!stop) {
				try {
					serve();
				} catch (Exception e) {
					if(log.isEnabledFor(Level.ERROR))log.error("[ServThread<"+worker.getName()+">:run] reason=io error or interrupted", e);
				} catch (Throwable t) {
					if(log.isEnabledFor(Level.ERROR))log.error("[ServThread<"+worker.getName()+">:run] reason=unknown error", t);
				}
			}
			try {
				fin();
			} catch (Throwable t) {
				if(log.isEnabledFor(Level.ERROR))log.error("[ServThread<"+worker.getName()+">:fin] reason=unknown error", t);
			}
		}
		
		public void close() {
			this.stop = true;
			this.interrupt();
		}
	}
}
