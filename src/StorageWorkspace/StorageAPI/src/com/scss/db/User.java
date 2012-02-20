/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.db;

import com.scss.IAccessor;
import com.scss.db.model.ScssUser;

/**
 * @author Samuel
 *
 */
public class User extends ScssUser implements IAccessor {
	// TODO: re-orgnize the default users.
	public final static User EveryOne = getEveryOne();
	
	protected static User getEveryOne() {
		User user = new User("EveryOne");
		user.setId((long)1); // Long or Int ?
		return user;
	}
	
	protected String name;
	private String access_key;
	private String secret_key;
	
	public User(String name) {
		this.name = name;
	}
}
