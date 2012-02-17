package com.bladefs.client.de;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.exception.NameServiceException;
import com.bladefs.client.io.NameServiceClient;

public enum DecisionEngine {
	DECISIONENGINEINST;//单例
	public final static int ROUND_COUNT = 5;
	protected List<Short> groupWeightArray = null;                     // 组的权重array
	protected int writeOnGroupCount = 0;
	protected HashMap<Short, GroupStat> groupStatMap = null;
	protected HashMap<Short, Group> groupMap = null;
	protected HashMap<Short, Block> blockMap = null;
	protected int gStatVer = 0;                                  // current version of Group Stat
	protected int blockVer = 0;                                  // current version of Block info
	protected int groupVer = 0;                                  // current version of Group info
	protected Random rd = null;
	protected boolean needUpdateGStat = false;
	protected boolean needUpdateGroup = false;
	protected boolean needUpdateBlock = false;
	protected boolean isUpdating = false;	
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private boolean initDone = false;                            // Client当前状态
	private boolean writeAble = false;
	private Logger logger = Logger.getLogger(DecisionEngine.class);
	private int sequenceNumber = 0;
	
	private DecisionEngine() {
		//super();
		groupWeightArray = new ArrayList<Short>();
		groupStatMap = new HashMap<Short, GroupStat>();
		groupMap = new HashMap<Short, Group>();
		blockMap = new HashMap<Short, Block>();
		rd = new Random();
		rd.setSeed(System.currentTimeMillis());
		sequenceNumber = rd.nextInt();
	}
	
	public List<DataService> getSingleGroup(short gid){
		List<DataService> ret = new ArrayList<DataService>();
		GroupStat gs = this.groupStatMap.get(gid);
		if(gs.getStatus() != Protocol.WRITE_ON)
			return null;
		DataService ds = this.groupMap.get(gid).getMaster();
		if(ds == null)
			return null;;
		ret.add(ds);
		return ret;
	}
	
	/*
	 * 此方法只用于写操作
	 * */
	public List<DataService> getDecisionIn(int connCount){
		List<DataService> selectedDataService = new ArrayList<DataService>();
		if(connCount == 0 || this.writeOnGroupCount == 0){
			return selectedDataService;
		}
		//用于保存已经加到ret的dataservice所在的组的Id
		HashSet<Short> selectedGrpIdSet = new HashSet<Short>();
		try{
			lock.readLock().lock();
			int weight = this.groupWeightArray.size();
			// 初始状态，weight全部为非零
			if (weight <= 0) { 
				logger.info("weight == 0");
				for (short grpId : this.groupStatMap.keySet()) {
					if(grpId >= 0){
						DataService machine = this.groupMap.get(grpId).getMaster();
						if (machine != null){//此处只需要获取到一个master便返回，为何下面要一堆？
							selectedDataService.add(machine);
							break;
						}
					}
				}				
				return selectedDataService;
			}			
			/*
			 * 如果当前的组数小于等于repCount * ROUND_COUNT，循环的次数为组数, 返回值ret的size最大为组数
			 * 如果当前的组数大于repCount * ROUND_COUNT，循环的次数为repCount * ROUND_COUNT， 返回值ret的最大size为repCount * ROUND_COUNT
			 * repCount * ROUND_COUNT是为何？？
			 * */
			for(int i = 0; i < connCount; i++){
				/*
				 * 如果已选择的组数已经大于或等于当前可读可写的组的个数
				 * 或者
				 * 如果要返回的DataService的个数已经大于或等于重复写的次数，
				 * 那么就要结束循环，跳出返回
				 * */
				if(selectedGrpIdSet.size() >= this.writeOnGroupCount || selectedDataService.size() >= connCount){
					break;
				}
				/*
				 * 获取[0,weight)间的一个随机数,
				 * 并根据此随机数从groupId数组中选取一个组id，
				 * 即相当于随机选择一个组id
				 * */
				int randomIdx = Math.abs(rd.nextInt()) % weight;  
				short grpId = this.groupWeightArray.get(randomIdx);                                                
				/*
				 * 如果selectedGrpIdSet里不包含当前组id，且当前组、组的状态、组下面的master不为空，且组的状态为可读可写，
				 * 那么就把当前组的master dataservice加到返回值ret中，同时把当前组id加到已选择组集合selectedGrpIdSet中
				 * */
				if(!selectedGrpIdSet.contains(grpId)){
					Group grp = this.groupMap.get(grpId);
					if(grp != null){                                                                         //如果当前组不为空
						GroupStat grpStat = this.groupStatMap.get(grpId);
						if(grpStat != null && grpStat.getStatus() == Protocol.WRITE_ON){                     //如果当前组状态不为空，且状态为可读可写
							DataService ds = this.groupMap.get(grpId).getMaster();
							if(ds != null){                                                                  //如果当前组的master不为空
								selectedDataService.add(ds);
								selectedGrpIdSet.add(grpId);
							}
						}
					}
				}
			}
			return selectedDataService;
		}catch(Exception e){
			return selectedDataService;
		}finally{
			lock.readLock().unlock();
		}
	}
	
