/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server.object;

import com.bfsapi.IAccessor;
import com.bfsapi.Operation;
import com.bfsapi.OperationResult;
import com.bfsapi.Resource;
import com.bfsapi.db.Object;

/**
 * @author Samuel
 *
 */
public class ObjectResource extends Resource {
	protected String key;
	protected String bucket_name;
	
	public ObjectResource (String key, String bucket_name){
		this.key = key;
		this.bucket_name = bucket_name;
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.IAccessable#CanRead(com.bfsapi.IAccessor)
	 */
	@Override
	public Boolean CanRead(IAccessor who) {
		return false;
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.IOperatable#Operate(com.bfsapi.Operation)
	 */
	@Override
	public OperationResult Operate(Operation op) {
		// TODO Auto-generated method stub
		return null;
	}
}
