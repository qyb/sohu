package com.bladefs.client.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.bladefs.client.exception.BladeFSException;
import com.bladefs.client.util.constants.BladeFSConfigConstants;

public class BladeFSConfig {
	private String fileName = null;
	private List<IPPort> nameServiceIps = new ArrayList<IPPort>();
	
	private long nameserviceConnCount = 0;
	private long nameserviceConnTimeout = 0;
	private long nameserviceReadTimeout = 0;
	private long nameserviceTryTime = 0;
	private long nameserviceCheckPeriod = 0;
	
	private long dataserviceConnCount = 0;
	private long dataserviceConnTimeout = 0;
	private long dataserviceReadTimeout = 0;
	private long dataserviceTryTime = 0;
	private long dataserviceRepCount = 0;
	private long dataserviceMaxFileSize = 16 * 1024 * 1024;
	
	public BladeFSConfig(Properties p) throws BladeFSException {
		parse(p);
	}
	
	public BladeFSConfig(String fileName) throws IOException, BladeFSException{
		this.fileName = fileName;
		parseFile(this.fileName);
	}
	
	private void parseFile(String fileName) throws IOException, BladeFSException {
		Properties pp = new Properties();
		FileInputStream fs = new FileInputStream(fileName);
		pp.load(fs);
		fs.close();
		
		parse(pp);
	}
	
	private void parse(Properties pp) throws BladeFSException{
		parseIps(pp, BladeFSConfigConstants.NAME_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.IPS, this.nameServiceIps);
		this.nameserviceConnCount = parseLong(pp, BladeFSConfigConstants.NAME_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.CONN_COUNT, 3, 0, Long.MAX_VALUE);
		this.nameserviceConnTimeout = parseLong(pp, BladeFSConfigConstants.NAME_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.CONN_TIMEOUT, 3000, 0, Long.MAX_VALUE);
		this.nameserviceReadTimeout = parseLong(pp, BladeFSConfigConstants.NAME_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.READ_TIMEOUT, 3000, 0, Long.MAX_VALUE);
		this.nameserviceCheckPeriod = parseLong(pp, BladeFSConfigConstants.NAME_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.CHECK_PERIOD, 10000, 0, Long.MAX_VALUE);
		this.nameserviceTryTime = parseLong(pp, BladeFSConfigConstants.NAME_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.TRY_TIME, 1000, 0, Long.MAX_VALUE);
		
		this.dataserviceConnCount = parseLong(pp, BladeFSConfigConstants.DATA_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.CONN_COUNT, 3, 0, Long.MAX_VALUE);
		this.dataserviceConnTimeout = parseLong(pp, BladeFSConfigConstants.DATA_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.CONN_TIMEOUT, 3000, 0, Long.MAX_VALUE);
		this.dataserviceReadTimeout = parseLong(pp, BladeFSConfigConstants.DATA_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.READ_TIMEOUT, 3000, 0, Long.MAX_VALUE);
		this.dataserviceRepCount = parseLong(pp, BladeFSConfigConstants.DATA_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.REP_COUNT, 3, 0, Long.MAX_VALUE);
		this.dataserviceTryTime = parseLong(pp, BladeFSConfigConstants.DATA_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.TRY_TIME, 1000, 0, Long.MAX_VALUE);
		this.dataserviceMaxFileSize = parseLong(pp, BladeFSConfigConstants.DATA_SERVICE + BladeFSConfigConstants.DOT + BladeFSConfigConstants.MAX_FILE_SIZE, 16 * 1024 *1024, 0, Long.MAX_VALUE);
	}
	
	private long parseLong(Properties pp, String key, long def, long min, long max){
		long ret = def;
		try{
			if(pp.containsKey(key)){
				ret = Long.parseLong(pp.getProperty(key));
			}
		}
		catch(Exception e){
			return def;
		}
		if(ret > max || ret < min){
			return def;
		}
		return ret;
	}
	
	private void parseIps(Properties pp, String key, List<IPPort> ips) throws BladeFSException{
		if(key == null){
			return;
		}
		String sips = pp.getProperty(key);
		if(sips == null){
			throw new BladeFSException(key + " is empty.");
		}
		String[] s = sips.split(";");
		if(s.length > 0){
			for(int i = 0; i < s.length; i++){
				if(s[i] != null && (!s[i].trim().equals(""))){
					String[] ipport = s[i].split(":");
					if(ipport.length != 2){
						continue;
					}
					int port = 0;
					try{
						port = Integer.parseInt(ipport[1].trim());
					}catch(Exception e){
						continue;
					}
					if(port < 0 || port > 65535){
						continue;
					}
					ips.add(new IPPort(ipport[0].trim(), port));
				}
			}
		}
		
		if(ips.size() == 0){
			throw new BladeFSException(key + " is empty.");
		}
	}

	public String getFileName() {
		return fileName;
	}

	public List<IPPort> getNameServiceIps() {
		return nameServiceIps;
	}

	public long getNameserviceConnCount() {
		return nameserviceConnCount;
	}

	public long getNameserviceConnTimeout() {
		return nameserviceConnTimeout;
	}


	public long getNameserviceReadTimeout() {
		return nameserviceReadTimeout;
	}


	public long getNameserviceCheckPeriod() {
		return nameserviceCheckPeriod;
	}
	
	
	public long getNameserviceTryTime() {
		return nameserviceTryTime;
	}

	public long getDataserviceConnCount() {
		return dataserviceConnCount;
	}

	public long getDataserviceConnTimeout() {
		return dataserviceConnTimeout;
	}

	public long getDataserviceReadTimeout() {
		return dataserviceReadTimeout;
	}

	public long getDataserviceTryTime() {
		return dataserviceTryTime;
	}

	public long getDataserviceRepCount() {
		return dataserviceRepCount;
	}
	
	public long getDataserviceMaxFileSize() {
		return dataserviceMaxFileSize;
	}

	@Override
	public String toString() {
		return "BladeFSConfig [fileName=" + fileName 
				+ ", nameServiceIps=" + nameServiceIps
				+ ", nameserviceConnCount=" + nameserviceConnCount
				+ ", nameserviceConnTimeout=" + nameserviceConnTimeout
				+ ", nameserviceReadTimeout=" + nameserviceReadTimeout
				+ ", nameserviceTryTime=" + nameserviceTryTime
				+ ", nameserviceCheckPeriod=" + nameserviceCheckPeriod
				+ ", dataserviceConnCount=" + dataserviceConnCount
				+ ", dataserviceConnTimeout=" + dataserviceConnTimeout
				+ ", dataserviceReadTimeout=" + dataserviceReadTimeout
				+ ", dataserviceTryTime=" + dataserviceTryTime
				+ ", dataserviceRepCount=" + dataserviceRepCount + "]";
	}
}
