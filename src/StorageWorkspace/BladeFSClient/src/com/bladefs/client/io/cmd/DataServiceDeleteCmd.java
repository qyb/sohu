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

public class DataServiceDeleteCmd  extends PackUnit implements IoCmd{
	private final static Logger log = Logger.getLogger(DataServiceDeleteCmd.class);
	private long fileName = -1;
	private int errCode = -1;
	private int sequenceNumber = 0;
	
	public DataServiceDeleteCmd(){
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		String hostInfo = " (Sequence Number: " + sequenceNumber + " LocalHost IP: " + session.getLocalAddr().getHostAddress() + ", Local PORT: " + session.getLocalPort() + ")" + 
                " (PeerHost IP: " + session.getPeerAddr().getHostAddress() + ", PeerHost PORT: " + session.getPeerPort() + ")";
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2DS_DELETE);
		out.writeInt(sequenceNumber);
		out.writeLong(fileName);
		out.flush();
		try {
			short cmd = in.readShort();
			if (cmd != Protocol.DS2Client_DELETE) {
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_DELETE failed: Key Lost " + cmd + " filename: " + fileName + hostInfo);
				return;
			}
			
			long seqNum = in.readInt();
			if (seqNum != sequenceNumber) {
				if (log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_DELETE failed: Sequence number is not equal (recv:" + seqNum + ")" + hostInfo);
				return;
			}
			
			errCode = in.readInt(); 
			if(errCode!=0)
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_DELETE failed:errcode-- " + errCode + " filename: " + fileName + hostInfo);
			
		}catch (Exception e) {
			if(log.isEnabledFor(Level.ERROR)){
				log.error("DS2Client_DELETE failed1-- " + e + hostInfo);
				log.error("DS2Client_DELETE failed2-- " + "fileName: " + fileName + DecisionEngine.DECISIONENGINEINST.getFileInfo(fileName) + hostInfo);
			}
		}

	}
	
	public int getErrCode() {
		return errCode;
	}
	
	public void setSequenceNumber(int seqNum) {
		this.sequenceNumber = seqNum;
	}

	/*
	 * enum Del_Or_Recover_Return_Code 
	 * { 
	 * OK = 0, 
	 * ERR_FAILED, 
	 * ERR_FILE_NOT_FOUND,
	 * ERR_FILE_REPEAT, 
	 * ERR_OPEN_FILE_FAILED, 
	 * ERR_MODIFY_FAILED, 
	 * };
	 */
	public void setFileName(long fileName){
		this.fileName = fileName;
	}
}
