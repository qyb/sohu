package com.bladefs.client.util.io.socket;

import java.util.Enumeration;
import java.util.Properties;

public class PropertySelector {
	protected static final String DELIM = ".";
	protected Properties props; 
	
	public PropertySelector(){
		this.props = new Properties();
	}
	public PropertySelector(Properties props){
		this.props = props;
	}
	
	/**
	 * @param prefix like "zhishi.dao.user." 
	 * @param key like "CONN_NUM"
	 */
	protected String getParamater(String prefix, String key){
		String value = props.getProperty(prefix + key);
		if (value != null && value.trim().length() != 0) return value.trim();
		
//		int fromIndex = prefix.length()-1;
//		int index = -1;
//		while((index=prefix.lastIndexOf(DELIM, fromIndex))>0){
//			String pre = prefix.substring(0, index + 1);
//			value = props.getProperty(pre + key);
//			if (value != null) return value;
//			fromIndex = index - 1;
//		}
		
		return null;
	}
	
	protected String getProperty(String key){
		String value = props.getProperty(key);
		if (value != null && value.trim().length() != 0) return value.trim();
		return null;
	}
	
	public int getPropertyAsInt(String key, int defaultValue) {
		int value = defaultValue;
		String v = getProperty(key);
		if(v != null) {
			try { value = Integer.parseInt(v); } catch(Exception e) { e.printStackTrace(); }
		}
		return value;
	}
	
	public boolean getPropertyAsBool(String key, boolean defaultValue) {
		boolean value = defaultValue;
		String v = getProperty(key);
		if(v != null) {
			if (v.equals("1") || v.equalsIgnoreCase("true"))value = true;
			else value = false;
		}
		return value;
	}

	public String getPropertyAsString(String key, String defaultValue) {
		String v = getProperty(key);
		if (v != null) return v;
		else return defaultValue;
	}
	
	public long getPropertyAsLong(String key, long defaultValue) {
		long value = defaultValue;
		String v = getProperty(key);
		if(v != null) {
			try { value = Long.parseLong(v); } catch(Exception e) { e.printStackTrace(); }
		}
		return value;
	}
	
	public int getParamaterAsInt(String prefix, String key, int defaultValue) {
		int value = defaultValue;
		String v = getParamater(prefix, key);
		if(v != null) {
			try { value = Integer.parseInt(v); } catch(Exception e) { e.printStackTrace(); }
		}
		return value;
	}
	
	public boolean getParamaterAsBool(String prefix, String key, boolean defaultValue) {
		boolean value = defaultValue;
		String v = getParamater(prefix, key);
		if(v != null) {
			if (v.equals("1") || v.equalsIgnoreCase("true"))value = true;
			else value = false;
		}
		return value;
	}

	public String getParamaterAsString(String prefix, String key, String defaultValue) {
		String v = getParamater(prefix, key);
		if (v != null) return v;
		else return defaultValue;
	}
	
	public long getParamaterAsLong(String prefix, String key, long defaultValue) {
		long value = defaultValue;
		String v = getParamater(prefix, key);
		if(v != null) {
			try { value = Long.parseLong(v); } catch(Exception e) { e.printStackTrace(); }
		}
		return value;
	}
	
	public int getPropertyAsInt(String key) { return getPropertyAsInt(key, 0); }
	public boolean getPropertyAsBool(String key) { return getPropertyAsBool(key, false); }
	public String getPropertyAsString(String key) { return getProperty(key); }
	public long getPropertyAsLong(String key) { return getPropertyAsLong(key, 0L); }
	
	public int getParamaterAsInt(String prefix, String key) { return getParamaterAsInt(prefix, key, 0); }
	public boolean getParamaterAsBool(String prefix, String key) { return getParamaterAsBool(prefix, key, false); }
	public String getParamaterAsString(String prefix, String key) { return getParamater(prefix, key); }
	public long getParamaterAsLong(String prefix, String key) { return getParamaterAsLong(prefix, key, 0L); }

	public Enumeration<?> propertyNames() {
		return this.props.propertyNames();
	}
}
