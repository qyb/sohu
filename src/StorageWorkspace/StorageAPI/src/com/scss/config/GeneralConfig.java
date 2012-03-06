/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * @author Samuel
 *
 */
@ConfigFilePath("config.xml")
public class GeneralConfig implements IConfig {
	
	private Map<String, String> fields = new HashMap<String, String>();

	@Override
	public void parse(XMLConfiguration xmlconf) throws ConfigException {
		fields.put("domain", xmlconf.getString("domain"));
		fields.put("port", xmlconf.getString("port"));
		fields.put("xmlns", xmlconf.getString("xmlns"));
	}
	
	@Override
	public String get(String name) {
		return fields.get(name);
	}

	@Override
	public void put(String name, String value) {
		if (fields.containsKey(name))
			fields.put(name, value);
		else
			throw new ConfigException(String.format("%s is not an available config field name", name));
	}
	
	/**
	 * The domain we are hosting for.
	 * 
	 * @return
	 */
	public String getDomain() {
		return fields.get("domain");
	}
	
	private int port = 0;
	public int getPort() {
		if (port < 0)
			port = Integer.parseInt(fields.get("port"));
		return port;
	}

	public String getXmlNamespace() {
		return fields.get("xmlns");
	}
}
