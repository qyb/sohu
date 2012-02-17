package com.bladefs.client.util.strategy;

import org.apache.log4j.*;

/**
 * <pre>
 * 基于滑动窗口的Token算法，可用于Client或其他控制
 * 具有快速启动，快速拥塞，自适应能力，每秒允许请求量由滑动窗口cwnd控制。
 * 
 * 适应以下情况：
 * 1. 快速启动：
 *     正常启动时，滑动窗口初始为100，模拟TCP/IP慢启动算法，指数级增长使请求控制很快达到上限
 *     若刚开始请求失败，算法不受影响，依然处于开始启动状态，滑动窗口还是1。
 * 2. 服务宕机，快速拥塞：
 *     则滑动窗口快速设置为1，请求量马上得到控制，并保持每秒一次试探式的请求服务。
 *     若此时服务恢复，则使用快速启动算法，请求控制恢复正常。
 * 3. 服务承受压力过大，连接数受限，或者网络适应能力差：
 *     此时启动自适应能力，滑动窗口逐渐减小，请求失败率越大，滑动窗口减小的越快。
 *     当请求量减少到服务或网络可承受的量时，此时滑动窗口启用线性增长算法，慢慢放开请求控制
 * </pre>
 *
 */
public class FlowTokenStrategy {

	protected int FAIL_LIMIT = 3;
	protected Logger log;	
	protected final String clientId;	
	protected int lastTimeSec = 0; 		// 上次请求时间
	protected int tokenNum = 0;			// 令牌控制
	protected int cwnd = 1; 			// 滑动窗口
	protected int ssthresh = 10000; 	// 初始压力控制
	protected int requestCount = 0;   	// 请求次数
	protected int succCount = 0;		// 成功次数
	protected int failCount = 0;		// 失败次数
	protected int errorCount = 0;		// 卸载次数
	public FlowTokenStrategy(String clientId) {
		this(clientId, null);
	}
	
	public FlowTokenStrategy(String clientId, String logName) {
		this.clientId = clientId;
		this.log = Logger.getLogger(logName == null || logName.isEmpty() ? "flowsecurity" : logName);
	}

	public String getClientId(){ return clientId; }
	public int getLastTimeSec(){ return lastTimeSec; }
	public int getTokenNum() { return tokenNum; }
	public int getCwnd() { return cwnd; }
	public int getSsthresh() { return ssthresh; }
	public int getRequestCount() { return requestCount; }
	public int getSuccCount() { return succCount; }
	public int getFailCount() { return failCount; }
	public int getErrorCount() { return errorCount; }
	
	/**
	 * 获取Token快照，快照中<br>
	 * tokenNum 表示尚有多少令牌可用<br>
	 * @return
	 */
	public synchronized Snapshot getSnapshot(){
		return new Snapshot(clientId, lastTimeSec, tokenNum>0, tokenNum, 
			cwnd, ssthresh, requestCount, succCount, 
			failCount, errorCount);
	}
	
	/**
	 * 获取令牌并返回执行令牌之后的快照，快照中<br>
	 * token = true/false 表示成功/失败<br>
	 * tokenNum 表示尚有多少令牌可用
	 * @return
	 */
	public synchronized Snapshot getSnapshotToken(){
		boolean t = getToken();
		return new Snapshot(clientId, lastTimeSec, t, tokenNum, 
			cwnd, ssthresh, requestCount, succCount, 
			failCount, errorCount);
	}
	
