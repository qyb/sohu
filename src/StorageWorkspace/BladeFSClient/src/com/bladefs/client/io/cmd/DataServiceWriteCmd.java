package com.bladefs.client.io.cmd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bladefs.client.de.Protocol;
import com.bladefs.client.util.io.IoCmd;
import com.bladefs.client.util.io.IoSession;

public class DataServiceWriteCmd extends PackUnit implements IoCmd{
	private	final	Logger	log = Logger.getLogger(DataServiceWriteCmd.class);
	private byte[]	fileData = null;
	private long	fileName = -1;
	private int fileLen = 0;
	private int errCode = -1;
	private int sequenceNumber = 0;
	public DataServiceWriteCmd(){
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		String hostInfo = " (Sequence Number: " + sequenceNumber + " LocalHost IP: " + session.getLocalAddr().getHostAddress() + ", Local PORT: " + session.getLocalPort() + 
                " PeerHost IP: " + session.getPeerAddr().getHostAddress() + ", PeerHost PORT: " + session.getPeerPort() + ")";
		int addr = 0;
		if(fileData == null || fileLen <= 0){
			if(log.isEnabledFor(Level.ERROR))
				log.error("DS2Client_WRITE failed: fileData is NULL" + hostInfo);
			return;
		}
		
		if(fileLen > fileData.length){
			fileLen = fileData.length;
		}
		
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2DS_WRITE);
		out.writeInt(sequenceNumber);
		out.writeInt(fileLen);
		out.write(fileData, 0, fileLen);
		out.flush();
		addr =1;
		try {
			short cmd = in.readShort();
			addr = 2;
			if (cmd != Protocol.DS2Client_WRITE) {
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_WRITE failed: Key Lost " + cmd + hostInfo);
				return;
			}
			
			long seqNum = in.readInt();
			if (seqNum != sequenceNumber) {
				if (log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_WRITE failed: Sequence number is not equal (recv:" + seqNum + ")" + hostInfo);
				return;
			}
			
			int len = getContentLen(in); 
			if (len >= 4) {
				errCode = in.readInt();
				if (errCode != 0){
					if(log.isEnabledFor(Level.ERROR))
						log.error("DS2Client_WRITE failed code:" + errCode + hostInfo);
					setErrCode(errCode);
					return;
				}
				len -= Protocol.errlen;
			}
			else{
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_WRITE failed: len=" + len + "(len < 4) filename:" + fileName);
				return;
			}
			
			if (len != Protocol.fileNamelen){
				if(log.isEnabledFor(Level.ERROR))
					log.error("DS2Client_WRITE failed:len(" + len + ") != Protocol.fileNameLen(" + Protocol.fileNamelen + ")" + hostInfo);
				return;
			}
			byte[] tmpfileName = new byte[Protocol.fileNamelen];
			if (in.read(tmpfileName) != -1)
				fileName = bytes2Long(tmpfileName);
		}catch (Exception e) {
			if(log.isEnabledFor(Level.ERROR)){
				log.error("DS2Client_WRITE failed:Exception-- " + e + hostInfo);
				log.error("addr: " + addr + hostInfo);
			}
		}
	}
	
	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public void setSequenceNumber(int seqNum) {
		this.sequenceNumber = seqNum;
	}
	public void setFileData(byte[] data){
		fileData = data;
	}
	
	public void setFileLen(int len){
		fileLen = len;
	}
	
	public long getFileName(){
		if(fileName>0)
			return fileName;
		else
			return -1;
	}

	public int splitErrCode(int errcode){
		switch(errcode){
		case 0://WRITE_OK
			return 0;
		case 1://ERR_WRITE_FAILED
		case 2://ERR_WRITE_OPEN_FAILED
		case 3://ERR_Write_Slave_Down		
		case 5://ERR_Write_Master_Wrong
		case 7://E_Write_Read_Slave_Error
		case 8://E_Write_Slave_Write_Failed
		case 9://E_Write_Master_Confusion
			return -1;
		case 4://ERR_Write_Slave_Net_Down
		case 6://ERR_Write_Slave_TimeOut
			return -2;// timeout			
		}
		return -1;// server exception
	}
	
}
