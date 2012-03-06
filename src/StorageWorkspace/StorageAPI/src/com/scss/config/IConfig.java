/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.config;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * Configuration interface.
 * 
 * @author Samuel
 *
 */
public interface IConfig {
	void parse(XMLConfiguration xmlconf) throws ConfigException;
	String get(String name);
	void put(String name, String value);
}
