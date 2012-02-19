/**
 * Copyright Sohu Inc. 2012
 */
package com.bfsapi.server.bucket;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

import com.bfsapi.IAccessor;
import com.bfsapi.Operation;
import com.bfsapi.OperationResult;
import com.bfsapi.Resource;
import com.bfsapi.db.Bucket;
import com.bfsapi.server.APIResponse;
import com.bfsapi.server.APIResponseHeader;
import com.bfsapi.server.CommonRequestHeader;
import com.bfsapi.server.CommonResponseHeader;
import com.bfsapi.server.Handler;
import com.bfsapi.server.MediaTypes;

import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;

/**
 * @author Samuel
 *
 */
public class BucketResource extends Resource {
	protected String bucket;
	
	public BucketResource(String bucket) {
		this.bucket = bucket;
	}

	/* (non-Javadoc)
	 * @see com.bfsapi.IAccessable#CanAccess(com.bfsapi.Operation)
	 */
	@Override
	public Boolean CanAccess(Operation op) {
		assert(null != op);
		assert(null != op.Request);
		assert(null != op.Performer);
		
		// Route to check access per API
		return op.Operator.CanInvoke(op.Request, op.Performer);
	}
	
	/* (non-Javadoc)
	 * @see com.bfsapi.IOperatable#Operate(com.bfsapi.Operation)
	 */
	@Override
	public OperationResult Operate(Operation op) {

		assert(null != op);
		assert(null != op.Operator);
		assert(null != op.Performer);
		assert(null != op.Request);
		
		OperationResult result = new OperationResult();
		
		// Access check
		if (!this.CanAccess(op)) {
			// No Access
			ErrorResponse err_resp = ErrorResponse.AccessDenied(op.Request);
			result.Succeed = false;
			result.ErrorCode = err_resp.code;
			result.ErrorMessage = err_resp.message;
			result.Value = err_resp;
			
		} else {
			
			// Passed access check. Invoke API.
			APIResponse resp = (APIResponse) op.Operator.Invoke(op.Request);
		
			if (ErrorResponse.class.isInstance(resp)) {
				ErrorResponse err_resp = (ErrorResponse) resp;
				result.Succeed = false;
				result.ErrorCode = err_resp.code;
				result.ErrorMessage = err_resp.message;
			} else 
				result.Succeed = true;
		
			result.Value = resp;
		}
		
		
		
		return result;
	}

}
