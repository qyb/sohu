package com.bladefs.client.io;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bladefs.client.de.DataService;
import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.de.TaskType;
import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;
import com.bladefs.client.io.cmd.DataServiceReadCmd;
import com.bladefs.client.io.cmd.DataServiceWriteCmd;
import com.bladefs.client.io.cmd.DataServiceDeleteCmd;
import com.bladefs.client.io.cmd.DataServiceRecoverCmd;
import com.bladefs.client.util.BladeFSConfig;
import com.bladefs.client.util.BladeFSConfigFactory;
import com.bladefs.client.util.io.imp.TCPShortClient;

public class DataServiceClient{
	private DecisionEngine de = null;
	private BladeFSConfig config = null; 
	private Logger logger = Logger.getLogger(DataServiceClient.class);
	private long waitTime = 10*1000;
	public DataServiceClient(DecisionEngine de) throws BladeFSException{
		this.de = de;
		config = BladeFSConfigFactory.getBladeFSConfig();
	}
	
	private byte[] tryRead(long fileName, int timeout, int connCount, long tryTime) throws UnknownHostException, BladeFSException {
		//第一次读取
		List<DataService> dss = this.de.getDecisionOut(TaskType.Read, fileName);
		int seqNum = 0;
		synchronized (this.de) {
			seqNum = this.de.getSequenceNumber();
		}
		for(DataService ds:dss){//只要能从一个DataService上读成功，就返回
			TCPShortClient tcpShortClient = new TCPShortClient(ds.getHost(), ds.getPort(), timeout, connCount);
			DataServiceReadCmd cmd = new DataServiceReadCmd();
			cmd.setFileName(fileName);
			cmd.setSequenceNumber(seqNum);
			try {
				tcpShortClient.serve(cmd);
			} catch (IOException e) {
				logger.error("connect to ds(ip:" + ds.getHost() + ", port:" + ds.getPort() + ") failed. " + "Sequence num= " + seqNum + " read fail: " + e.getMessage());
			}
			tcpShortClient.close();
			if (cmd.getErrCode() == 3) {
				throw new BladeFSException("File [" + Long.toString(fileName) + "](sequence:" + seqNum +") has been deleted");
			}
			if (cmd.isReadSuccess()) 
				return cmd.getFile();
		}	
		
		return null;
	}
	
	public byte[] read(long fileName) throws UnknownHostException, InterruptedException, NameServiceException, BladeFSException {
		while(!de.isInitDone()){
			logger.warn("read------!de.isInitDone()");
			synchronized(this.de){				
				this.de.updateDataIfNeed();
				this.de.wait(waitTime);
			}		
		}
		int timeout = (int)config.getDataserviceConnTimeout();
		int connCount = (int)config.getDataserviceConnCount();
		long tryTime = config.getDataserviceTryTime();
		byte[] data = null;
		//第一次读取
		if ((data = tryRead(fileName, timeout, connCount, tryTime)) == null){
			//依然读取失败，则更新数据，等待一个时间以后再取，如果依然失败，则返回错误
			synchronized(this.de){
				this.de.updateDataIfNeed();
			}			
			data = tryRead(fileName, timeout, connCount, tryTime);
		}
		
		if (data == null) {
			throw new BladeFSException("file [" + Long.toString(fileName) + "] cannot read!");
		}
		return data;
	}
	
	public long write(byte[] data, int len) throws UnknownHostException, InterruptedException, NameServiceException, BladeFSException {
		while(!de.isInitDone()){
			logger.warn("write------!de.isInitDone()");
			synchronized(de){
				this.de.updateDataIfNeed();
				this.de.wait(waitTime);					
			}
		}
		if(!de.isWriteAble())
			throw new BladeFSException("NameService cannot update!");
		if (len > config.getDataserviceMaxFileSize())
			throw new BladeFSException("File is too large! Current file size:" + len);
		int timeout = (int)config.getDataserviceConnTimeout();
		int connCount = (int)config.getDataserviceConnCount();     //连接次数？
		long fileName = -1;
		List<DataService> dss = this.de.getDecisionIn(connCount);   //dss means dataservices, 是ds的复数形式
		int seqNum = 0;
		synchronized (this.de) {
			seqNum = this.de.getSequenceNumber();
		}
		for(DataService ds:dss){                                   //同读操作相同，只要有一个在master上写操作能成功就返回，不再继续循环
			TCPShortClient tcpShortClient = new TCPShortClient(ds.getHost(), ds.getPort(), timeout, connCount);
			DataServiceWriteCmd cmd = new DataServiceWriteCmd();
			cmd.setSequenceNumber(seqNum);
			cmd.setFileData(data);
			cmd.setFileLen(len);
			try {
				tcpShortClient.serve(cmd);
			} catch (IOException e) {
				logger.error("connect to ds(ip:" + ds.getHost() + ", port:" + ds.getPort() + ") failed. " + "Sequence num= " + seqNum + " write fail: " + e.getMessage());
			}
			fileName = cmd.getFileName();
			tcpShortClient.close();
			if (fileName > 0) {
				return fileName;
			} else {
				fileName = cmd.getErrCode();
				if(logger.isEnabledFor(Level.ERROR)){
					logger.error("DataService write failed ds: "+ds.toString());
					logger.error("File cannot write! errcode=[" + Long.toString(fileName) + "](sequence:" + seqNum +")");
				}
			}
		}
		
		return -1;
	}
	
