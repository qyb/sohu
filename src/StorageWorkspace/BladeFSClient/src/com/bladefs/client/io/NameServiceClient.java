package com.bladefs.client.io;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;
import com.bladefs.client.io.cmd.NameServiceRequestCheckCmd;
import com.bladefs.client.io.cmd.NameServiceRequestBlockCmd;
import com.bladefs.client.io.cmd.NameServiceRequestGroupCmd;
import com.bladefs.client.io.cmd.NameServiceRequestGroupStatCmd;
import com.bladefs.client.util.BladeFSConfig;
import com.bladefs.client.util.IPPort;
import com.bladefs.client.util.BladeFSConfigFactory;
import com.bladefs.client.util.io.imp.TCPClient;

public class NameServiceClient{
	private static NameServiceClient nameServiceClient = null;
	
	private BladeFSConfig config = null;
	private DecisionEngine de = null;
	private Timer timer = null;
	private boolean firstStart = true;
	private static long timestamp = 0; // modify after pull&check
	private static TCPClient tcpClient = null;
	private Logger logger = Logger.getLogger(NameServiceClient.class);
	
	public synchronized static NameServiceClient getInstance() throws BladeFSException{
		if(nameServiceClient == null){
			nameServiceClient = new NameServiceClient();
		}
		return nameServiceClient;
	}
	private NameServiceClient() throws BladeFSException {
		config = BladeFSConfigFactory.getBladeFSConfig();
		de = DecisionEngine.DECISIONENGINEINST;
		updateData();
		start();
	}
	
	public DecisionEngine getDecisionEngine(){
		return de;
	}
	
	public void checkAndUpdate() {
		this.de.setUpdating(true);
		List<IPPort> nameServiceIps = config.getNameServiceIps();
		int size = nameServiceIps.size();
		Random rd = new Random();
		rd.setSeed(System.currentTimeMillis());
		int i = Math.abs(rd.nextInt()) % size;
		int count = 0;
		while (count < size) {
			count++;
			try {
				doCheckAndUpdate(nameServiceIps.get(i));
				break;
			} catch (UnknownHostException e) {
				logger.error("UnknownHostException:"+e);
				tcpClient = null;
			} catch (NameServiceException e) {
				logger.error("NameSericeException:"+e);
				tcpClient = null;
			} catch (InterruptedException e) {
				logger.error("InterruptException:"+e);
				tcpClient = null;
			} catch (Exception e) {
				logger.error("Exception:"+e);
				tcpClient = null;
			}
			++i;
			if (i >= size)
				i -= size;
		}		
		synchronized (de) {
			this.de.setUpdating(false);
			if(firstStart){
				this.de.setInitDone(true);
				firstStart = false;
			}
			this.de.notifyAll();
		}
		
		if(this.de.isWriteAble()){
			if(tcpClient == null)
			{
				this.de.setWriteAble(false);
			}
		}
		else{
			if(tcpClient != null)
			{
				this.de.setWriteAble(true);
			}
		}
	}
	
	public void updateData(){
		this.de.updateData();
	}
	
	public void start(){
		timer = new Timer();
		timer.schedule(new NSCacheTimerTask(), 0, config.getNameserviceCheckPeriod());
	}

	private void setTimestamp() {
		timestamp = System.currentTimeMillis();
	}
	
	private void doCheckAndUpdate(IPPort ipport) throws NameServiceException, InterruptedException, IOException{
		if(tcpClient == null){
			logger.warn("tcpClient == null");
			int connCount = (int)config.getNameserviceConnCount();
			int connTimeout = (int)config.getNameserviceConnTimeout();
			tcpClient = new TCPClient(ipport.getIp(), ipport.getPort(), 1,connTimeout, connCount);
		}
		NameServiceRequestCheckCmd check = new NameServiceRequestCheckCmd(this.de);
		if (!tcpClient.serve(check)) {
			throw new NameServiceException("tcpClient serve("
					+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
		}
		if (check.isSocketError()) {
			throw new NameServiceException("check socket error("
					+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
		}
		setTimestamp();		
		if(this.de.isNeedUpdateBlock()){
			NameServiceRequestBlockCmd block = new NameServiceRequestBlockCmd(this.de);
			if(!tcpClient.serve(block)){
				throw new NameServiceException("Request block name service("+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
			}
			if(block.isSocketError()){
				throw new NameServiceException("Request block name service("+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
			}
		}
		if(this.de.isNeedUpdateGroup()){
			NameServiceRequestGroupCmd group = new NameServiceRequestGroupCmd(this.de);
			if(!tcpClient.serve(group)){
				throw new NameServiceException("Request group name service(1"+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
			}
			if(group.isSocketError()){
				throw new NameServiceException("Request group name service(2"+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
			}
		}
		if(this.de.isNeedUpdateGStat()){
			NameServiceRequestGroupStatCmd gstat = new NameServiceRequestGroupStatCmd(this.de);
			if(!tcpClient.serve(gstat)){
				throw new NameServiceException("Request group stat name service("+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
			}
			if(gstat.isSocketError()){
				throw new NameServiceException("Request group name service("+ ipport.getIp() + ":" + ipport.getPort() + ") failed.");
			}
		}
	}
	
	public boolean timeExpired() {
		boolean ret = false;
		if (firstStart) {
			ret = true;
		} else {
			long tmptime = System.currentTimeMillis();
			if (config.getNameserviceCheckPeriod() <= (tmptime - timestamp))
				ret = true; // overtime
		}
		return ret;
	}
	
	public class NSCacheTimerTask extends TimerTask {
		@Override
		public void run() {
			if (timeExpired()) {
				if (!de.isUpdating())
					checkAndUpdate();
			}
		}
	}// class NSCacheTimerTask

}
