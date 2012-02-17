package com.bladefs.client.de;

import java.util.Arrays;

public class Group {
	private DataService machines[] = null;
	private short groupID = -1; // 分组 ID
	public Group(DataService[] machines, short groupID) {
		super();
		this.groupID = groupID;
		this.machines = machines;
	}
	public DataService[] getMachines() {
		return machines;
	}
	public short getGroupID() {
		return groupID;
	}
	public void setGroupID(short groupID) {
		this.groupID = groupID;
	}
	public DataService getMaster() {
		DataService map = null;
		if(machines != null){
			int index = 0;
			while(index < machines.length){
				if(machines[index].isMaster()){
					map = machines[index];			
					break;
				}
				index++;
			}
		}
		return map;
	}
		
	@Override
	public String toString() {
		return "Group [machines=" + Arrays.toString(machines) + ", groupID="
				+ this.groupID + "]";
	}
}
