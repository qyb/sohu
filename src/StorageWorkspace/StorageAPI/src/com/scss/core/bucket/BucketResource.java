/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.bucket;

import com.scss.Operation;
import com.scss.OperationResult;
import com.scss.Resource;
import com.scss.core.APIResponse;
import com.scss.core.ErrorResponse;

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
//			result.HttpStatus = err_resp.getHttp_status();
//			result.ErrorCode = err_resp.getCode();
//			result.ErrorMessage = err_resp.getMessage();
			result.Value = err_resp;
			
		} else {
			
			// Passed access check. Invoke API.
			APIResponse resp = (APIResponse) op.Operator.Invoke(op.Request);
		
			if (ErrorResponse.class.isInstance(resp)) {
				ErrorResponse err_resp = (ErrorResponse) resp;
				result.Succeed = false;
//				result.ErrorCode = err_resp.getCode();
//				result.ErrorMessage = err_resp.getMessage();
			} else 
				result.Succeed = true;
		
			result.Value = resp;
		}
		
		
		
		return result;
	}

}
