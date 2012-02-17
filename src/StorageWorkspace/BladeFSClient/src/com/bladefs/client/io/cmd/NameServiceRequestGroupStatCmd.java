package com.bladefs.client.io.cmd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.de.GroupStat;
import com.bladefs.client.de.Protocol;
import com.bladefs.client.util.io.IoCmd;
import com.bladefs.client.util.io.IoSession;

public class NameServiceRequestGroupStatCmd extends PackUnit implements IoCmd {
	private DecisionEngine de = null;
	private final static Logger log = Logger.getLogger(NameServiceRequestGroupStatCmd.class);
	private boolean socketError = false;
	private byte[] BytesToCheck = null;
	public NameServiceRequestGroupStatCmd(DecisionEngine de){
		this.de = de;
	}
	
	public void process(IoSession session) throws IOException {
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2NS_ReqGStat);
		out.flush();

		short cmd = in.readShort();
		if (cmd != Protocol.NS2Client_ReqGStat) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("Client2NS_ReqGStat failed:Key Lost");
			this.socketError = true;
			return;
		}

		int len = getContentLen(in); 
		if(len >= 4){
			int errCode = in.readInt();
			if(errCode != 0){
				if(log.isEnabledFor(Level.ERROR))
					log.error("Client2NS_ReqGStat failed:errcode-- "+errCode);
				return;
			}
		}else{
			if(log.isEnabledFor(Level.ERROR))
				log.error("Client2NS_ReqGStat failed: len=" + len + "(len < 4)");
			return;
		}
		len -= Protocol.errlen;
		if (len < Protocol.verlen + Protocol.checksumlen + Protocol.groupsumlen) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("Client2NS_ReqGStat failed:len(" + len + ") < " +
						  "Protocol.verlen(" + Protocol.verlen + ") + " +
						  "Protocol.checksumlen(" + Protocol.checksumlen + ") + " +
						  "Protocol.groupsumlen(" + Protocol.groupsumlen + ")");
			this.socketError = true;
			return;
		}

		byte[] bytesRead = new byte[Protocol.verlen + Protocol.groupsumlen];
		int index = 0;
		int ver = storeIntBytes(in, bytesRead, index);
		if (ver == this.de.getGStatVer()) {
			if(log.isEnabledFor(Level.ERROR)){
				log.error("Client2NS_ReqGStat failed:ver == this.de.getGStatVer()");
				log.error("Client2NS_ReqGStat failed:ver ="+this.de.getGStatVer());
			}
			this.socketError = true;
			this.de.setNeedUpdateGStat(false);
			return;
		}

		index += Protocol.verlen;
		len -= Protocol.verlen;

		int count = storeIntBytes(in, bytesRead, index);
		if (count < 0) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("Client2NS_ReqGStat failed:count<0");
			this.socketError = true;
			return;
		}

		index += Protocol.groupsumlen;
		len -= Protocol.groupsumlen;
		if (!saveGStatInfo(in, bytesRead, ver, count)) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("Client2NS_ReqGStat failed:saveGStatInfo failed");
			this.socketError = true;
			return;
		}
		else{
			this.de.setNeedUpdateGStat(false);
		}
	}
	
	private boolean saveGStatInfo(DataInputStream in, byte[] bytesReaded, int version, int groupsum)throws IOException {
		boolean ret = false;
		int bytesindex= 0;
		int groupindex = 0;
		int gstatlen = Protocol.groupIDlen + Protocol.groupstatlen+Protocol.groupweightlen;
		BytesToCheck = new byte[bytesReaded.length + groupsum * gstatlen];
		HashMap<Short, GroupStat> tmpgroupStatmap = new HashMap<Short, GroupStat>();
		byte status= -1;
		byte weight = -1;
		short gid = -1;		
		System.arraycopy(bytesReaded, 0, BytesToCheck, 0, bytesReaded.length);
		bytesindex += bytesReaded.length;
		
		while (groupindex < groupsum) {
			gid = storeShortBytes(in,BytesToCheck,bytesindex);
			if (gid == -1)
				return ret;			
			
			bytesindex += 2;		
			status = in.readByte();			
			if (status == -1)
				return ret;
			
			weight = in.readByte();
			if (weight == -1)
				return ret;
			
			BytesToCheck[bytesindex++] = status;
			BytesToCheck[bytesindex++] = weight;			
			GroupStat gs = new GroupStat(gid,weight,status);
			groupindex++;
			tmpgroupStatmap.put(gid, gs);
		}
		
		if(groupindex == groupsum)
		{
			byte[] b = new byte[Protocol.checksumlen];
			in.read(b);
			if (checksumOK(BytesToCheck,b)) {
				this.de.setGroupStat(tmpgroupStatmap, version);
				ret = true;
				// finally,gstat list all download.you success!
			}
		}		
		return ret;
	}
	
	public boolean isSocketError() {
		return socketError;
	}
}



