package com.bladefs.client.util.io.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import org.apache.log4j.*;

import com.bladefs.client.util.io.IoSocket;
import com.bladefs.client.util.job.AsyncJob;
import com.bladefs.client.util.job.JobHandler;


public class AddressFactory {
	protected static final Logger log = Logger.getLogger(AddressFactory.class);
	
	protected static final String DELIM = ".";
	protected static final String ZHISHI_ADDRESS = "zhishi.address.";
	protected static final int ZHISHI_ADDRESS_LENGTH = ZHISHI_ADDRESS.length();

	// parameter
	protected static final String PORT = "port";
	protected static final String TIMEOUT_IN_MILLIS = "timeout";
	protected static final String LAYOUT = "layout";
	protected static final String LAYOUT_RETIES = "layout.tries";
	protected static final String LAYOUT_TTB = "layout.ttb";

	// layout
	protected static final String FAILOVER = "failover";
	protected static final String FAILBACK = "failback";
	protected static final String DUAL = "dual";

//	protected static Properties getProperties(String propFile){
//		try {
//			Properties p = new Properties();
//			InputStream is = DaoFactory.class.getClassLoader().getResourceAsStream(propFile);
//			if (is != null) p.load( is );
//			return p;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	// ================ interface start ==========================
//	/**
//	 * 
//	 * @param propFile eg. /com/soso/zhishi/dao/dao.properties
//	 * @return
//	 */
//	public static boolean configure(String propFile) {
//		Properties ps = getProperties(propFile);
//		if (ps != null){
//			pool.reloadAllAddresses(ps);
//			return true;
//		}else {
//			return false;
//		}
//	}
	
	public static void configure(Properties p){
		pool.reloadAllAddresses(p);
	}
	
	/**
	 * @param hostKey like zhishi.address.test or wenwen.soso.com:80
	 * @return
	 */
	
	public static IoSocket getIoSocket(String hostKey){
		if (hostKey.startsWith(ZHISHI_ADDRESS)) return pool.clients.get(hostKey);
		else {
			String[] ss = hostKey.split(":");
			if (ss.length==2){
				try {
					String hostName = ss[0];
					int port = Integer.parseInt(ss[1]);
					return getIoSocket(hostName, port);
				}catch(NumberFormatException e){
					
				}
			}
			if(log.isEnabledFor(Level.WARN))
			log.warn("[AddressFactory.getIoSocket] hostKey is invalid. hostKey="+hostKey);
			return null;
		}
	}
	
	/**
	 * 
	 * @param hostName like wenwen.soso.com, if is ip, always create new IoSocket
	 * @param port like 8080
	 * @return
	 */
	public static IoSocket getIoSocket(String hostName, int port){
		if (isIp(hostName)){
			String hostKey = hostName + ":" + port;
			IoSocket client = pool.clients.get(hostKey);
			if (client != null) return client;
			
			client = pool.createSimpleIoSocket(hostName, port);
			IoHostSocket hostClient = new IoHostSocket(null);
			hostClient.setSocketClient(client);
			pool.clients.put(hostKey, hostClient);
			return hostClient;
		}else {
			Map<Integer,String> hostKeys = pool.hostAddrs.get(hostName);
			if (hostKeys != null){
				String hostKey = hostKeys.get(port);
				if (hostKey != null){
					return pool.clients.get(hostKey);
				}
			} 
			String hostKey = hostName + ":" + port;
			return pool.reloadAddress(hostKey);
		}
		
		
	}
	
	public static boolean isIp(String hostName){
		if (hostName != null){
			String[] ips = hostName.split("\\.");
			if (ips.length == 4){
				try {
					for (String ip:ips){
						int pn = Integer.parseInt(ip);
						if (pn < 0 || pn > 255) return false;
					}
					return true;
				}catch(NumberFormatException e){}
			}
		}
		return false;
	}
	/**
	 * �첽ˢ��host
	 * @param hostName
	 */
	public static void flushHost(String hostName){
		pool.flushHostJobs.exec(hostName);
	}
	
	public static void close(){
		pool.flushHostJobs.end();
	}
	
	// ================ interface end ==========================
	
