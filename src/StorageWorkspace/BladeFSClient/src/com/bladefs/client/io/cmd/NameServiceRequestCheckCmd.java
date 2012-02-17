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

public class NameServiceRequestCheckCmd extends PackUnit implements IoCmd {
	private DecisionEngine de = null;
	private final static Logger log = Logger.getLogger(NameServiceRequestCheckCmd.class);
	private boolean socketError = false;
	public NameServiceRequestCheckCmd(DecisionEngine de){
		this.de = de;
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2NS_ReqCheck);
		out.flush();
		
		short cmd = in.readShort();
		if (cmd != Protocol.NS2Client_ReqCheck) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqCheck failed:Key Lost");
			this.socketError = true;
			return;
		}

		int len = getContentLen(in); 
		if(len >= 4){
			int errCode = in.readInt();
			if(errCode != 0){
				if(log.isEnabledFor(Level.ERROR))
					log.error("NS2Client_ReqCheck failed:errcode-- "+errCode);
				return;
			}
		}else{
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqCheck failed: len=" + len + "(len < 4)");
			return;
		}
		len -= Protocol.errlen;
		if (len < Protocol.checksumlen) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqCheck failed: len(" + len + ") < Protocol.checksumlen(" + Protocol.checksumlen + ")");
			this.socketError = true;
			return;
		}
		
		int GStat_Ver = -1;
		int Group_Ver = -1;
		int Block_Ver = -1;

		byte[] BytesToCheck = new byte[Protocol.verlen * 3];
		int index = 0;
		int bytesindex = 0;
		int tmpver = -1;
		int id[] = new int[3];
		
		while (index < 3) {
			tmpver = storeIntBytes(in, BytesToCheck, bytesindex);
			id[index++] = tmpver;
			bytesindex += 4;
		}

		GStat_Ver = id[0];
		Group_Ver = id[1];
		Block_Ver = id[2];
		byte[] b = new byte[Protocol.checksumlen];
		in.read(b);	
		if (checksumOK(BytesToCheck, b)) {
			this.de.setUpdateStatus(Block_Ver, Group_Ver, GStat_Ver);
		} else {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqCheck failed:checksumOK Failed");
			this.socketError = true;
			return;
		}
	}

	public boolean isSocketError() {
		return socketError;
	}
	
}
