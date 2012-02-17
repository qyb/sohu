/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.db;

import com.bfsapi.IAccessor;

/**
 * @author Samuel
 *
 */
public class User implements IAccessor {
	public static User EveryOne = new User("EveryOne");
	
	protected String name;
	private String access_key;
	private String secrit_key;
	
	public User(String name) {
		this.name = name;
	}
}
