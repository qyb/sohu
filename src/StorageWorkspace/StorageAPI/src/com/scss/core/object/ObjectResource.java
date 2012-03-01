/**
 * Copyright Sohu Inc. 2012
 */
package com.scss.core.object;

import com.scss.Operation;
import com.scss.OperationResult;
import com.scss.Resource;
import com.scss.core.APIResponse;
import com.scss.core.ErrorResponse;

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
	 * @see com.scss.IAccessable#CanAccess(com.scss.Operation)
	 */
	@Override
	public Boolean CanAccess(Operation op) {
		// TODO Auto-generated method stub
		return true;
	}


	/* (non-Javadoc)
	 * @see com.scss.IOperatable#Operate(com.scss.Operation)
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
