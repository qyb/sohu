package com.bladefs.client.util;

import java.io.IOException;
import java.util.Properties;

import com.bladefs.client.exception.BladeFSException;

public class BladeFSConfigFactory {
	private static BladeFSConfig  config= null;
	
	public static void createBladeFSConfig(Properties p) throws BladeFSException {
		if (config == null) {
			synchronized(BladeFSConfigFactory.class) {
				if (config == null) {
					config = new BladeFSConfig(p);
				}
			}
		}
	}
	
	public static void createBladeFSConfig(String fileName) throws IOException, BladeFSException{
		if(config == null){
			synchronized(BladeFSConfigFactory.class){
				if(config == null){
					config = new BladeFSConfig(fileName);
				}
			}
		}
	}
	
	public static BladeFSConfig getBladeFSConfig() throws BladeFSException{
		if(config == null){
			throw new BladeFSException("congfig not create");
		}
		return config;
	}
}
