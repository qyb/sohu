package com.bladefs.client.io.cmd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bladefs.client.de.DataService;
import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.de.Group;
import com.bladefs.client.de.Protocol;
import com.bladefs.client.util.io.IoCmd;
import com.bladefs.client.util.io.IoSession;

public class NameServiceRequestGroupCmd extends PackUnit implements IoCmd {
	private DecisionEngine de = null;
	private final static Logger log = Logger.getLogger(NameServiceRequestGroupCmd.class);
	private boolean socketError = false;
	private byte[] BytesToCheck = null;
	public NameServiceRequestGroupCmd(DecisionEngine de){
		this.de = de;
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2NS_ReqGroup);
		out.flush();
		
		short cmd = in.readShort();
		if(cmd != Protocol.NS2Client_ReqGroup){
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqGroup failed:Key Lost");
			this.socketError = true;
			return;
		}
			
		int len = getContentLen(in); // (len + err)
		if(len >= 4){
			int errCode = in.readInt();
			if(errCode != 0){
				if(log.isEnabledFor(Level.ERROR))
					log.error("NS2Client_ReqGroup failed:errcode-- "+errCode);
				return;
			}
		}else{
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqGroup failed: len=" + len + "(len < 4)");
			return;
		}
		len -= Protocol.errlen;
		if (len < Protocol.verlen + Protocol.checksumlen + Protocol.groupsumlen){
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqGroup failed:len(" + len + ") < " +
						  "Protocol.verlen(" + Protocol.verlen + ") + " +
						  "Protocol.checksumlen(" + Protocol.checksumlen + ") + " +
						  "Protocol.groupsumlen(" + Protocol.groupsumlen + ")");
			this.socketError = true;
			return;
		}

		byte[] bytesRead = new byte[Protocol.verlen+Protocol.groupsumlen];
		int index =0;
		int ver = storeIntBytes(in,bytesRead,index);
		if (ver < de.getGroupVer()){
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqGroup failed:ver < de.getGroupVer()");
			this.socketError = true;
			return;
		}
		
		index += Protocol.verlen;
		len -= Protocol.verlen;

		int count = storeIntBytes(in,bytesRead,index);// group
		if (count < 0){
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqGroup failed:count<0");
			this.socketError = true;
			return;
		}

		index += Protocol.groupsumlen;
		len -= Protocol.groupsumlen;
		if(!saveGroupInfo(in, bytesRead, ver, count, len)){
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqGroup failed:saveGroupInfo Failed");
			this.socketError = true;
			return;
		}
		else{
			this.de.setNeedUpdateGroup(false);
		}
	}
	
	private boolean saveGroupInfo(DataInputStream in, byte[] bytesReaded, int version, int groupsum, int length)throws IOException {
		boolean ret = false;
		int bytesindex= 0;
		int groupindex= 0;
		HashMap<Short, Group> tmpgroupmap = 
				new HashMap<Short, Group>();
		int machinelen = Protocol.IPlen + Protocol.portlen + Protocol.masterlen;
		BytesToCheck = new byte[bytesReaded.length +length - Protocol.checksumlen];
		short gid = -1;
		int machinenum = -1;
		System.arraycopy(bytesReaded, 0, BytesToCheck, 0, bytesReaded.length);
		bytesindex += bytesReaded.length;
		
		while (groupindex < groupsum) {
			gid = storeShortBytes(in,BytesToCheck,bytesindex);
			if (gid < 0)
				return ret;
			
			bytesindex += Protocol.groupIDlen;
			machinenum = storeIntBytes(in,BytesToCheck,bytesindex);
			if (machinenum < 0)
				return ret;
			
			bytesindex += Protocol.machinesumlen;			
			DataService[] dsArray = getMachine(in, BytesToCheck, bytesindex, machinenum);
			if (dsArray == null || dsArray.length != machinenum)
				return ret;
			
			bytesindex +=  machinenum*machinelen;			
			// if you come here,group success!
			Group group = new Group(dsArray, gid);
			groupindex++;
			tmpgroupmap.put(gid, group);//
		} // loop,group
		
		if (groupindex != groupsum)
			return ret;
		else
		{
			byte[] b = new byte[Protocol.checksumlen];
			in.read(b);
			if (checksumOK(BytesToCheck,b)) {
				this.de.setGroup(tmpgroupmap, version);
				ret = true;
			}
		}
		
		return ret;
	}
	
	private DataService[] getMachine(DataInputStream in, byte[] bytesReaded, int brIndex, int machinenum) throws IOException{
		byte master = -1;
		DataService [] dsArray = new DataService[machinenum];
		int machindex = 0;
		short port = -1;
		byte tmpIP = -1;
		int bytesindex = brIndex;
		while(machindex < machinenum){
			int ipindex =0;
			int ip[] = new int[4];
			while(ipindex < 4){
				tmpIP = in.readByte();			
				bytesReaded[bytesindex++] = tmpIP;
				ip[ipindex++] = (tmpIP < 0) ? (tmpIP + 256) : tmpIP;
			} // loop,ip addr
			
			if (ipindex != 4) // ip address success!
				return null;
			
			port = storeShortBytes(in, bytesReaded, bytesindex);
			if (port == -1)
				return null;
			
			bytesindex += Protocol.portlen;
						
			master = in.readByte();			
			if ((master != 0)&&(master != 1)) 
				return null;
			
			BytesToCheck[bytesindex++] = master;
			// if you come here,machine success!
			String tmphost = ints2IP(ip[0],ip[1],ip[2],ip[3]);				
			boolean tmpmaster = (master == 0)?false:true;
			dsArray[machindex++] = new DataService(tmphost,port,tmpmaster);
		} // loop,machine
		
		return dsArray;		
	}
	public boolean isSocketError() {
		return socketError;
	}
}