	public String getFileInfo(long fileName) {
		short bid = getBlockID(fileName);
		Block block = this.blockMap.get(bid);
		if (block == null)
			return null;
		else {
			Group g = this.groupMap.get(block.getGroupID());
			return "bid: " + bid + g.toString();
		}
	}
	
	public List<DataService> getDecisionOut(int taskType, long fileName){
		List<DataService> ret = new ArrayList<DataService>();
		short bid = getBlockID(fileName);
		try{
			lock.readLock().lock();
			Block block = this.blockMap.get(bid);//根据block id获取block
			if(block == null){
				return ret;
			}
			Group g = this.groupMap.get(block.getGroupID());//根据group id获取group
			if(g == null){
				return ret;
			}
			GroupStat gs = this.groupStatMap.get(block.getGroupID());//根据group id获取group stat
			if(gs == null){
				return ret;
			}
			
			if (taskType == TaskType.Delete || taskType == TaskType.Recover) {//删除或恢复操作，为什么没有写操作？哦，写操作调用的是getDecisionIn
				if (gs.getStatus() != Protocol.WRITE_ON)
					return ret;
				else {
					DataService machine = g.getMaster();
					if (machine == null)
						return ret;
					ret.add(machine);
				}
			}
			else {	// taskType == TaskType.Read //读操作
				if((gs.getStatus() != Protocol.WRITE_ON) && (gs.getStatus() != Protocol.READ_ONLY))
				    return ret;
				DataService[] ds = g.getMachines();
				if (ds == null) {
					return ret;
				}
				ds = disarrange(ds);//将dataservice数组打乱,是为了随机，读操作只需要随机选一个机器成功完成即可
				for (int i = 0; i < ds.length; i++) {
					ret.add(ds[i]);
				}
			}
			
			return ret;			
		}
		catch(Exception e){
			return ret;
		}
		finally{
			lock.readLock().unlock();
		}
	}
	
	private static short getBlockID(long fileName) {
		long blockid = 0; 
		long tool = 0xFFFF00000000L;
		blockid = (fileName & tool);
		blockid = blockid >> 32;
		return (short) (blockid);
	}
	
	public void setUpdateStatus(int blockVer, int groupVer, int grouStatVer) {
		if (blockVer > this.blockVer) {
			setNeedUpdateBlock(true);
		}
		if (groupVer > this.groupVer) {
			setNeedUpdateGroup(true);
		}
		if (grouStatVer != this.gStatVer) {
			setNeedUpdateGStat(true);
		}
	}
	
	public void setNeedUpdateGStat(boolean needUpdateGStat) {
		try {
			lock.writeLock().lock();
			this.needUpdateGStat = needUpdateGStat;
		} finally {
			lock.writeLock().unlock();
		}	
	}

