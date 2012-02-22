package com.bladefs.client.io.cmd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.de.Protocol;
import com.bladefs.client.util.io.IoCmd;
import com.bladefs.client.util.io.IoSession;


public class DataServiceRecoverCmd  extends PackUnit implements IoCmd{
	private final static Logger log = Logger.getLogger(DataServiceRecoverCmd.class);
	private long fileName = -1;
	private int errCode = -1;
	private int sequenceNumber = 0;
	
	public DataServiceRecoverCmd(){
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		String hostInfo = " (Sequence Number: " + sequenceNumber + " LocalHost IP: " + session.getLocalAddr().getHostAddress() + ", Local PORT: " + session.getLocalPort() + 
	                           " PeerHost IP: " + session.getPeerAddr().getHostAddress() + ", PeerHost PORT: " + session.getPeerPort() + ")";
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2DS_RECOVER);
		out.writeInt(sequenceNumber);
		out.writeLong(fileName);
		out.flush();
		try {
			short cmd = in.readShort();
			if (cmd != Protocol.DS2Client_RECOVER) {
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_RECOVER, Key Lost " + cmd + " filename:" + fileName + hostInfo);
				return;
			}
			
			long seqNum = in.readInt();
			if (seqNum != sequenceNumber) {
				if (log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_RECOVER failed: Sequence number is not equal (recv:" + seqNum + ")" + hostInfo);
				return;
			}
			
			errCode = in.readInt(); 
			if(errCode!=0){
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_RECOVER failed!errcode: "+errCode + " filename:" + fileName + hostInfo);
			}
			
		}catch (Exception e) {
			if(log.isEnabledFor(Level.ERROR)){
				log.error("DS2Client_RECOVER failed-- " + e + hostInfo);
				log.error("DS2Client_RECOVER failed2-- " + "fileName: " + fileName + DecisionEngine.DECISIONENGINEINST.getFileInfo(fileName) + hostInfo);
			}
		}

	}
	
	public int getErrCode() {
		return errCode;
	}
	
	public void setSequenceNumber(int seqNum) {
		this.sequenceNumber = seqNum;
	}
	
	public void setFileName(long fileName){
		this.fileName = fileName;
	}
}
