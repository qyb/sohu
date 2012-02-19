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
	 * @see com.bfsapi.IAccessable#CanAccess(com.bfsapi.Operation)
	 */
	@Override
	public Boolean CanAccess(Operation op) {
		// TODO Auto-generated method stub
		return null;
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