	protected static AddressFactory pool = new AddressFactory();
	protected Properties props;
	protected PropertySelector ps;
	protected AsyncJob<String> flushHostJobs; // host name
	protected Map<String, Host> hosts = new HashMap<String, Host>(); // <hostName,Host>
	// <hostName,<port,hostKey>>
	protected Map<String, Map<Integer,String>> hostAddrs = new TreeMap<String, Map<Integer,String>>();
	protected Map<String, IoSocketProxy> clients = new HashMap<String, IoSocketProxy>();
	
	protected AddressFactory(){
		props = new Properties();
		ps = new PropertySelector(props);
		flushHostJobs = new AsyncJob<String>("AddressFactory.flushHostJobs", new JobHandler<String>(){
			@Override
			public void handle(String hostName) {
				reloadHost(hostName);
			}}, true);
	}
	
	// =================== sync reload Addresses =======================
	/**
	 * sync flush all address
	 * @param p
	 */
	public void reloadAllAddresses(Properties p){
		if (p == null || p.isEmpty()) return ;
		props.putAll(p);
		Enumeration<?> enumeration = props.propertyNames();
		while(enumeration.hasMoreElements()){
			String key = (String) enumeration.nextElement();
	      	if(key.startsWith(ZHISHI_ADDRESS) && key.length() > ZHISHI_ADDRESS_LENGTH && key.indexOf('.', ZHISHI_ADDRESS_LENGTH) == -1) {
	      		reloadAddress(key);
	      	}
		}
	}