	public void setNeedUpdateGroup(boolean needUpdateGroup) {
		try {
			lock.writeLock().lock();
			this.needUpdateGroup = needUpdateGroup;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void setNeedUpdateBlock(boolean needUpdateBlock) {
		try{
			lock.writeLock().lock();
			this.needUpdateBlock = needUpdateBlock;
		}finally {
			lock.writeLock().unlock();
		}
	}

	public void setBlock(HashMap<Short, Block> tmpBlockMap, int blockVer){
		if(blockVer <= this.blockVer){
			return;
		}
		
		if(tmpBlockMap == null || tmpBlockMap.isEmpty()){
			return;
		}
				
		try {
			lock.writeLock().lock();
			this.blockMap = tmpBlockMap;
			tmpBlockMap = null;
			this.blockVer = blockVer;
			this.needUpdateBlock = false;
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void setGroup(HashMap<Short, Group> tmpGroupMap, int groupVer){
		if(groupVer <= this.groupVer){
			return;
		}
		
		if(tmpGroupMap == null || tmpGroupMap.isEmpty()){
			return;
		}
				
		try{
			lock.writeLock().lock();
			this.groupMap = tmpGroupMap;
			tmpGroupMap = null;
			this.groupVer = groupVer;
			this.needUpdateGroup = false;
		}
		finally{
			lock.writeLock().unlock();
		}
	}
	
	/*
	 * 设置groupStatMap表
	 * */
	public void setGroupStat(HashMap<Short, GroupStat> tmpGroupStatMap, int gStatVer){
		if(gStatVer == this.gStatVer){// gStat update when version changed(increase or decrease) 
			return;
		}
		
		if(tmpGroupStatMap == null || tmpGroupStatMap.isEmpty()){
			return;
		}
		
		List<Short> tmpGroupArray = new ArrayList<Short>(); 
		int i = 0;
		int tmpWriteOnGroupCount = 0;
		for(Short k:tmpGroupStatMap.keySet()){
			GroupStat gs = tmpGroupStatMap.get(k);
			if(gs.getStatus() != Protocol.WRITE_ON){
				continue;
			}
			int weight = Math.abs((int)gs.getWeight());
			for(i = 0; i < weight; i++){//权重值是多少，就要在array里加多少个一样的grpID
				tmpGroupArray.add(k);
			}
			tmpWriteOnGroupCount++;
		}
		
		try{
			lock.writeLock().lock();
			this.groupStatMap = tmpGroupStatMap;
			tmpGroupStatMap = null;
			this.groupWeightArray = tmpGroupArray;
			tmpGroupArray = null;
			this.gStatVer = gStatVer;
			this.writeOnGroupCount = tmpWriteOnGroupCount;
			this.needUpdateGStat = false;
		}
		finally{
			lock.writeLock().unlock();
		}
	}

	public boolean isNeedUpdateGStat() {
		try{
			lock.readLock().lock();
			return needUpdateGStat;
		}
		finally{
			lock.readLock().unlock();
		}
	}

	public boolean isNeedUpdateGroup() {
		try{
			lock.readLock().lock();
			return needUpdateGroup;
		}
		finally{
			lock.readLock().unlock();
		}
	}

	public boolean isNeedUpdateBlock() {
		try{
			lock.readLock().lock();
			return needUpdateBlock;
		}
		finally{
			lock.readLock().unlock();
		}
	}

	public int getGStatVer() {
		try{
			lock.readLock().lock();
			return gStatVer;
		}
		finally{
			lock.readLock().unlock();
		}
	}

	public int getBlockVer() {
		try{
			lock.readLock().lock();
			return blockVer;
		}
		finally{
			lock.readLock().unlock();
		}
	}

	public int getGroupVer() {
		try{
			lock.readLock().lock();
			return groupVer;
		}
		finally{
			lock.readLock().unlock();
		}
	}
	
	public void updateData(){//我估计是有个任务在背 后一直在判断这三个值是否为true，若为true则进行更新
		try{
			lock.writeLock().lock();
			this.needUpdateBlock = true;
			this.needUpdateGroup = true;
			this.needUpdateGStat = true;
		}
		finally{
			lock.writeLock().unlock();
		}
	}
	
	public void updateDataIfNeed() throws NameServiceException, BladeFSException{
		if(!this.isUpdating){
			NameServiceClient.getInstance().checkAndUpdate();
		}
	}
	
	public int getSequenceNumber() {
		try {
			lock.readLock().lock();
			sequenceNumber++;
			return sequenceNumber;
		} finally {
			lock.readLock().unlock();
		}
	}

	public boolean isUpdating() {
		try{
			lock.readLock().lock();
			return isUpdating;
		}
		finally{
			lock.readLock().unlock();
		}
	}

	public void setUpdating(boolean isUpdating) {
		try{
			lock.writeLock().lock();
			this.isUpdating = isUpdating;
		}
		finally{
			lock.writeLock().unlock();
		}
	}
	
	private DataService[] disarrange(DataService[] machines){
		int size = machines.length;
		if(size < 1) return machines;
		int randoms[] = getRandomIndexArray(size);
		DataService tmpMachines[] = new DataService[size];
		for (int j = 0; j < size; j++) {
			tmpMachines[j] = machines[randoms[j]];
		}
		machines = tmpMachines;
		/*randoms = getRandomIndexArray(size);
		for (int j = 0; j < size; j++) {
			machines[j] = tmpMachines[randoms[j]];
		}*/
		return machines;
	}

	private int[] getRandomIndexArray(int size){
		int[] ret = new int[size];
		Random rd = new Random();
		rd.setSeed(System.currentTimeMillis());
		int tmpindex = -1;
		int tmp= -1;
		for(int i =0; i< size; i++){
			ret[i] = i;
		}
		for(int i=0; i< size; i++){ // 每次随机挑选一个与最后一个元素换位		
			tmpindex = Math.abs(rd.nextInt()) % size;
			tmp = ret[size-1];
			ret[size-1] = ret[tmpindex];
			ret[tmpindex] = tmp;
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return "DecisionEngine [groupArray=" + groupWeightArray
				+ ", writeOnGroupCount=" + writeOnGroupCount
				+ ", groupStatMap=" + groupStatMap + ", groupMap=" + groupMap
				+ ", blockMap=" + blockMap + ", gStatVer=" + gStatVer
				+ ", blockVer=" + blockVer + ", groupVer=" + groupVer + ", rd="
				+ rd + ", needUpdateGStat=" + needUpdateGStat
				+ ", needUpdateGroup=" + needUpdateGroup + ", needUpdateBlock="
				+ needUpdateBlock + "]";
	}

	public void setInitDone(boolean initDone) {
		try{
			lock.writeLock().lock();
			this.initDone = initDone;
		}
		finally{
			lock.writeLock().unlock();
		}
	}
	
	public boolean isWriteAble() {
		return writeAble;
	}

	public void setWriteAble(boolean writeAble) {
		this.writeAble = writeAble;
	}
	
	public boolean isInitDone() {
		try{
			lock.readLock().lock();
			return initDone;
		}
		finally{
			lock.readLock().unlock();
		}
	}
}