	/**
	 * 获取当前令牌
	 * @return
	 *   token = true/false 表示成功/失败<br>
	 */
	public synchronized boolean getToken() {
		long currMillis = System.currentTimeMillis();
		int currSec = (int)(currMillis / 1000L);
		if (currSec > lastTimeSec){ // 新的一秒开始
			int request = requestCount;
			int succ = succCount;
			int fail = failCount;
			int error = errorCount;
			int last_cwnd = cwnd;
			this.requestCount = 0;
			this.succCount = 0;
			this.failCount = 0;
			this.errorCount = 0;
			if (lastTimeSec == 0){
				// 刚启动
				cwnd = 100;
			}else if (succ == 0 && fail >= FAIL_LIMIT){
				// 全失败，服务宕机
				cwnd = 1;
			}else if (fail == 0 && succ != 0){
				// 全成功，快速启动算法
				if (cwnd < ssthresh) cwnd += succ;
				else {
					cwnd++;
					// 可承受压力上涨，+2使得如果上一轮succ==cwnd时，下一轮能够启用快速算法
					if (ssthresh < succ) ssthresh = succ+2; 
				}
//			}else if (cwnd < succ){
//				// 有成功也有失败请求，服务应该是刚起来的
//				cwnd += succ;
//				if (ssthresh < succ) ssthresh += succ; 
			}else {
				// 有成功也有失败请求，达到服务或网络可承受压力临界点
				if (fail >= succ && fail >= FAIL_LIMIT){
					// 失败占多数，指数减小滑动窗口
					cwnd >>= 1;
				}else {
					// 小幅度波动，线性减小活动窗口，此时承受压力具有参考价值
					cwnd -= fail;
					if (ssthresh > cwnd) ssthresh = cwnd;
				}
				// 保证每秒有一次试探式的请求控制
				if (cwnd <= 0) cwnd = 1;
			}
			
			tokenNum = cwnd;
			lastTimeSec = currSec;
			if (last_cwnd <= 1 && cwnd > 1){
				StringBuilder sb = new StringBuilder("[FlowToken] startup. clientId=").append(clientId);
				sb.append(", currSec=").append(currSec).append(", last_cwnd=").append(last_cwnd).append(", cwnd=").append(cwnd);
				sb.append(", ssthresh=").append(ssthresh).append(", request=").append(request).append(", succ=");
				sb.append(succ).append(", fail=").append(fail).append(", error=").append(error);
				if(log.isEnabledFor(Level.WARN))log.warn(sb.toString());
			}else if (last_cwnd > 1 && cwnd <= 1){
				StringBuilder sb = new StringBuilder("[FlowToken] shundown. clientId=").append(clientId);
				sb.append(", currSec=").append(currSec).append(", last_cwnd=").append(last_cwnd).append(", cwnd=").append(cwnd);
				sb.append(", ssthresh=").append(ssthresh).append(", request=").append(request).append(", succ=");
				sb.append(succ).append(", fail=").append(fail).append(", error=").append(error);
				if(log.isEnabledFor(Level.WARN))log.warn(sb.toString());
			}else if (log.isInfoEnabled()){
				StringBuilder sb = new StringBuilder("[FlowToken] running. clientId=").append(clientId);
				sb.append(", currSec=").append(currSec).append(", last_cwnd=").append(last_cwnd).append(", cwnd=").append(cwnd);
				sb.append(", ssthresh=").append(ssthresh).append(", request=").append(request).append(", succ=");
				sb.append(succ).append(", fail=").append(fail).append(", error=").append(error);
				if(log.isEnabledFor(Level.INFO))log.info(sb.toString());
			}
		}
		if (tokenNum <= 0) {
			++errorCount;
			return false;
		}
		--tokenNum;
		return true;
	}
	
	/**
	 * 获得令牌之后，响应成功，用于调整策略
	 */
	public synchronized void succ(){
		++succCount;
		++tokenNum;
	}
	
	/**
	 * 获得令牌之后，响应失败，用于调整策略
	 */
	public synchronized void fail(){
		++failCount;
		if (tokenNum > 1) --tokenNum;
	}
	
	@Override
	public String toString(){
		Snapshot ss = getSnapshot();
		return new StringBuilder("[FlowToken] ").append(ss).toString();
	}
	
	public static class Snapshot{
		protected final String clientId;
		protected final int lastTimeSec; 	// 上次请求时间
		protected final boolean token;		// 令牌控制
		protected final int tokenNum;		// 令牌数量
		protected final int cwnd; 		// 滑动窗口
		protected final int ssthresh; 	// 初始压力控制
		protected final int requestCount; // 请求次数
		protected final int succCount;	// 成功次数
		protected final int failCount;	// 失败次数
		protected final int errorCount;	// 卸载次数
		protected Snapshot(String clientId, int lastTimeSec, boolean token, int tokenNum, int cwnd,
			int ssthresh, int requestCount, int succCount, int failCount, int errorCount){
			this.clientId = clientId;
			this.lastTimeSec = lastTimeSec;
			this.token = token;
			this.tokenNum = tokenNum;
			this.cwnd = cwnd;
			this.ssthresh = ssthresh;
			this.requestCount = requestCount;
			this.succCount = succCount;
			this.failCount = failCount;
			this.errorCount = errorCount;
		};
		
		public String getClientId(){ return clientId; }
		public int getLastTimeSec(){ return lastTimeSec; }
		public boolean getToken() { return token; }
		public int getTokenNum() { return tokenNum; }
		public int getCwnd() { return cwnd; }
		public int getSsthresh() { return ssthresh; }
		public int getRequestCount() { return requestCount; }
		public int getSuccCount() { return succCount; }
		public int getFailCount() { return failCount; }
		public int getErrorCount() { return errorCount; }
		
		@Override
		public String toString(){
			StringBuilder sb = new StringBuilder("clientId=").append(clientId);
			sb.append(", lastTimeSec=").append(lastTimeSec).append(", token=").append(token).append(", tokenNum=").append(tokenNum);
			sb.append(", cwnd=").append(cwnd).append(", ssthresh=").append(ssthresh);
			sb.append(", request=").append(requestCount).append(", succ=").append(succCount)
			.append(", fail=").append(failCount).append(", error=").append(errorCount);
			return sb.toString();
		}
	}
}

