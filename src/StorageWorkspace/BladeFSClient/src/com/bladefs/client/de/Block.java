package com.bladefs.client.de;

public class Block {	
	// Block_Group 映射关系
	private short groupID; // 对应所在的分组 ID 2Bytes
	private byte status; // Block 当前的服务状态 2Bytes
	
	public Block(short groupID, byte status) {
		super();
		this.groupID = groupID;
		this.status = status;
	}
	public short getGroupID() {
		return groupID;
	}
	public void setGroupID(short groupID) {
		this.groupID = groupID;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Block [groupID=" + groupID + ", status=" + status + "]";
	}
}
