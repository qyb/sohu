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

public class DataServiceReadCmd  extends PackUnit implements IoCmd{
	private final static Logger log = Logger.getLogger(DataServiceReadCmd.class);
	private byte[] fileData = null;
	private long fileName = -1;
	private int errCode = -1;
	private int sequenceNumber = 0;
	
	public DataServiceReadCmd(){
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		String hostInfo = " (Sequence Number: " + sequenceNumber + " LocalHost IP: " + session.getLocalAddr().getHostAddress() + ", Local PORT: " + session.getLocalPort() + ")" + 
                " (PeerHost IP: " + session.getPeerAddr().getHostAddress() + ", PeerHost PORT: " + session.getPeerPort() + ")";
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2DS_READ);
		out.writeInt(sequenceNumber);
		out.writeLong(fileName);
		out.flush();
		try {
			short cmd = in.readShort();
			if (cmd != Protocol.DS2Client_READ) {
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_READ failed:Key Lost " + cmd + " filename:" + fileName + hostInfo);
				return;
			}
			
			long seqNum = in.readInt();
			if (seqNum != sequenceNumber) {
				if (log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_READ failed: Sequence number is not equal (recv:" + seqNum + ")" + hostInfo);
				return;
			}

			int len = getContentLen(in);
			if (len >= 4) {
				errCode = in.readInt();
				if (errCode != 0){
					if(log.isEnabledFor(Level.ERROR))
						log.error("DS2Client_READ failed code:"+errCode + " filename:" + fileName + hostInfo);
					setErrCode(errCode);
					return;
				}
				len -= Protocol.errlen;
			}
			else{
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_READ failed: len=" + len + "(len < 4) filename:" + fileName + hostInfo);
				return;
			}
			fileData = new byte[len];
			byte[] buf = new byte[1024*5];
			int buflen = 0;
			int currentlen = 0;
			while ((buflen = in.read(buf)) > 0){
				System.arraycopy(buf, 0, fileData, currentlen, buflen);
				currentlen += buflen;	
				if (currentlen >= len)
					break;
				buf = new byte[1024*5];
			}
			
			if(currentlen != len){
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_READ, currentlen(" + currentlen + ") != len(" + len + ") filename:" + fileName + hostInfo);
				return;
			}
//			if (len < Protocol.checksumlen) {
//				log.error("len < Failed");
//				return;
//			}
//			fileData = new byte[len - Protocol.checksumlen];
			// 校验和
//			if (tmp != -1) {
//				byte[] b = new byte[Protocol.checksumlen];
//				in.readFully(b);
//				if (!checksumOK(fileData, b)) {
//					fileData = null;
//					log.error("checksumOK Failed");
//					return;
//				}
//			}
//			else{
//				log.error("fileData read Failed");
//				return;
//			}			
		} catch (Exception e) {
			if(log.isEnabledFor(Level.ERROR)){
				log.error("DS2Client_READ failed-- "+e + hostInfo);
				log.error("DS2Client_READ failed2-- "+"fileName: "+fileName+DecisionEngine.DECISIONENGINEINST.getFileInfo(fileName) + hostInfo);
			}
		}

	}
	
	public byte[] getFile(){
		return fileData;
	}
	
	public boolean isReadSuccess(){
		if(fileData == null || errCode != 0)
			return false;
		
		return true;
	}
	
	public int getErrCode() {
		return errCode;
	}
	
	public void setSequenceNumber(int seqNum) {
		this.sequenceNumber = seqNum;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	
	public void setFileName(long fileName){
		this.fileName = fileName;
	}
	
    public int splitErrCode(int errcode){
		switch(errcode){
		case 0://READ_OK
			return 0;
		case 1://ERR_READ_FAILED
		case 2://ERR_READ_FILE_NOT_FOUND
		case 3://ERR_READ_FILE_DELETED
		case 4://ERR_READ_DISK_FAILED
		case 5://ERR_READ_CHECK_FAILED
		case 6://ERR_READ_OPEN_FILE_FAILED
			return -1;
		}
		return -1;// server exception
	}
}