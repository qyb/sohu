package com.bladefs.client.io.cmd;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bladefs.client.de.Block;
import com.bladefs.client.de.DecisionEngine;
import com.bladefs.client.de.Protocol;
import com.bladefs.client.util.io.IoCmd;
import com.bladefs.client.util.io.IoSession;

public class NameServiceRequestBlockCmd extends PackUnit implements IoCmd {
	private DecisionEngine de = null;
	private final static Logger log = Logger.getLogger(NameServiceRequestBlockCmd.class);
	private boolean socketError = false;
	private byte[] BytesToCheck = null;
	public NameServiceRequestBlockCmd(DecisionEngine de){
		this.de = de;
	}
	
	@Override
	public void process(IoSession session) throws IOException {
		DataOutputStream out = session.getOutputStream();
		DataInputStream in = session.getInputStream();
		out.writeShort(Protocol.Client2NS_ReqBlock);
		out.flush();
		
		short cmd = in.readShort();
		if (cmd != Protocol.NS2Client_ReqBlock) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqBlock failed: Key Lost");
			return;
		}
		int len = getContentLen(in); 
		if(len >= 4){
			int errCode = in.readInt();
			if(errCode != 0){
				if(log.isEnabledFor(Level.ERROR))
					log.error("NS2Client_ReqBlock failed:errcode-- "+errCode);
				return;
			}
		}else{
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqBlock failed: len=" + len + "(len < 4)");
			return;
		}
		len -= Protocol.errlen;
		if (len < Protocol.verlen + Protocol.checksumlen + Protocol.blocksumlen) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqBlock failed:len(" + len + ") < " +
						  "Protocol.verlen(" + Protocol.verlen + ") + " +
						  "Protocol.checksumlen(" + Protocol.checksumlen + ") + " +
						  "Protocol.blocksumlen(" + Protocol.blocksumlen + ")");
			return;
		}

		byte[] bytesRead = new byte[Protocol.verlen + Protocol.blocksumlen];
		int index = 0;
		int ver = storeIntBytes(in, bytesRead, index);// 版本
		if (ver < de.getBlockVer()) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqBlock failed:ver < de.getBlockVer()");
			this.socketError = true;
			return;
		}
		
		index += Protocol.verlen;
		len -= Protocol.verlen;

		int count = storeIntBytes(in, bytesRead, index);// group 个数
		if (count < 0) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqBlock failed:count < 0");
			this.socketError = true;
			return;
		}

		index += Protocol.blocksumlen;// block 数目
		len -= Protocol.blocksumlen;

		if (!saveBlockInfo(in, bytesRead, ver, count)) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("NS2Client_ReqBlock failed:saveGStatInfo");
			this.socketError = true;
			return;
		} else {
			this.de.setNeedUpdateBlock(false);
		}
	}
	
	private boolean saveBlockInfo(DataInputStream in, byte[] bytesReaded,
			int version, int blocksum) throws IOException {
		boolean ret = false;
		short tmpcount = 1; // Notice here! Notice! block_id begin with 1
		short gid = -1;
		byte status = -1;
		HashMap<Short, Block> blockMap = new HashMap<Short, Block>();
		BytesToCheck = new byte[bytesReaded.length + blocksum * (Protocol.blockstatlen + Protocol.groupIDlen)];
		int bytesindex = 0; // BytesToCheck
		System.arraycopy(bytesReaded, 0, BytesToCheck, 0, bytesReaded.length);
		bytesindex += bytesReaded.length;

		while (tmpcount <= blocksum) {
			gid = storeShortBytes(in, BytesToCheck, bytesindex);
			if (gid == -1)
				return ret;

			bytesindex += Protocol.groupIDlen;
			if ((status = in.readByte()) == -1)
				return ret;

			BytesToCheck[bytesindex++] = status;
			Block bk = new Block(gid, status);
			blockMap.put(tmpcount++, bk);
		}

		if (tmpcount - 1 == blocksum) {// block_id index begin with 1
			byte[] b = new byte[Protocol.checksumlen];
			in.read(b);
			if (checksumOK(BytesToCheck, b)) {
				this.de.setBlock(blockMap, version);
				this.de.setNeedUpdateBlock(false);
				ret = true; // success!
			}
		}
		return ret;
	}
	public boolean isSocketError() {
		return socketError;
	}
}

