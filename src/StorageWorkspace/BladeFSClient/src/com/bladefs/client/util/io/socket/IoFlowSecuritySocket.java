package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.*;

import com.bladefs.client.util.io.IoSocket;
import com.bladefs.client.util.stat.StatClient;
import com.bladefs.client.util.strategy.FlowTokenStrategy;
import com.bladefs.client.util.strategy.FlowTokenStrategy.Snapshot;

/**
 * <pre>
 * 基于滑动窗口的安全SecurityClient
 * 使用 FlowToken 控制。
 * </pre>
 *
 */
public class IoFlowSecuritySocket implements IoSocket {

	private final Logger log;
	
	private final IoSocket client;
	private final String clientId;
	private final StatClient statClient;
	private final FlowTokenStrategy token;
	
	public IoFlowSecuritySocket(IoSocket client, String clientId, StatClient statClient) {
		this(client, clientId, statClient, null);
	}
	
	public IoFlowSecuritySocket(IoSocket client, String clientId, StatClient statClient, String logName) {
		this.client = client;
		this.clientId = clientId;
		this.statClient = statClient;
		this.token = new FlowTokenStrategy("IoSocket:"+clientId);
		this.log = Logger.getLogger(logName == null || logName.isEmpty() ? "flowsecurity" : logName);
	}
	
	@Override
	public Socket checkOut(int timeout, boolean alive) throws IOException {
		Socket result = null;
		long before = 0, after=0;
		int state = 0;
		// 首先获取令牌，控制每秒的请求量
		Snapshot snap = token.getSnapshotToken();
		if(snap.getToken()) {
			before = System.currentTimeMillis();
			result = client.checkOut(timeout, alive);
			after = System.currentTimeMillis();
			if(result == null) {
				token.fail();
				state = -1;
			} else {
				token.succ();
				state = 0;
			}
		} else {
			state = 1;
			StringBuilder sb = new StringBuilder("[IoFlowSecuritySocket.checkOut] getToken fail. ").append(snap);
			if(log.isEnabledFor(Level.WARN))
				log.warn(sb.toString());
		}
		
		if(statClient != null) {
			statClient.report(state, (int)(after-before));
		}
		
		return result;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("[IoFlowSecuritySocket] clientId=").append(clientId);
		return sb.toString();
	}
}