	private boolean tryRecover(long fileName, int timeout, int connCount, long tryTime) throws UnknownHostException {
		List<DataService> dss = this.de.getDecisionOut(TaskType.Recover, fileName);
		int seqNum = 0;
		synchronized (this.de) {
			seqNum = this.de.getSequenceNumber();
		}
		for(DataService ds:dss){
			TCPShortClient tcpShortClient = new TCPShortClient(ds.getHost(), ds.getPort(), timeout, connCount);
			DataServiceRecoverCmd cmd = new DataServiceRecoverCmd();
			cmd.setSequenceNumber(seqNum);
			cmd.setFileName(fileName);
			try {
				tcpShortClient.serve(cmd);
			} catch (IOException e) {
				logger.error("connect to ds(ip:" + ds.getHost() + ", port:" + ds.getPort() + ") failed. " + "Sequence num= " + seqNum + " recover fail: " + e.getMessage());
			}
			tcpShortClient.close();
			if (cmd.getErrCode() == 0) 
				return true;			
		}
		
		return false;
	}
	
	public boolean recover(long fileName) throws UnknownHostException, InterruptedException, NameServiceException, BladeFSException  {
		while(!de.isInitDone()){
			logger.warn("recover------!de.isInitDone()");
			synchronized(de){
				this.de.updateDataIfNeed();
				this.de.wait(waitTime);
			}	
		}
		
		if(!de.isWriteAble())
			throw new BladeFSException("NameService cannot update!");
		
		int timeout = (int)config.getDataserviceConnTimeout();
		int connCount = (int)config.getDataserviceConnCount();
		long tryTime = config.getDataserviceTryTime();
		
		//第一次恢复
		boolean ret = tryRecover(fileName, timeout, connCount, tryTime);
		if(!ret){
			//依然恢复失败，则更新数据，等待一个时间以后再取，如果依然失败，则返回错服
			synchronized(this.de){
				this.de.updateDataIfNeed();
			}	
			ret = tryRecover(fileName, timeout, connCount, tryTime);
		}
		
		return ret;
	}
	
	private boolean tryDelete(long fileName, int timeout, int connCount, long tryTime) throws UnknownHostException {
		List<DataService> dss = this.de.getDecisionOut(TaskType.Delete, fileName);
		int seqNum = 0;
		synchronized (this.de) {
			seqNum = this.de.getSequenceNumber();
		}
		for(DataService ds:dss){
			TCPShortClient tcpShortClient = new TCPShortClient(ds.getHost(), ds.getPort(), timeout, connCount);
			DataServiceDeleteCmd cmd = new DataServiceDeleteCmd();
			cmd.setSequenceNumber(seqNum);
			cmd.setFileName(fileName);
			try {
				tcpShortClient.serve(cmd);
			} catch (IOException e) {
				logger.error("connect to ds(ip:" + ds.getHost() + ", port:" + ds.getPort() + ") failed. " + "Sequence num= " + seqNum + " delete fail: " + e.getMessage());
			}
			tcpShortClient.close();
			if(cmd.getErrCode()==0)
				return true;
			
		}
		
		return false;
	}
	
	public boolean delete(long fileName) throws UnknownHostException, InterruptedException, NameServiceException, BladeFSException  {
		while(!de.isInitDone()){
			logger.warn("delete------!de.isInitDone()");
			synchronized(de){
				this.de.updateDataIfNeed();
				this.de.wait(waitTime);
			}	
		}
		
		if(!de.isWriteAble())
			throw new BladeFSException("NameService cannot update!");
		
		int timeout = (int)config.getDataserviceConnTimeout();
		int connCount = (int)config.getDataserviceConnCount();
		long tryTime = config.getDataserviceTryTime();
		
		//第一次删除
		boolean ret = tryDelete(fileName, timeout, connCount, tryTime);
		if(!ret){
			//依然删除失败，则更新数据，等待一个时间以后再取，如果依然失败，则返回错服
			synchronized(this.de){
				this.de.updateDataIfNeed();
			}	
			ret = tryDelete(fileName, timeout, connCount, tryTime);
		}
		
		return ret;
	}
}