	/**
	 * sync flush address
	 * @param like zhishi.address.test
	 */
	protected IoSocketProxy reloadAddress(String hostKey) {
		if (hostKey == null || hostKey.isEmpty()) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.reloadHost] hostKey is null. hostKey=" + hostKey);
			return null;
		}

		IoSocket client = createIoSocket(hostKey);
		if (client == null) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.reloadHost] createIoSocket client is null. hostKey=" + hostKey);
		}

		IoSocketProxy host = createSocketProxy(hostKey);
		if (host == null) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.reloadHost] createSocketProxy host is null. hostKey=" + hostKey);
			return null;
		}
		host.setSocketClient(client);
		return host;
	}
	
	/**
	 * sync flush host
	 * @param hostName
	 */
	protected void reloadHost(String hostName){
		// flush host
		Host h = hosts.get(hostName);
		if (h == null) return ;
		
		if (h.flush()){
			// update the proxy
			Map<Integer,String> hostKeys = hostAddrs.get(hostName);
			if (hostKeys != null){
				for(Integer portKey: hostKeys.keySet()){
					String hostKey = hostKeys.get(portKey);
					IoSocket client = null;
					if (hostKey.startsWith(ZHISHI_ADDRESS)){
						client = createIoSocket(hostKey);
					}else {
						client = createSimpleIoSocket(hostName, portKey);
					}
					IoSocketProxy host = createSocketProxy(hostKey);
					host.setSocketClient(client);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param hostName like wenwen.soso.com
	 * @return
	 */
	protected Host createHost(String hostName) {
		if (hostName == null || hostName.isEmpty()) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.createHost] hostName is null.");
			return null;
		}
		Host h = hosts.get(hostName);
		if (h == null) {
			h = new Host(hostName, 120000);
			h.flush();
			hosts.put(hostName, h);
		}
		return h;
	}
	
	protected IoSocket createSimpleIoSocket(String hostName, int port){
		try {
			// make IoSocket array
			Host h = createHost(hostName);
			InetAddress[] addrs = h.getInetAddresses();
			if (addrs == null || addrs.length == 0)	throw new UnknownHostException(hostName + " is not valid");
			if (port == 0) throw new IOException("port 0 invalid");
			
			IoSocket client = null;
			if (addrs.length == 1){
				InetSocketAddress isa = new InetSocketAddress(addrs[0], port);
				client = new IoSimpleSocket(isa);
				client = new IoFlowSecuritySocket(client, isa.toString(), null);
			}else {
				IoSocket[] clients = new IoSocket[addrs.length];
				for(int i=0;i<clients.length;++i){
					InetSocketAddress isa = new InetSocketAddress(addrs[i], port);
					clients[i] = new IoSimpleSocket(isa);
					clients[i] = new IoFlowSecuritySocket(clients[i], isa.toString(), null);
				}
				
				// wrap layout
				client = new IoFailoverSocket(clients, 1);
			}
			return client;
		} catch (UnknownHostException e) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.createSimpleIoSocket] unknown host. hostName="+hostName, e);
		} catch (IOException e) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.createSimpleIoSocket] exception. hostName="+hostName, e);
		}
		return null;
	}
	

	/**
	 * 
	 * @param hostKey like zhishi.address.test or wenwen.soso.com:80
	 * @return
	 */
	protected IoSocket createIoSocket(String hostKey){
		if (!hostKey.startsWith(ZHISHI_ADDRESS)){
			String[] ss = hostKey.split(":");
			if (ss.length == 2){
				try {
					String hostName = ss[0];
					int port = Integer.parseInt(ss[1]);
					return createSimpleIoSocket(hostName, port);
				}catch(NumberFormatException e){
					
				}
			}
			
			log.error("[AddressFactory.createIoSocket] hostKey is invalid. hostKey="+hostKey);
			return null;
		}
		String prefix = hostKey + DELIM;
		String hostName = props.getProperty(hostKey);
		try {
			// make IoSocket array
			Host h = createHost(hostName);
			InetAddress[] addrs = h.getInetAddresses();
			int port = ps.getParamaterAsInt(prefix, PORT);
			if (addrs == null || addrs.length == 0)	throw new UnknownHostException(hostName + " is not valid");
			if (port == 0) throw new IOException("port 0 invalid");
			
			IoSocket client = null;
			if (addrs.length == 1){
				InetSocketAddress isa = new InetSocketAddress(addrs[0], port);
				client =  new IoSimpleSocket(isa);
				client =  new IoFlowSecuritySocket(client, isa.toString(), null);
			}else {
				IoSocket[] clients = new IoSocket[addrs.length];
				for(int i=0;i<clients.length;++i){
					InetSocketAddress isa = new InetSocketAddress(addrs[i], port);
					clients[i] = new IoSimpleSocket(isa);
					clients[i] = new IoFlowSecuritySocket(clients[i], isa.toString(), null);
				}
				
				// wrap layout
				String layout = ps.getParamaterAsString(prefix, LAYOUT, FAILOVER);
				int tries = ps.getParamaterAsInt(prefix, LAYOUT_RETIES);
				int ttb = ps.getParamaterAsInt(prefix, LAYOUT_TTB, 1000);
				if (FAILOVER.equalsIgnoreCase(layout)){
					client = new IoFailoverSocket(clients, tries);
				}else if (FAILBACK.equalsIgnoreCase(layout)){
					client = new IoFailbackSocket(clients, ttb, tries);
				}else if (DUAL.equalsIgnoreCase(layout)){
					client = new IoDualSocket(clients);
				}else {
					client = new IoFailoverSocket(clients, tries);
				}
			}
			return client;
		} catch (UnknownHostException e) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.createIoSocket] unknown host. hostKey="+hostKey, e);
		} catch (IOException e) {
			if(log.isEnabledFor(Level.ERROR))
				log.error("[AddressFactory.createIoSocket] exception. hostKey="+hostKey, e);
		}
		return null;
	}
	
	/**
	 * 
	 * @param hostKey like zhishi.address.test or wenwen.soso.com:80
	 * @return
	 */
	protected IoSocketProxy createSocketProxy(String hostKey){
		IoSocketProxy proxy = clients.get(hostKey);
		if (proxy == null){
			// get the host
			String hostName = hostKey;
			int port = 0;
			if (hostKey.startsWith(ZHISHI_ADDRESS)){
				hostName = props.getProperty(hostKey);
				String prefix = hostKey + DELIM;
				port = ps.getParamaterAsInt(prefix, PORT);
			} else {
				String[] ss = hostKey.split(":");
				if (ss.length == 2){
					try {
						hostName = ss[0];
						port = Integer.parseInt(ss[1]);
					}catch(NumberFormatException e){
						
					}
				}
			}
			if(port == 0) {
				if(log.isEnabledFor(Level.ERROR))
					log.error("[AddressFactory.createSocketProxy] hostKey is invalid, port is 0. hostKey="+hostKey);
				return null;
			}
			Host h =  createHost(hostName);
			if (h == null) return null;
			
			// new proxy
			proxy = new IoHostSocket(h);
			
			// put in list
			clients.put(hostKey, proxy);
			Map<Integer,String> hads = hostAddrs.get(hostName);
			if (hads == null){
				hads = new TreeMap<Integer,String>();
				hostAddrs.put(hostName, hads);
			}
			hads.put(port, hostKey);
		}
		return proxy;
	}
}
