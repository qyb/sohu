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
	public static User EveryOne = null;
	static {
		EveryOne = new User();
		EveryOne.setId((long)1);
		EveryOne.setSohuId("EveryOne");
	}

	public User() {}
	public User(ScssUser user) {
		this.setAccessKey(user.getAccessKey());
		this.setId(user.getId());
		this.setSohuId(user.getSohuId());
		this.setStatus(user.getStatus());
	}
	
}
