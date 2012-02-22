package com.bladefs.client.de;


public class GroupStat implements Comparable<GroupStat>{
	private final byte write_on = 0; // 1Byte 0可读写 1只读 2临时不可用 3废弃
//	private final byte read_only = 0; // 1Byte 0可读写 1只读 2临时不可用 3废弃
//	private final byte hold_on = 0; // 1Byte 0可读写 1只读 2临时不可用 3废弃
//	private final byte expired = 0; // 1Byte 0可读写 1只读 2临时不可用 3废弃
	
	private short groupTD = -1; //  ID 2Bytes
	private byte weight = 0; // Group  1Byte
	private byte status = 0; // 1Byte 0可读写 1只读 2临时不可用 3废弃
	
	public GroupStat(short groupTD, byte weight, byte status) {
		super();
		this.groupTD = groupTD;
		this.weight = weight;
		this.status = status;
	}
	public short getGroupTD() {
		return groupTD;
	}
	public void setGroupTD(short groupTD) {
		this.groupTD = groupTD;
	}
	public byte getWeight() {
		return weight;
	}
	public void setWeight(byte weight) {
		this.weight = weight;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	public boolean isWriteable() {
		if(this.status == write_on)
			return true;
		return false;
	}
	@Override
	public String toString() {
		return "GroupStat [groupTD=" + groupTD + ", weight=" + weight
				+ ", status=" + status + "]";
	}
	
	@Override
	public int compareTo(GroupStat gs) {
		// TODO Auto-generated method stub
		if (gs.getWeight() > this.getWeight()) {
			return 1;
		} else if (gs.getWeight() < this.getWeight()) {
			return -1;
		} else {
			return 0;
		}
	}
	
}
